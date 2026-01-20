package com.example.corenet.client.message.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.corenet.entity.ChatRoomParticipant;

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    List<ChatRoomParticipant> findByRoomId(Long roomId);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    // 로그인 사용자가 참여한 방(roomId) 조회
    List<ChatRoomParticipant> findByUserId(Long userId);

    void deleteByRoomIdAndUserId(Long roomId, Long userId);

    long countByRoomId(Long roomId); // 채팅방 참가자 수 확인
}