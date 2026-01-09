package com.example.corenet.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받을 사용자
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // 알림을 발생시킨 사용자 (없을 수도 있음)
    @Column(name = "sender_id")
    private Integer senderId;

    // 알림 타입
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // 참조 ID (게시글 ID, 댓글 ID, 일정 ID 등)
    @Column(name = "reference_id")
    private Long referenceId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 읽음 여부
    @Column(name = "is_read")
    private boolean read = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    // 생성 시 자동 세팅
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 읽음 처리
    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }

}
    
