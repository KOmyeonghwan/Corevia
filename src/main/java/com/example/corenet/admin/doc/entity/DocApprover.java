package com.example.corenet.admin.doc.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "doc_approver")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocApprover {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long docId; // 동적 생성 문서 테이블의 doc_id 참조

    // 결재자 정보
    @Column(length = 20, nullable = false)
    private String approverEmpNo;
    
    @Column(length = 50, nullable = false)
    private String approverName;

    @Column(length = 50)
    private Integer approverPosition;

    // docCode
    @Column(name = "doc_type", nullable = false, length = 50)
    private String docType;

    // 결재 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.DRAFT; // 임시저장 상태 -> 결재단계로 안넘어간 상태

    private LocalDateTime approvalDate;

    @Column(columnDefinition = "TEXT")
    private String approvalComments;

    // 생성 시 기본값 설정
    @PrePersist
    public void prePersist() {
        if (approvalStatus == null) {
            approvalStatus = ApprovalStatus.PENDING;
        }
    }

    // 승인 처리 메서드
    public void approve(String comments) {
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.approvalComments = comments;
        this.approvalDate = LocalDateTime.now();
    }

    // 반려 처리 메서드
    public void reject(String comments) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.approvalComments = comments;
        this.approvalDate = LocalDateTime.now();
    }

    public enum ApprovalStatus {
        DRAFT,      // 초안 (작성 중, 미상신)
        PENDING,    // 보류
        APPROVED,   // 승인
        REJECTED    // 반려
    }
}
