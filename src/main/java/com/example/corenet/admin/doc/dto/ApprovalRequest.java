package com.example.corenet.admin.doc.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ApprovalRequest {

    private String approverEmpNo;    // 결재자 사번
    private String approverName;     // 결재자 이름
    private Integer approverPosition; // 결재자 직급
    private String approvalComments; // 결재자 반려 사유
    private String approvalStatus;  // 승인 상태
    private String docType;

    // 기본 생성자
    public ApprovalRequest() {}

    // Getter와 Setter 메서드
    public String getApproverEmpNo() {
        return approverEmpNo;
    }

    public void setApproverEmpNo(String approverEmpNo) {
        this.approverEmpNo = approverEmpNo;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public Integer getApproverPosition() {
        return approverPosition;
    }

    public void setApproverPosition(Integer approverPosition) {
        this.approverPosition = approverPosition;
    }

    public String getApprovalComments() {
        return approvalComments;
    }

    public void setApprovalComments(String approvalComments) {
        this.approvalComments = approvalComments;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }


    @Override
    public String toString() {
        return "ApprovalRequest{" +
                "approverEmpNo='" + approverEmpNo + '\'' +
                ", approverName='" + approverName + '\'' +
                ", approverPosition=" + approverPosition +
                ", approvalComments='" + approvalComments + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                '}';
    }

}

