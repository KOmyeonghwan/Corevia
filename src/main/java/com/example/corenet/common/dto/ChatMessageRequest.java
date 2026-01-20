package com.example.corenet.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {
    private Long roomId;
    private String message;
}