package com.example.corenet.client.mail.Serv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.client.mail.Repo.MailRepository;
import com.example.corenet.client.notification.serv.NotificationService;
import com.example.corenet.common.dto.MailDetailDTO;
import com.example.corenet.common.dto.MailListDTO;
import com.example.corenet.common.dto.MailSendDTO;
import com.example.corenet.entity.Mail;
import com.example.corenet.entity.MailStatus;
import com.example.corenet.entity.NotificationType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

        private final MailRepository mailRepository;
        private final NotificationService notificationService;
        private final UsersRepository usersRepository;

        // 메일 보내기
        public void sendMail(String senderCompanyEmail, String senderName, MailSendDTO dto, String attachmentPath) {

                Mail mail = Mail.builder()
                                .senderEmail(senderCompanyEmail)
                                .senderName(senderName)
                                .recipientEmail(dto.getRecipientEmail())
                                .title(dto.getTitle())
                                .description(dto.getDescription())
                                .status(MailStatus.sent)
                                .attachmentPath(attachmentPath)
                                .createdAt(LocalDateTime.now())
                                .build();

                mailRepository.save(mail);

                // ✅ 수신자 userId 조회
                Integer receiverUserId = usersRepository
                                .findByCompanyEmail(dto.getRecipientEmail())
                                .orElseThrow(() -> new IllegalArgumentException("수신자 없음"))
                                .getId();

                // ✅ 메일 알림 생성
                notificationService.create(
                                receiverUserId, // ✅ 받는 사람
                                null,
                                NotificationType.mail,
                                mail.getId(), // 메일 ID
                                "새 메일 도착",
                                senderName + "님으로부터 메일이 도착했습니다.");
        }

        // 받은 메일함
        public List<MailListDTO> getInbox(String email) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return mailRepository.findByRecipientEmailOrderByCreatedAtDesc(email)
                                .stream()
                                .map(mail -> MailListDTO.builder()
                                                .id(mail.getId())
                                                .senderName(mail.getSenderName())
                                                .title(mail.getTitle())
                                                .status(mail.getStatus().name())
                                                .createdAt(mail.getCreatedAt().format(formatter))
                                                .build())
                                .toList();
        }

        // 메일 상세 + 읽음 처리
        @Transactional
        public MailDetailDTO getMailDetail(Long mailId, String loginEmail) {

                Mail mail = mailRepository.findById(mailId)
                                .orElseThrow(() -> new IllegalArgumentException("메일 없음"));

                // ✅ 권한 체크 (받은 사람 or 보낸 사람)
                if (!mail.getRecipientEmail().equals(loginEmail)
                                && !mail.getSenderEmail().equals(loginEmail)) {
                        throw new SecurityException("권한 없음");
                }

                // ✅ 읽음 처리는 받은 메일만
                if (mail.getRecipientEmail().equals(loginEmail)
                                && mail.getStatus() == MailStatus.sent) {
                        mail.setStatus(MailStatus.read);
                }

                return MailDetailDTO.builder()
                                .id(mail.getId())
                                .senderEmail(mail.getSenderEmail())
                                .senderName(mail.getSenderName())
                                .recipientEmail(mail.getRecipientEmail())
                                .title(mail.getTitle())
                                .description(mail.getDescription())
                                .attachmentPath(mail.getAttachmentPath())
                                .status(mail.getStatus().name())
                                .createdAt(mail.getCreatedAt())
                                .build();
        }

        // 보낸 메일함
        public List<MailListDTO> getSentMail(String email) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return mailRepository.findBySenderEmailOrderByCreatedAtDesc(email)
                                .stream()
                                .map(mail -> MailListDTO.builder()
                                                .id(mail.getId())
                                                // 🔥 보낸 메일함에서는 "받는 사람"이 보이게
                                                .senderName(mail.getRecipientEmail())
                                                .title(mail.getTitle())
                                                .status(mail.getStatus().name())
                                                .createdAt(mail.getCreatedAt().format(formatter))
                                                .build())
                                .toList();
        }

        @Transactional
        public void deleteMail(Long mailId, String loginEmail) {

                Mail mail = mailRepository.findById(mailId)
                                .orElseThrow(() -> new IllegalArgumentException("메일 없음"));

                // 🔒 권한 체크
                if (!mail.getRecipientEmail().equals(loginEmail)
                                && !mail.getSenderEmail().equals(loginEmail)) {
                        throw new SecurityException("권한 없음");
                }

                mailRepository.delete(mail);
        }

}