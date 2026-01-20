package com.example.corenet.admin.board.dto.commentDTO;

import java.util.ArrayList;
import java.util.List;

import com.example.corenet.admin.board.dto.CommentDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentGroupDTO {
    private CommentDTO root;              // 원댓글 (depth = 0)
    private List<CommentDTO> replies = new ArrayList<>(); // 대댓글 + 대대댓글

    public boolean isStatusNormal() {
        return root != null && "normal".equals(root.getStatus());
    }

    public boolean isStatusHidden() {
        return root != null && "hidden".equals(root.getStatus());
    }

    public Long getPostId() {
        return root != null ? root.getPostId() : null;
    }
}