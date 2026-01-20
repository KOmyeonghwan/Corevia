package com.example.corenet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_room_participants")
@Getter 
@Setter
public class ChatRoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "joined_at", insertable = false, updatable = false)
    private LocalDateTime joinedAt;


    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;
}
