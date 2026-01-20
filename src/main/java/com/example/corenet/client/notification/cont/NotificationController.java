package com.example.corenet.client.notification.cont;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.corenet.client.notification.dto.NotificationDTO;
import com.example.corenet.client.notification.serv.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping
    public List<NotificationDTO> getMyNotifications(@RequestParam("userId") Integer userId) {
        return notificationService.getNotificationsByUser(userId).stream()
                .map(n -> new NotificationDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getContent(),
                        n.getReferenceId(),
                        n.isRead(),
                        n.getType().name(),
                        n.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // 알림 읽음 처리
    @PostMapping("/{id}/read")
    public ResponseEntity<?> readNotification(@PathVariable("id") Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok("읽음 처리 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("읽음 처리 실패: " + e.getMessage());
        }
    }

    // 알림 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable("id") Long id) {
        System.out.println("DELETE notifications id = " + id);
        try {
            notificationService.delete(id);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
    }

    // 로그인 사용자, 보낸 사용자로 삭제
    @DeleteMapping("/{userId}/{senderId}")
    public ResponseEntity<?> deleteNotification(@PathVariable("userId") Long userId, @PathVariable("senderId") Long senderId) {
        try {
            notificationService.deleteByUserIdAndSenderId(userId, senderId);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
    }

    // 모든 알림 삭제
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllNotifications() {
        try {
            notificationService.deleteAll();
            return ResponseEntity.ok("모든 알림이 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("모든 알림 삭제 실패: " + e.getMessage());
        }
    }
}