package com.example.corenet.client.message.Serv;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.client.message.Repo.ChatMessageRepository;
import com.example.corenet.client.message.Repo.ChatRoomParticipantRepository;
import com.example.corenet.client.message.Repo.ChatRoomRepository;
import com.example.corenet.common.dto.ChatRoomDTO;

import com.example.corenet.entity.ChatRoom;
import com.example.corenet.entity.User;

import jakarta.transaction.Transactional;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomParticipantRepository participantRepository;

    @Autowired
    private ChatMessageRepository ChatMessageRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<ChatRoomDTO> getChatRooms(Long currentUserId) {

        // 참여 방 ID 가져오기
        List<Long> roomIds = participantRepository.findByUserId(currentUserId)
                .stream()
                .map(p -> p.getRoomId())
                .toList();
        if (roomIds.isEmpty())
            return List.of();

        // 방 정보 가져오기
        List<ChatRoom> rooms = chatRoomRepository.findAllById(roomIds);

        // DTO 변환
        return rooms.stream().map(room -> {
            List<User> users = usersRepository.findByChatRoomId(room.getId());
            String roomName;

            if (room.isGroup()) {
                // 그룹 채팅: 현재 사용자 제외 이름 나열
                roomName = users.stream()
                        .filter(u -> !u.getId().equals(currentUserId))
                        .map(User::getUserName)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("알 수 없음");
            } else {
                // 1:1 채팅: 상대방 이름
                User otherUser = users.stream()
                        .filter(u -> !u.getId().equals(currentUserId))
                        .findFirst()
                        .orElse(null);
                roomName = otherUser != null ? otherUser.getUserName() : "알 수 없음";
            }

            int participantCount = users.size();
            String lastMessage = ""; // 필요 시 메시지 테이블에서 가져오기

            return new ChatRoomDTO(room.getId(), roomName, participantCount, lastMessage, room.isGroup());
        }).toList();
    }

    // 채팅방 삭제
    @Transactional
    public boolean deleteChatRoom(Long roomId, Long userId) {
        // 채팅방 존재 여부 확인
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (!roomOpt.isPresent()) {
            System.out.println("채팅방이 존재하지 않습니다.");
            return false;
        }

        ChatRoom room = roomOpt.get();

        // 사용자가 해당 채팅방 참가자인지 확인
        boolean isParticipant = participantRepository.existsByRoomIdAndUserId(roomId, userId);
        if (!isParticipant) {
            System.out.println("사용자가 채팅방 참가자가 아닙니다.");
            return false;
        }

        // 채팅방에 속한 메시지 삭제 (명시적으로)
        ChatMessageRepository.deleteById(roomId);
        System.out.println("메시지 삭제 완료");

        // 채팅방 참가자 삭제
        participantRepository.deleteByRoomIdAndUserId(roomId, userId);
        System.out.println("참가자 삭제 완료");

        // 참가자가 모두 나갔으면 채팅방 삭제
        long participantCount = participantRepository.countByRoomId(roomId);
        System.out.println("채팅방 참가자 수: " + participantCount);

        if (participantCount == 0) {
            chatRoomRepository.delete(room);
            System.out.println("채팅방 삭제 완료");
            return true;
        }

        return false; // 참가자가 남아있으면 삭제되지 않음
    }

}
