package com.example.corenet.common.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailListDTO {
    private Long id;
    private String senderName;
    private String title;
    private String status;
    private String createdAt;
}