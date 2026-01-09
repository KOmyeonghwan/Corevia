package com.example.corenet.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardAdContentDTO {
    private Long id;
    private String title;
    private String boardCode;
    private String author; // board_boardCode의 user_name
    private int views;

}