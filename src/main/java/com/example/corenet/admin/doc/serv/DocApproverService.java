package com.example.corenet.admin.doc.serv;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.corenet.admin.doc.dto.ApproverDTO;
import com.example.corenet.admin.doc.entity.DocApprover;


@Service
public class DocApproverService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 결재 상태 변경 (예: 반려, 승인)
     * 
     * @param docId            : 문서 ID
     * @param approverEmpNo    : 결재자 사번
     * @param status           : 결재 상태 (APPROVED / REJECTED)
     * @param approvalComments : 결재자 의견
     * @return DocApprover 객체
     */
    public DocApprover updateApprovalStatus(
            Long docId,
            String approverEmpNo,
            String status,
            String approvalComments,
            String docType) {

        try {
            LocalDateTime approvalDate = LocalDateTime.now();

            // 반려일 경우 문서 상태 변경
            if ("REJECTED".equals(status)) {
                String rejectedSql = "UPDATE doc_" + docType +
                        " SET doc_status = ? " +
                        "WHERE doc_id = ?";
                jdbcTemplate.update(rejectedSql, status, docId);
                // updateDocStatus(docId, docType, "rejected");
            }

            if ("DISCARDED".equals(status)) {
                updateDocStatus(docId, docType, "discarded");
                status = "REJECTED";
            }

            // 결재자 상태 업데이트
            String approverSql = "UPDATE doc_approver " +
                    "SET approval_status = ?, approval_comments = ?, approval_date = ? " +
                    "WHERE doc_id = ? AND approver_emp_no = ? AND doc_type = ?";

            int rowsAffected = jdbcTemplate.update(
                    approverSql,
                    status,
                    approvalComments,
                    approvalDate,
                    docId,
                    approverEmpNo,
                    docType);

            if (rowsAffected == 0) {
                throw new RuntimeException("No matching doc approver found");
            }

            // 둘 다 승인하면 문서 상태 변경
            if ("APPROVED".equals(status)) {
                updateDocStatus(docId, docType, "approved");
            }

            // 반환용 DocApprover 객체 생성
            DocApprover docApprover = new DocApprover();
            docApprover.setDocId(docId);
            docApprover.setApproverEmpNo(approverEmpNo);
            docApprover.setApprovalComments(approvalComments);
            docApprover.setApprovalDate(approvalDate);
            docApprover.setApprovalStatus(DocApprover.ApprovalStatus.valueOf(status));

            return docApprover;

        } catch (Exception e) {
            throw new RuntimeException("Approval status update failed: " + e.getMessage(), e);
        }
    }

    // 결재자 정보만 가져오는 함수 -> function updateApprovalTable(approvers)애
    public List<ApproverDTO> getApprovalStatus(Long docId, String docType) {
        String sql = "SELECT approver_emp_no, approval_status, approval_comments, approval_date " +
                "FROM doc_approver " +
                "WHERE doc_id = ? AND doc_type = ?";

        return jdbcTemplate.query(sql, new Object[] { docId, docType }, (rs, rowNum) -> {
            ApproverDTO approver = new ApproverDTO();
            approver.setApproverEmpNo(rs.getString("approver_emp_no"));
            approver.setApprovalStatus(rs.getString("approval_status"));
            approver.setApprovalComments(rs.getString("approval_comments"));
            Timestamp ts = rs.getTimestamp("approval_date");
            if (ts != null) {
                approver.setApprovalDate(ts.toLocalDateTime());
            }
            return approver;
        });
    }

    // doc_status 변경
    public void updateDocStatus(Long docId, String docType, String status) {
        // 전체 결재자 수 조회
        String totalSql = "SELECT COUNT(*) FROM doc_approver WHERE doc_id = ? AND doc_type = ?";
        int totalCount = jdbcTemplate.queryForObject(totalSql, new Object[] { docId, docType }, Integer.class);

        boolean shouldUpdate = false;

        if (totalCount > 0) {
            switch (status.toLowerCase()) {
                case "discarded":
                    shouldUpdate = true;
                    break;

                case "rejected":
                    shouldUpdate = true;
                    // String rejectedSql = "SELECT COUNT(*) FROM doc_approver WHERE doc_id = ? AND doc_type = ? AND approval_status = 'REJECTED'";
                    // int rejectedCount = jdbcTemplate.queryForObject(rejectedSql, new Object[] { docId, docType },
                    //         int.class);
                    // if (rejectedCount == totalCount) {
                    //     shouldUpdate = true;
                    // }
                    break;

                case "approved":
                    // 승인된 결재자 수 조회
                    String approvedSql = "SELECT COUNT(*) FROM doc_approver WHERE doc_id = ? AND doc_type = ? AND approval_status = 'APPROVED'";
                    int approvedCount = jdbcTemplate.queryForObject(approvedSql, new Object[] { docId, docType },
                            int.class);

                    if (approvedCount == totalCount) {
                        shouldUpdate = true;
                    }
                    break;

                default:
                    break;
            }
        }

        if (shouldUpdate) {
            String updateSql = "UPDATE doc_" + docType + " SET doc_status = ? WHERE doc_id = ?";
            jdbcTemplate.update(updateSql, status.toLowerCase(), docId);
        }
    }

    // 문서 수정하기 등록 시
    public void updateApprovalStatusAndComment(Long docId, String docCode, String status) {
        if (docId == null && docCode == null) {
            throw new IllegalArgumentException("docId and docCode cannot be null");
        }

        System.out.println("Long docId > " + docId + " docCode > " + docCode + " status > " + status);

        if ("DRAFT".equalsIgnoreCase(status)) {
            String sql = "UPDATE doc_approver SET approval_status = ?, approval_comments = ? WHERE doc_id = ? AND doc_type = ?";
            jdbcTemplate.update(sql, "DRAFT", null, docId, docCode);
        }

        else if ("PENDING".equalsIgnoreCase(status)) {
            String sql = "UPDATE doc_approver" + " SET approval_status = ?, approval_comments = ? WHERE doc_id = ? AND doc_type = ?";
            jdbcTemplate.update(sql, status, null, docId, docCode);
        }
        
    }
    
}
