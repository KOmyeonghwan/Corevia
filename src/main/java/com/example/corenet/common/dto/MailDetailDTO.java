package com.example.corenet.common.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MailDetailDTO {
    private Long id;
    private String senderEmail;
    private String senderName;
    private String recipientEmail;
    private String title;
    private String description;
    private String attachmentPath;
    private String status;
    private LocalDateTime createdAt;
}