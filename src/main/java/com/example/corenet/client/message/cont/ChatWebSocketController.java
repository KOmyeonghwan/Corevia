package com.example.corenet.client.message.cont;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.corenet.client.message.Serv.ChatService;
import com.example.corenet.common.dto.WebSocketChatMessage;
import com.example.corenet.entity.ChatMessage;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    /** 메시지 받기 */
    @MessageMapping("/chat")
    @SendTo("/room/{roomId}") // /room/public 구독자에게 브로드캐스트
    public WebSocketChatMessage sendMessage(WebSocketChatMessage message) {
        // DB 저장
        ChatMessage saved = chatService.sendMessage(
                message.getRoomId(),
                message.getSenderId(),
                message.getMessage()
        );

        // 서버에서 다시 브로드캐스트용 DTO 반환
        message.setMessage(saved.getMessage());
        return message;
    }
}