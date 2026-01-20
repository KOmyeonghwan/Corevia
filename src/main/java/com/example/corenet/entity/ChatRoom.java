package com.example.corenet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 1:1이면 "user1-user2"

    @Column(name = "is_group")
    private boolean isGroup; // true = 그룹채팅

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}