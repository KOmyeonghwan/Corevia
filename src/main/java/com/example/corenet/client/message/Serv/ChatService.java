package com.example.corenet.client.message.Serv;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.client.message.Repo.ChatRoomParticipantRepository;
import com.example.corenet.client.message.Repo.ChatRoomRepository;
import com.example.corenet.client.notification.serv.NotificationService;
import com.example.corenet.client.message.Repo.ChatMessageRepository;
import com.example.corenet.entity.ChatRoom;
import com.example.corenet.entity.ChatRoomParticipant;
import com.example.corenet.entity.NotificationType;

import jakarta.transaction.Transactional;

import com.example.corenet.entity.ChatMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;
    private final UsersRepository usersRepository;
    private final NotificationService notificationService;

    /** 채팅방 생성 */
    public ChatRoom createChatRoom(List<Integer> userIds, Long loginUserId) {
        if (!userIds.contains(loginUserId.intValue())) {
            userIds.add(loginUserId.intValue()); // 로그인 사용자 자동 추가
        }

        // 1:1 채팅 체크
        if (userIds.size() == 2) {
            Integer u1 = userIds.get(0);
            Integer u2 = userIds.get(1);
            Optional<ChatRoom> exist = chatRoomRepository.findDirectRoom(u1, u2);
            if (exist.isPresent()) {
                return exist.get();
            }
        }

        ChatRoom room = new ChatRoom();
        room.setGroup(userIds.size() > 2);

        List<String> names = usersRepository.findAllById(userIds)
                .stream()
                .map(u -> u.getUserName())
                .collect(Collectors.toList());

        if (userIds.size() == 2) {
            room.setName(String.join(" - ", names));
        } else {
            room.setName(String.join(", ", names));
        }

        chatRoomRepository.save(room);

        for (Integer uid : userIds) {
            ChatRoomParticipant p = new ChatRoomParticipant();
            p.setRoomId(room.getId());
            p.setUserId(uid.longValue());
            participantRepository.save(p);
        }

        return room;
    }

    /** 메시지 저장 */
    public ChatMessage saveMessage(ChatMessage message) {
        return messageRepository.save(message);
    }

    /** 특정 방 메시지 전체 조회 */
    public List<ChatMessage> getMessages(Long roomId) {
        return messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll(); // 단순 예시
    }

    @Transactional
    public ChatMessage sendMessage(Long roomId, Long senderId, String text) {

        if (!participantRepository.existsByRoomIdAndUserId(roomId, senderId)) {
            ChatRoomParticipant p = new ChatRoomParticipant();
            p.setRoomId(roomId);
            p.setUserId(senderId);
            participantRepository.save(p);
        }

        // 1️⃣ 메시지 저장
        ChatMessage msg = new ChatMessage();
        msg.setRoomId(roomId);
        msg.setSenderId(senderId);
        msg.setMessage(text);
        messageRepository.save(msg);

        // 2️⃣ 방 참여자 조회
        List<ChatRoomParticipant> participants = participantRepository.findByRoomId(roomId);

        // 3️⃣ 참여자들에게 알림 생성 (본인 제외)
        for (ChatRoomParticipant p : participants) {
            if (p.getUserId().equals(senderId))
                continue;

            notificationService.create(
                    p.getUserId().intValue(), // 받는 사람
                    senderId.intValue(), // 보낸 사람
                    NotificationType.message, // 메신저 알림
                    msg.getId(), // 메시지 ID
                    "새 메시지",
                    "새 채팅 메시지가 도착했습니다.");
        }

        return msg;
    }

}