package com.example.corenet.client.mypage.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MypageService {
    private final JdbcTemplate jdbcTemplate;

    public MypageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getBoardCodeList(Integer departmentId) {
        String sql = "SELECT board_code from board_manager where dept_code IN (1, ?)";
        return jdbcTemplate.queryForList(sql, String.class, departmentId);
    }

    // 내가 쓴 게시판 전부
    public Integer getBoardTotal(Integer departmentId, Integer userId) {
        List<String> boardCodeList = getBoardCodeList(departmentId);


        Integer boardCount = 0; // 초기화

        String sql;
        for (String board : boardCodeList) {
            sql = "SELECT COUNT(*) from board_" + board + " where dept_code = ? and user_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, departmentId, userId);
            boardCount += (count != null) ? count : 0; 
        }

        return boardCount;
    }

    // 내단 댓글 전부
    public Integer getCommentTotal(Integer departmentId, Integer userId) {
        List<String> boardCodeList = getBoardCodeList(departmentId);
        Integer commentCount = 0; //

        String sql;
        for (String board : boardCodeList) {
            sql = "SELECT COUNT(*) from comment_" + board + " where dept_code = ? and user_id = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, departmentId, userId);
            commentCount += (count != null) ? count : 0; // null 처리: count가 null일 경우 0으로 처리
        }

        return commentCount;
    }

    // 내가 쓴 전자결재
    // 전부('draft','pending','approved','rejected','discarded','no_approval')
    public Integer getDocTotal(Integer jobcode) {
        List<String> docCodes = jdbcTemplate.queryForList("SELECT doc_code from doc_manager", String.class);

        String likeString = "%-" + jobcode + "-%"; // LIKE에 맞게 수정
        Integer docCount = 0; // 초기화

        String sql;
        for (String docCode : docCodes) {
            sql = "SELECT COUNT(*) from doc_" + docCode + " where doc_no LIKE ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, likeString);
            docCount += (count != null) ? count : 0; // null 처리: count가 null일 경우 0으로 처리
        }

        return docCount;
    }

}
