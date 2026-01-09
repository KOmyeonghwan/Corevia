package com.example.corenet.admin.board.cont;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.corenet.admin.board.dto.commentDTO.CommentUpdateDTO;
import com.example.corenet.admin.board.serv.CommentService;

@Controller
public class CommentController {

    @Autowired
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 삭제
    @DeleteMapping("/comment/delete/{boardCode}/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("commentId") Long commentId) {
        try {
            int deleted = commentService.deleteComment(boardCode, commentId);
            if (deleted > 0) {
                return ResponseEntity.ok("삭제 완료");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("삭제 실패: 댓글을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 실패: " + e.getMessage());
        }
    }

    // 원댓글 + 대댓글 동시에 업데이트
    @PostMapping("/comment/update/{boardCode}")
    public ResponseEntity<?> updateComments(
            @PathVariable("boardCode") String boardCode,
            @RequestBody List<CommentUpdateDTO> updates
    ) {
        try {
            commentService.updateComments(boardCode, updates);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업데이트 실패");
        }
    }

    // 뎃글 content 숨김 처리
    @PostMapping("/comment/hide/{boardCode}/{commentId}")
    public ResponseEntity<Void> hideComment(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("commentId") Long commentId
    ) {
        commentService.hideComment(boardCode, commentId);
        return ResponseEntity.ok().build();
    }
    

}
