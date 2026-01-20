package com.example.corenet.admin.board.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardContentDetailDTO {

    private Long id;
    private String boardCode;
    private String title;
    private String content;

    private Integer userId;
    private String userName;

    private Integer views;
    private String fileName;
    private String fileUrl;

    private LocalDateTime createAt;

    public String getCreateAtFormatted() {
        if (createAt == null)
            return "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return createAt.format(formatter);
    }
}
