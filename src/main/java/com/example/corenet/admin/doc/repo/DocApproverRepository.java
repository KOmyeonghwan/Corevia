package com.example.corenet.admin.doc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.corenet.admin.doc.entity.DocApprover;

public interface DocApproverRepository extends JpaRepository<DocApprover, Long> {

    // 특정 문서(docId)의 결재자 전체 조회
    List<DocApprover> findByDocId(Long docId);

    // 특정 문서 + 승인 상태별 조회
    List<DocApprover> findByDocIdAndApprovalStatus(Long docId, DocApprover.ApprovalStatus status);

    // 특정 결재자 삭제
    void deleteById(Long id);

    // 특정 문서의 결재자 전체 삭제
    void deleteByDocId(Long docId);

    void deleteByDocTypeAndDocId(String docType, Long docId);

}
