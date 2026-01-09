package com.example.corenet.admin.board.serv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.corenet.admin.board.dto.CommentDTO;
import com.example.corenet.admin.board.dto.commentDTO.CommentGroupDTO;
import com.example.corenet.admin.board.dto.commentDTO.CommentListDTO;
import com.example.corenet.admin.board.dto.commentDTO.CommentUpdateDTO;
import com.example.corenet.admin.board.repo.CommentRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {
    private final JdbcTemplate jdbcTemplate;
    private final CommentRepository commentRepository;

    public CommentService(JdbcTemplate jdbcTemplate, CommentRepository commentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.commentRepository = commentRepository;
    }

    // 댓글 리스트 조회
    public List<CommentListDTO> getCommentList(
            String boardCode,
            Integer deptCode,
            String keyWordString,
            String keyWord,
            int page, // 현재 페이지
            int size // 한 페이지 글 개수
    ) {
        return commentRepository.getCommentList(boardCode, deptCode, keyWordString, keyWord, page, size);
    }

    // 댓글 상세 조회
    public List<CommentDTO> getCommentDetail(String boardCode, Long postId, Integer deptCode) {
        return commentRepository.getCommentDetail(boardCode, postId, deptCode);
    }

    // 댓글 상세 조회(원댓글과 자식 댓글 구조)
    public List<CommentGroupDTO> getCommentGroups(String boardCode, Long postId, Integer deptCode) {
        // 모든 댓글 조회 (depth 순서대로 정렬)
        List<CommentDTO> allComments = getCommentDetail(boardCode, postId, deptCode);

        // parentId 기준으로 댓글 그룹화
        Map<Long, CommentGroupDTO> groups = new LinkedHashMap<>();
        Map<Long, List<CommentDTO>> childMap = new HashMap<>();

        for (CommentDTO comment : allComments) {
            if (comment.getDepth() == 0) {
                // 원댓글
                CommentGroupDTO group = new CommentGroupDTO();
                group.setRoot(comment);
                groups.put(comment.getId(), group);
            } else {
                // 대댓글 / 대대댓글
                childMap.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>())
                        .add(comment);
            }
        }

        // 원댓글에 대댓글 붙이기 (대댓글 포함하면 depth 2도 같이 포함)
        for (CommentGroupDTO group : groups.values()) {
            attachReplies(group.getRoot(), group.getReplies(), childMap);
        }

        return new ArrayList<>(groups.values());
    }

    // 재귀적으로 자식 댓글 붙이기
    private void attachReplies(CommentDTO parent, List<CommentDTO> resultList, Map<Long, List<CommentDTO>> childMap) {
        List<CommentDTO> children = childMap.get(parent.getId());
        if (children != null) {
            for (CommentDTO child : children) {
                resultList.add(child);
                // depth 2 이상도 포함
                attachReplies(child, resultList, childMap);
            }
        }
    }

    // 댓글 삭제
    public int deleteComment(String boardCode, Long commentId) {
        return commentRepository.deleteComment(boardCode, commentId);
    }

    // 댓글 수정 (여러 댓글 업데이트)
    @Transactional
    public void updateComments(String boardCode, List<CommentUpdateDTO> updates) {
        for (CommentUpdateDTO dto : updates) {
            commentRepository.updateContentAndStatus(boardCode, dto.getCommentId(), dto.getContent(), dto.getStatus());
        }
    }

    // 댓글 숨김
    public void hideComment(String boardCode, Long commentId) {

        int updated = commentRepository.updateStatus(
                boardCode,
                commentId);

        if (updated == 0) {
            throw new IllegalStateException("댓글 숨김 처리 실패");
        }
    }

    public int getCommentTotalCount(String boardCode, Integer deptCode, String keyWordString, String keyWord) {
        return commentRepository.getCommentTotalCount(boardCode, deptCode, keyWordString, keyWord);
    }

    public List<CommentGroupDTO> getCommentGroupsByBoard(String boardCode, Integer deptId) {
        // boardCode 기준으로 모든 댓글 그룹 조회
        List<Long> postIds = commentRepository.findPostIdsByBoardAndDept(boardCode, deptId);

        List<CommentGroupDTO> groups = new ArrayList<>();
        for (Long postId : postIds) {
            List<CommentGroupDTO> group = getCommentGroups(boardCode, postId, deptId);
            if (!group.isEmpty()) {
                groups.add(group.get(0)); // 각 postId 별 첫 번째 댓글 그룹만 추가
            }
        }

        return groups;
    }

}
