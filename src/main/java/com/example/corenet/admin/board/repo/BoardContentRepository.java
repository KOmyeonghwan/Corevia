package com.example.corenet.admin.board.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.corenet.admin.board.dto.BoardContentDetailDTO;

@Repository
public class BoardContentRepository {

    private final JdbcTemplate jdbcTemplate;

    public BoardContentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 상세 조회
    public BoardContentDetailDTO findUserBoardDetail(String boardCode, Long id) {

        String tableName = "board_" + boardCode;

        String sql = "SELECT id, board_code, title, content, user_id, user_name, views, " +
                "file_name, file_url, create_at " +
                "FROM " + tableName +
                " WHERE board_code = ? AND id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            BoardContentDetailDTO dto = new BoardContentDetailDTO();
            dto.setId(rs.getLong("id"));
            dto.setBoardCode(rs.getString("board_code"));
            dto.setTitle(rs.getString("title"));
            dto.setContent(rs.getString("content"));
            dto.setUserId(rs.getInt("user_id"));
            dto.setUserName(rs.getString("user_name"));
            dto.setViews(rs.getInt("views"));
            dto.setFileName(rs.getString("file_name"));
            dto.setFileUrl(rs.getString("file_url"));
            dto.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
            return dto;
        }, boardCode, id);
    }

    // view 증가
    public void increseView(String boardCode, Long id) {
        String tableName = "board_" + boardCode;

        String sql = """
                    UPDATE %s
                    SET views = views + 1
                    WHERE id = ?
                """.formatted(tableName);

        jdbcTemplate.update(sql, id);
    }

    // 페이지네이션
    public int countAdPostList(
            String boardCode,
            Integer deptCode,
            String keyWord,
            String searchType) {

        String tableName = "board_" + boardCode;

        // 부서 전용 게시판 여부 확인
        Integer deptCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT dept_code) FROM board_manager WHERE board_code = ?",
                Integer.class,
                boardCode);

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT COUNT(*) FROM ").append(tableName).append(" WHERE 1=1 ");

        // 부서 조건
        if (deptCount != null && deptCount > 1) {
            sql.append(" AND dept_code = ? ");
            params.add(deptCode);
        }

        // 검색 조건 (LIST와 반드시 동일)
        if (StringUtils.hasText(keyWord) && StringUtils.hasText(searchType)) {
            switch (searchType) {
                case "name":
                    sql.append(" AND user_name LIKE ? ");
                    params.add("%" + keyWord.trim() + "%");
                    break;

                case "title":
                    sql.append(" AND title LIKE ? ");
                    params.add("%" + keyWord.trim() + "%");
                    break;

                default:
                    // 허용되지 않은 검색 타입 무시
                    break;
            }
        }

        return jdbcTemplate.queryForObject(
                sql.toString(),
                params.toArray(),
                Integer.class);
    }

}