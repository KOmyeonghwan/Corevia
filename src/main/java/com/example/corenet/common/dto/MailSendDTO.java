package com.example.corenet.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailSendDTO {
    private String recipientEmail;
    private String title;
    private String description;
}