package com.example.corenet.admin.board.dto.commentDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateDTO {
    private Long commentId;   // 댓글 ID
    private String content;   // 수정된 내용
    private String status;    // 수정된 상태 (normal / hidden)
}