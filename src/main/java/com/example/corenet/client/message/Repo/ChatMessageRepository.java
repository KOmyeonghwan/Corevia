package com.example.corenet.client.message.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.corenet.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = "SELECT * FROM messages WHERE room_id = :roomId ORDER BY created_at ASC", nativeQuery = true)
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(@Param("roomId") Long roomId);

    void deleteByRoomId(Long roomId);
}