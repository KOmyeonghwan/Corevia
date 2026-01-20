package com.example.corenet.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomDTO {

    private Long id;
    private String roomName; 
    private int participantCount;
    private String lastMessage;
    private boolean isGroup;
}
