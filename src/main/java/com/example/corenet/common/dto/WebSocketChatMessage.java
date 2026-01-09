package com.example.corenet.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocketChatMessage {
    private Long roomId;
    private Long senderId;
    private String senderName; // 클라이언트 표시용
    private String message;
    private String type; // "ENTER", "CHAT", "LEAVE"
}