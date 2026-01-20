package com.example.corenet.client.message.Repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.corenet.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

        // 1:1 채팅 중복방지: 두 참가자가 이미 존재하는 방 있는지 확인
        @Query("SELECT cr FROM ChatRoom cr " +
                        "JOIN ChatRoomParticipant p1 ON cr.id = p1.roomId " +
                        "JOIN ChatRoomParticipant p2 ON cr.id = p2.roomId " +
                        "WHERE p1.userId = :u1 AND p2.userId = :u2 AND cr.isGroup = false")
        Optional<ChatRoom> findDirectRoom(@Param("u1") Integer user1, @Param("u2") Integer user2);

        @Query("SELECT cr FROM ChatRoom cr " +
                        "JOIN ChatRoomParticipant p ON cr.id = p.roomId " +
                        "WHERE p.userId = :userId")
        List<ChatRoom> findAllByUserId(Long userId);

        Optional<ChatRoom> findById(Long id);
}