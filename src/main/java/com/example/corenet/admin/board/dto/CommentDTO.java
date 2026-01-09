package com.example.corenet.admin.board.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    private Long id;
    private Long postId;
    private Integer userId;
    private String userName;
    private String content;
    private Long parentId; // null 가능
    private int depth; // 0=댓글,1=대댓글,2=2단댓글
    private LocalDateTime createAt;

    private String status; // default = normal

    public String getCreateAtFormatted() {
        if (createAt == null)
            return "";
        return createAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    // 원댓글 여부 (depth == 0)
    public boolean isRoot() {
        return depth == 0;
    }

    // 대댓글 여부 (depth > 0) 
    public boolean isNested() {
        return depth > 0;
    }

    // 상태 한글화 
    public String getStatusName() {
        if ("normal".equals(status))
            return "정상";
        if ("hidden".equals(status))
            return "숨김";
        if ("deleted".equals(status))
            return "삭제";
        return status;
    }

    private String convertDepthName(int depth) {
        switch (depth) {
            case 0: return "댓글";
            case 1: return "대댓글";
            case 2: return "대대댓글";
            default: return "알 수 없음";
        }
    }

    public String getDepthName() {
        return convertDepthName(this.depth);
    }
    
}
