package com.example.corenet.admin.board.dto.commentDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentListDTO {
    private Long commentId;                     // comment_notice.id

    // 원 게시글 정보
    private Long postId;                 // board_notice.id
    private Integer boardUserId;         // board_notice.user_id
    private String boardCode;            // board_notice.board_code
    private String boardContent;         // board_notice.content

    // 댓글 정보
    private Long commentUserId;       // comment_notice.user_id
    private String commentUserName;      // comment_notice.user_name
    private String commentContent;       // comment_notice.content
    private Long parentId;               // comment_notice.parent_id
    private LocalDateTime commentCreateAt; // comment_notice.create_at
    private int depth;                   // comment_notice.depth
    
    private String depthName;   // 화면용

    private String convertDepthName(int depth) {
        switch (depth) {
            case 0: return "댓글";
            case 1: return "대댓글";
            case 2: return "대대댓글";
            default: return "알 수 없음";
        }
    }

    public void setDepth(int depth) {
        this.depth = depth;
        this.depthName = convertDepthName(depth);
    }

    public String getCommentCreateAt(){
        if(commentCreateAt == null) return "";
        return commentCreateAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
