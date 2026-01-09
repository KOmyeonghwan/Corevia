package com.example.corenet.admin.doc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.corenet.admin.doc.entity.DocFile;

public interface DocFileRepository extends JpaRepository<DocFile, Long> {

    List<DocFile> findByDocTypeAndDocId(String docType, Long docId);

    void deleteByDocTypeAndDocId(String docType, Long docId);

    // 여러 개의 docId에 대해 삭제
    void deleteByDocTypeAndDocIdIn(String docType, List<Long> docIds);
}