package com.example.corenet.client.main.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    private final JdbcTemplate jdbcTemplate;

    public MainService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isAdminUser(String jobcode) {
        // 정규 표현식: jobcode가 "001"로 끝나는지 확인
        String regex = "001$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jobcode);
        return matcher.find();
    }

    // [검수자 외] 메인 화면 전자결재 집계
    public Map<String, Integer> countDocsStatusForUser(String jobcode) {

        List<String> docCodes = jdbcTemplate.queryForList("SELECT doc_code from doc_manager", String.class);

        // 상태별 초기화
        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("approver", 0);

        resultMap.put("draft", 0);
        resultMap.put("pending", 0);
        resultMap.put("approved", 0);
        resultMap.put("rejected", 0);

        // 각 테이블에 대한 상태별 카운트를 구하는 쿼리
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < docCodes.size(); i++) {
            if (i > 0)
                sql.append(" UNION ALL ");

            sql.append("SELECT ")
                    .append("COUNT(*) AS total, ")
                    .append("SUM(doc_status = 'draft') AS draft, ")
                    .append("SUM(doc_status = 'pending') AS pending, ")
                    .append("SUM(doc_status = 'approved') AS approved, ")
                    .append("SUM(doc_status = 'rejected') AS rejected ")
                    .append("FROM doc_").append(docCodes.get(i)).append(" ");

            sql.append("WHERE doc_no LIKE ?"); // jobcode를 기준으로 검색 (jobcode 조건)
        }

        String likePattern = "%-" + jobcode + "-%";
        Object[] params = new Object[docCodes.size()];
        Arrays.fill(params, likePattern);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params);

        // 결과 합산
        for (Map<String, Object> row : rows) {
            resultMap.put("draft", resultMap.get("draft")
                    + ((Number) row.get("draft") != null ? ((Number) row.get("draft")).intValue() : 0));
            resultMap.put("pending", resultMap.get("pending")
                    + ((Number) row.get("pending") != null ? ((Number) row.get("pending")).intValue() : 0));
            resultMap.put("approved", resultMap.get("approved")
                    + ((Number) row.get("approved") != null ? ((Number) row.get("approved")).intValue() : 0));
            resultMap.put("rejected", resultMap.get("rejected")
                    + ((Number) row.get("rejected") != null ? ((Number) row.get("rejected")).intValue() : 0));
        }

        return resultMap;
    }

    // [검수자] 메인 화면 전자결재 집계
    public Map<String, Integer> countDocsStatusForAdmin(String jobcode) {

        Map<String, Integer> resultMap = new HashMap<>();

        resultMap.put("approver", 1); // 1을 세팅하면 검수자
        resultMap.put("pending", 0);
        resultMap.put("approved", 0);
        resultMap.put("rejected", 0);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append("SUM(CASE WHEN approval_status = 'pending' THEN 1 ELSE 0 END) AS pending, ")
                .append("SUM(CASE WHEN approval_status = 'approved' THEN 1 ELSE 0 END) AS approved, ")
                .append("SUM(CASE WHEN approval_status = 'rejected' THEN 1 ELSE 0 END) AS rejected ")
                .append("FROM doc_approver ")
                .append("WHERE approver_emp_no = ?");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), jobcode);

        for (Map<String, Object> row : rows) {
            resultMap.put("pending", resultMap.get("pending")
                    + ((Number) row.get("pending") != null ? ((Number) row.get("pending")).intValue() : 0));
            resultMap.put("approved", resultMap.get("approved")
                    + ((Number) row.get("approved") != null ? ((Number) row.get("approved")).intValue() : 0));
            resultMap.put("rejected", resultMap.get("rejected")
                    + ((Number) row.get("rejected") != null ? ((Number) row.get("rejected")).intValue() : 0));
        }

        return resultMap;
    }

}
