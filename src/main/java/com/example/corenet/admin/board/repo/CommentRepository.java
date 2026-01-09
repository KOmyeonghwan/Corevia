package com.example.corenet.admin.board.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.corenet.admin.board.dto.CommentDTO;
import com.example.corenet.admin.board.dto.commentDTO.CommentListDTO;

@Repository
public class CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getCommentTableName(String boardCode) {
        return "comment_" + boardCode;
    }

    public String getBoardTableName(String boardCode) {
        return "board_" + boardCode;
    }

    // 댓글 게시판 리스트
    public List<CommentListDTO> getCommentList(
            String boardCode,
            Integer deptCode,
            String keyWordString,
            String keyWord,
            int page,
            int size) {
        String boardTableName = getBoardTableName(boardCode);
        String commentTableName = getCommentTableName(boardCode);

        Integer deptCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT dept_code) FROM board_manager WHERE board_code = ?",
                Integer.class,
                boardCode);

        if (deptCount == null) {
            throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
        }

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT ")
                .append(" c.id            AS commentId, ")
                .append(" b.id            AS postId, ")
                .append(" b.user_id       AS boardUserId, ")
                .append(" b.board_code    AS boardCode, ")
                .append(" b.content       AS boardContent, ")
                .append(" c.user_id       AS commentUserId, ")
                .append(" c.user_name     AS commentUserName, ")
                .append(" c.content       AS commentContent, ")
                .append(" c.parent_id     AS parentId, ")
                .append(" c.create_at     AS commentCreateAt, ")
                .append(" c.depth         AS depth ")
                .append("FROM ").append(boardTableName).append(" b ")
                .append("JOIN ").append(commentTableName).append(" c ")
                .append(" ON c.post_id = b.id ")
                .append("WHERE 1=1 ");

        if (deptCount > 1) {
            sql.append(" AND c.dept_code = ? ");
            params.add(deptCode);
        }

        if (keyWordString != null && keyWord != null && !keyWord.trim().isEmpty()) {
            switch (keyWordString) {
                case "comment-author":
                    sql.append(" AND c.user_name LIKE ? ");
                    params.add("%" + keyWord.trim() + "%");
                    break;
                case "comment-content":
                    sql.append(" AND c.content LIKE ? ");
                    params.add("%" + keyWord.trim() + "%");
                    break;
            }
        }

        sql.append(" ORDER BY c.parent_id ASC, c.depth ASC, c.create_at ASC ")
                .append(" LIMIT ? OFFSET ? ");
        params.add(size);
        params.add((page - 1) * size);

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    CommentListDTO dto = new CommentListDTO();
                    dto.setCommentId(rs.getLong("commentId"));
                    dto.setPostId(rs.getLong("postId"));
                    dto.setBoardUserId(rs.getInt("boardUserId"));
                    dto.setBoardCode(rs.getString("boardCode"));
                    dto.setBoardContent(rs.getString("boardContent"));
                    dto.setCommentUserId(rs.getLong("commentUserId"));
                    dto.setCommentUserName(rs.getString("commentUserName"));
                    dto.setCommentContent(rs.getString("commentContent"));
                    dto.setParentId(rs.getLong("parentId"));
                    dto.setCommentCreateAt(rs.getTimestamp("commentCreateAt").toLocalDateTime());
                    dto.setDepth(rs.getInt("depth"));
                    return dto;
                });
    }

    // 해당 게시물에 대한 댓글
    public List<CommentDTO> getCommentDetail(String boardCode, Long postId, Integer deptCode) {

        String commentTableName = "comment_" + boardCode;

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT * FROM ")
                .append(commentTableName)
                .append(" WHERE post_id = ? ");

        params.add(postId);

        // 댓글 정렬 (부모 → depth → 작성순)
        sql.append(" ORDER BY parent_id ASC, depth ASC, create_at ASC ");

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    CommentDTO dto = new CommentDTO();
                    dto.setId(rs.getLong("id"));
                    dto.setPostId(rs.getLong("post_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setUserName(rs.getString("user_name"));
                    dto.setContent(rs.getString("content"));
                    dto.setParentId(rs.getLong("parent_id"));
                    dto.setDepth(rs.getInt("depth"));
                    dto.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
                    dto.setStatus(rs.getString("status"));
                    return dto;
                });
    }

    // 댓글 삭제
    public int deleteComment(String boardCode, Long commentId) {
        String commentTableName = getCommentTableName(boardCode);

        String sql = "DELETE FROM " + commentTableName + " WHERE id = ?";
        return jdbcTemplate.update(sql, commentId);
    }

    // 댓글 업데이트
    public int updateContentAndStatus(String boardCode, Long commentId, String content, String status) {
        String commentTableName = getCommentTableName(boardCode);

        String sql = "UPDATE " + commentTableName + " SET content = ?, status = ? WHERE id = ?";

        return jdbcTemplate.update(sql, content, status, commentId);
    }

    // 댓글 숨김처리
    public int updateStatus(String boardCode, Long commentId) {
        String commentTableName = getCommentTableName(boardCode);
        String status = "hidden";

        String sql = "UPDATE " + commentTableName + " SET status = ? WHERE id = ?";

        return jdbcTemplate.update(sql, status, commentId);
    }

    // CommentRepository.java
    public int getCommentTotalCount(String boardCode, Integer deptCode, String keyWordString, String keyWord) {
        String commentTableName = "comment_" + boardCode; // getCommentList()와 동일
        String sql = "SELECT COUNT(*) FROM " + commentTableName + " WHERE 1=1 ";

        List<Object> params = new ArrayList<>();

        if (deptCode != null) {
            sql += " AND dept_code = ? ";
            params.add(deptCode);
        }

        if (keyWord != null && !keyWord.isBlank() && keyWordString != null && !keyWordString.isBlank()) {
            if ("comment-author".equals(keyWordString)) {
                sql += " AND user_name LIKE ? ";
                params.add("%" + keyWord + "%");
            } else if ("comment-content".equals(keyWordString)) {
                sql += " AND content LIKE ? ";
                params.add("%" + keyWord + "%");
            }
        }

        return jdbcTemplate.queryForObject(sql, params.toArray(), Integer.class);
    }

    public List<Long> findPostIdsByBoardAndDept(String boardCode, Integer deptId) {
        String tableName = "comment_" + boardCode;

        String sql = "SELECT DISTINCT post_id FROM " + tableName;

        List<Object> params = new ArrayList<>();

        if (deptId != null) {
            sql += " WHERE dept_code = ?";
            params.add(deptId);
        }

        sql += " ORDER BY create_at ASC";

        return jdbcTemplate.queryForList(sql, Long.class, params.toArray());
    }

}
