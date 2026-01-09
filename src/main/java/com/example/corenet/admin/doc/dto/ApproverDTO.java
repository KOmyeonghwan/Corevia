package com.example.corenet.admin.doc.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproverDTO {
    private String approverEmpNo; // 결재자 사번
    private String approverName; // 결재자 이름
    private Integer approverPosition; // 결재자 직급
    private String approvalComments;
    private String approvalStatus;
    private LocalDateTime approvalDate;

    // 기본 생성자
    public ApproverDTO() {
    }

    // 모든 필드를 포함한 생성자
    public ApproverDTO(String approverEmpNo, String approverName, Integer approverPosition) {
        this.approverEmpNo = approverEmpNo;
        this.approverName = approverName;
        this.approverPosition = approverPosition;
    }

    // Getter and Setter methods
    public String getApprovalComments() {
        return approvalComments != null ? approvalComments : "";
    }
    
    public String getApprovalDateStr() {
        if (approvalDate != null) {
            return approvalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        return "";
    }

    public Integer getPositionForJs() {
        if (approverPosition == null) return 5; // 기본 사원
        switch (approverPosition) {
            case 1: return 1; // 대표
            case 2: return 2; // 부장
            case 3: return 3; // 과장
            case 4: return 4; // 대리
            case 5: return 5; // 사원
            default: return 5;
        }
    }

    // 직급 이름으로 반환
    public String getPositionName() {
        if (approverPosition == null) return "사원";
        switch (approverPosition) {
            case 1: return "대표";
            case 2: return "부장";
            case 3: return "과장";
            case 4: return "대리";
            case 5: return "사원";
            default: return "사원";
        }
    }

    // approvalStatus
    public String getApprovalStatusKor() {
        if (approvalStatus == null) return "";
        switch (approvalStatus) {
            case "DRAFT": return "기안";
            case "PENDING": return "결재대기";
            case "APPROVED": return "승인";
            case "REJECTED": return "반려";
            default: return approvalStatus;
        }
    }

    @Override
    public String toString() {
        return "ApproverDTO{" +
                "approverEmpNo='" + approverEmpNo + '\'' +
                ", approverName='" + approverName + '\'' +
                ", approverPosition=" + approverPosition +
                '}';
    }
}
