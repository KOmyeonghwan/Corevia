package com.example.corenet.client.notification.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.corenet.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

        // userId와 senderId에 맞는 Notification을 삭제하는 메서드 추가
        void deleteByUserIdAndSenderId(Long userId, Long senderId);
}
