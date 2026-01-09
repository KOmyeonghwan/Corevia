package com.example.corenet.admin.doc.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.corenet.admin.doc.entity.DocManager;

public interface DocManagerRepository extends JpaRepository<DocManager, Long> {

    // docCode로 docName 조회
    @Query("SELECT dm.docName FROM DocManager dm WHERE dm.docCode = :docCode")
    String findDocNameByDocCode(@Param("docCode") String docCode);

    // docCode로 조회
    Optional<DocManager> findByDocCode(String docCode);

    // docName으로 조회
    Optional<DocManager> findByDocName(String docName);

    // 부서 코드로 조회
    List<DocManager> findByDeptCode(Integer deptCode);

    // docCode로 삭제
    @Transactional
    void deleteByDocCode(String docCode);

}
