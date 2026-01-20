package com.example.corenet.admin.doc.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.corenet.admin.doc.entity.DocFile;

public interface DocFileRepository extends JpaRepository<DocFile, Long> {

    List<DocFile> findByDocTypeAndDocId(String docType, Long docId);

    void deleteByDocTypeAndDocId(String docType, Long docId);

    // 여러 개의 docId에 대해 삭제
    void deleteByDocTypeAndDocIdIn(String docType, List<Long> docIds);

    // docType + docId로 첫 번째 파일 조회
    Optional<DocFile> findFirstByDocTypeAndDocId(String docType, Long docId);

    // Method to find all files by docId
    List<DocFile> findByDocId(Long docId);

    // Method to delete files by docId
    @Transactional
    void deleteByDocId(Long docId);
}