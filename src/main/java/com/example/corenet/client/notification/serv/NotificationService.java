package com.example.corenet.client.notification.serv;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.corenet.client.notification.dto.NotificationDTO;
import com.example.corenet.client.notification.repo.NotificationRepository;
import com.example.corenet.entity.Notification;
import com.example.corenet.entity.NotificationType;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 조회
    public List<Notification> getNotificationsByUser(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    // @Transactional(readOnly = true)
    // public List<Notification> getNotificationsByUser(Long userId) {
    //     return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    // }

    // 생성
    public Notification create(Integer userId, Integer senderId, NotificationType type,
                               Long referenceId, String title, String content) {
        Notification notification = new Notification(); 
        notification.setUserId(userId);
        notification.setSenderId(senderId);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRead(false); 
        // createdAt는 @PrePersist에서 자동 세팅
        return notificationRepository.save(notification);
    }
    
    
    // 삭제
    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }

    // 로그인 사용자, 보낸 사용자로 삭제
    public void deleteByUserIdAndSenderId(Long userId,Long senderId){
        notificationRepository.deleteByUserIdAndSenderId(userId, senderId);
    }

    // 모두 삭제
    public void deleteAll() {
        notificationRepository.deleteAll();
    }

    // 읽음 처리
    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다. id=" + id));
        n.setRead(true);
        notificationRepository.save(n);
    }

    public List<NotificationDTO> getMainNotices(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(n -> new NotificationDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getContent(),
                        n.getReferenceId(), // 엔티티에 referenceId가 있어야 함
                        n.isRead(),
                        n.getType().name(), // enum → String
                        n.getCreatedAt()))
                .toList();
    }
}