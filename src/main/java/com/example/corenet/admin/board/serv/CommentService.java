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

    // 게시글에 달린 모든 댓글 가져오기
    public List<CommentDTO> getCommentDetails(String boardCode, Long postId, Integer deptCode) {
        return commentRepository.getCommentDetails(boardCode, postId, deptCode);
    }

    // 댓글 상세 조회(원댓글과 자식 댓글 구조) * 여러개
    public List<CommentGroupDTO> getCommentGroups(String boardCode, Long postId, Integer deptCode) {
        List<CommentDTO> allComments = getCommentDetails(boardCode, postId, deptCode);

        Map<Long, CommentGroupDTO> groups = new LinkedHashMap<>();
        Map<Long, List<CommentDTO>> childMap = new HashMap<>();

        for (CommentDTO comment : allComments) {
            if (comment.getDepth() == 0) {
                CommentGroupDTO group = new CommentGroupDTO();
                group.setRoot(comment);
                groups.put(comment.getId(), group);
            } else {
                childMap.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>()).add(comment);
            }
        }

        for (CommentGroupDTO group : groups.values()) {
            attachReplies(group.getRoot(), group.getReplies(), childMap);
        }

        return new ArrayList<>(groups.values());
    }

    private void attachReplies(CommentDTO parent, List<CommentDTO> resultList, Map<Long, List<CommentDTO>> childMap) {
        List<CommentDTO> children = childMap.get(parent.getId());
        if (children != null) {
            for (CommentDTO child : children) {
                resultList.add(child);
                attachReplies(child, resultList, childMap);
            }
        }
    }

    // 관리자 댓글 상세/목록용 - 원댓글 기준 그룹 조회
    public List<CommentGroupDTO> getAdminRootCommentGroups(String boardCode, Integer deptId) {

        // 1. 원댓글만 조회 (depth = 0)
        List<CommentDTO> roots = commentRepository.findRootCommentsByBoard(boardCode, deptId);

        List<CommentGroupDTO> result = new ArrayList<>();

        // 2. 각 원댓글마다 트리 구성
        for (CommentDTO root : roots) {

            // 해당 게시글의 모든 댓글
            List<CommentGroupDTO> groups = getCommentGroups(boardCode, root.getPostId(), deptId);

            // 3. root id와 일치하는 그룹만 선택
            for (CommentGroupDTO group : groups) {
                if (group.getRoot().getId().equals(root.getId())) {
                    result.add(group);
                    break;
                }
            }
        }

        return result;
    }

    // 페이지 기반 댓글 그룹 조회
    public List<CommentGroupDTO> getCommentGroupsByBoard(String boardCode, Integer deptId, int page, int size) {
        List<Long> postIds = commentRepository.findPostIdsByBoardAndDept(boardCode, deptId);
        List<CommentGroupDTO> allGroups = new ArrayList<>();

        for (Long postId : postIds) {
            List<CommentGroupDTO> groupList = getCommentGroups(boardCode, postId, deptId);
            allGroups.addAll(groupList); // 게시글 모든 원댓글 추가
        }

        int totalGroups = allGroups.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalGroups);

        if (fromIndex >= totalGroups) {
            return new ArrayList<>();
        }

        return allGroups.subList(fromIndex, toIndex);
    }

    // 총 댓글 그룹 개수
    public int getTotalCommentGroups(String boardCode, Integer deptId) {
        List<Long> postIds = commentRepository.findPostIdsByBoardAndDept(boardCode, deptId);
        int count = 0;
        for (Long postId : postIds) {
            List<CommentGroupDTO> groups = getCommentGroups(boardCode, postId, deptId);
            count += groups.size();
        }
        return count;
    }

    // 댓글 삭제 =============================
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

    // adcomment pagination
    public int getCommentTotalCount(String boardCode, Integer deptCode, String keyWordString, String keyWord) {
        return commentRepository.getCommentTotalCount(boardCode, deptCode, keyWordString, keyWord);
    }

}
