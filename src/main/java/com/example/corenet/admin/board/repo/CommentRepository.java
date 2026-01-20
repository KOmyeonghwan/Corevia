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

    // ======== adcomment =======
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
                .append("WHERE c.parent_id IS NULL ");

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
                    dto.setPostId(rs.getLong("postId")); // 게시글 id
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

    // ======== adcommentdetail =======
    // 댓글 상세 조회 (원댓글 + 대댓글 + 대대댓글) - 재귀 CTE 사용
    public List<CommentDTO> getCommentDetail(String boardCode, Long postId, Long commentId, Integer deptCode) {
        String commentTableName = getCommentTableName(boardCode);
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("WITH RECURSIVE comment_tree AS (")
                .append("    SELECT * FROM ").append(commentTableName)
                .append("    WHERE id = ?") // 최상위 댓글
                .append("    UNION ALL ")
                .append("    SELECT c.* FROM ").append(commentTableName).append(" c ")
                .append("    INNER JOIN comment_tree ct ON c.parent_id = ct.id ")
                .append(") ")
                .append("SELECT * FROM comment_tree WHERE post_id = ? ");

        params.add(commentId); // 원댓글 ID
        params.add(postId); // 게시글 ID

        if (deptCode != null) {
            sql.append(" AND dept_code = ?");
            params.add(deptCode);
        }

        sql.append(" ORDER BY depth ASC, create_at ASC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(rs.getLong("id"));
            dto.setPostId(rs.getLong("post_id"));
            dto.setUserId(rs.getInt("user_id"));
            dto.setUserName(rs.getString("user_name"));
            dto.setContent(rs.getString("content"));
            dto.setParentId(rs.getObject("parent_id") != null ? rs.getLong("parent_id") : null);
            dto.setDepth(rs.getInt("depth"));
            dto.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
            dto.setStatus(rs.getString("status"));
            return dto;
        });
    }

    // 해당 게시물에 대한 댓글들(댓글 + 대댓글s + 대대댓글s)
    public List<CommentDTO> getCommentDetails(String boardCode, Long postId, Integer deptCode) {

        String commentTableName = getCommentTableName(boardCode);

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

    // [관리자] 댓글 상세(post_id에 달린 모든 댓글 출력하는 방식 때 사용) -> 본인 부서가 작성한 댓글이 달린 post_id 출력
    public List<Long> findPostIdsByBoardAndDept(String boardCode, Integer deptId) {
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

        sql.append("SELECT DISTINCT post_id FROM ").append(commentTableName);

        if (deptCount > 1 && deptId != null) {
            sql.append(" WHERE dept_code = ?");
            params.add(deptId);
        }

        sql.append(" ORDER BY create_at ASC");

        return jdbcTemplate.queryForList(sql.toString(), Long.class, params.toArray());
    }

    public List<CommentDTO> findRootCommentsByBoard(String boardCode, Integer deptId) {

        String commentTableName = getCommentTableName(boardCode);

        Integer deptCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT dept_code) FROM board_manager WHERE board_code = ?",
                Integer.class,
                boardCode);

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT * FROM ")
                .append(commentTableName)
                .append(" WHERE depth = 0 ");

        // 부서 분리 게시판이면 deptId가 1(대표)가 아닌 경우만 필터링
        if (deptCount != null && deptCount > 1 && deptId != null && deptId != 1) {
            sql.append(" AND dept_code = ? ");
            params.add(deptId);
        }

        // 최신 댓글이 위로 오게 (관리자 기준)
        sql.append(" ORDER BY create_at DESC ");

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
                    dto.setParentId(null); 
                    dto.setDepth(0);
                    dto.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
                    dto.setStatus(rs.getString("status"));
                    return dto;
                });
    }

    // ========================

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

    // adcomment 페이지네이션 & 검색
    public int getCommentTotalCount(
            String boardCode,
            Integer deptCode,
            String keyWordString,
            String keyWord) {

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

        sql.append("SELECT COUNT(*) ")
                .append("FROM ").append(boardTableName).append(" b ")
                .append("JOIN ").append(commentTableName).append(" c ")
                .append(" ON c.post_id = b.id ")
                .append("WHERE c.parent_id IS NULL ");

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

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

}
