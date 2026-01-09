package com.example.corenet.admin.doc.serv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.corenet.admin.doc.repo.DocApproverRepository;
import com.example.corenet.admin.doc.repo.DocFileRepository;

import jakarta.transaction.Transactional;

@Service
public class DocFileService {
    private final DocFileRepository docFileRepository;
    private final DocApproverRepository docApproverRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DocFileService(DocFileRepository docFileRepository,
            DocApproverRepository docApproverRepository, JdbcTemplate jdbcTemplate) {
        this.docFileRepository = docFileRepository;
        this.docApproverRepository = docApproverRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getTableName(String docType) {
        return "doc_" + docType;
    }

    @Transactional
    public void deleteDocument(String docType) {
        // 1. doc_manager 테이블에서 모든 문서 유형(doc_code) 가져오기
        String sql = "SELECT doc_code FROM doc_manager";
        List<String> docCodes = jdbcTemplate.queryForList(sql, String.class);
    
        // 2. docCodes에서 각 docCode에 맞는 테이블 이름을 getTableName()을 사용하여 가져오기
        List<String> docTableNames = new ArrayList<>();
        for (String docCode : docCodes) {
            String tableName = getTableName(docCode);  
            docTableNames.add(tableName);
        }
    
        // 3. 허용된 테이블 목록 (테이블 이름 검증을 위해)
        List<String> allowedTables = new ArrayList<>();
        allowedTables.add(getTableName(docType)); // 예시로 doc_Draft 테이블을 허용된 테이블로 추가
    
        // 4. docTableNames에서 각 테이블에서 'discarded' 상태인 문서를 추출하여 삭제할 문서 목록 만들기
        List<Map<String, Object>> docTableAndIdx = new ArrayList<>();
    
        // 각 테이블에서 폐기된 문서 정보 가져오기
        for (String docTable : docTableNames) {
            System.out.println("검사 중인 테이블: " + docTable);
    
            // 테이블 이름이 허용된 테이블 목록에 있는지 확인
            if (!allowedTables.contains(docTable)) {
                System.out.println("허용되지 않은 테이블, 건너뛰기: " + docTable);
                continue; // 허용된 테이블이 아닌 경우, 건너뛰기
            }
    
            // 해당 테이블에서 'discarded' 상태인 문서의 doc_id를 찾는 쿼리 실행
            String findDiscardedSql = "SELECT doc_id FROM " + docTable + " WHERE doc_status = 'discarded'";
            List<Map<String, Object>> discardedDocs = jdbcTemplate.queryForList(findDiscardedSql);
    
            // 삭제할 문서가 있으면 리스트에 추가
            if (!discardedDocs.isEmpty()) {
                System.out.println("테이블: " + docTable + "에서 폐기된 문서가 있습니다.");
                for (Map<String, Object> doc : discardedDocs) {
                    // 각 테이블명과 doc_id를 매핑한 정보를 리스트에 추가
                    Map<String, Object> docInfo = new HashMap<>();
                    docInfo.put("docTable", docTable); // 테이블명
                    docInfo.put("docId", doc.get("doc_id")); // doc_id
                    docTableAndIdx.add(docInfo);
                    System.out.println("삭제할 문서 ID: " + doc.get("doc_id"));
                }
            } else {
                System.out.println("테이블: " + docTable + "에 폐기된 문서가 없습니다.");
            }
        }
    
        // 5. 삭제할 문서 정보 출력 (디버깅용)
        System.out.println("삭제할 문서 목록:");
        for (Map<String, Object> entry : docTableAndIdx) {
            System.out.println("테이블: " + entry.get("docTable") + ", 문서 ID: " + entry.get("docId"));
        }
    
        // 6. 각 테이블에서 'discarded' 상태인 문서 삭제
        for (Map<String, Object> entry : docTableAndIdx) {
            String docTable = (String) entry.get("docTable");
            Long docId = (Long) entry.get("docId");
    
            try {
                System.out.println("파일 삭제 중: 테이블: " + docTable + ", 문서 ID: " + docId);
                String deleteSql = "DELETE FROM doc_file WHERE doc_id = ? AND doc_type = ?";
                jdbcTemplate.update(deleteSql, docId, docType);
    
                System.out.println("결재자 삭제 중: 테이블: " + docTable + ", 문서 ID: " + docId);
                deleteSql = "DELETE FROM doc_approver WHERE doc_id = ? AND doc_type = ?";
                jdbcTemplate.update(deleteSql, docId, docType);
    
                // 실제 문서 삭제
                deleteSql = "DELETE FROM " + docTable + " WHERE doc_id = ? AND doc_status = 'discarded'";
                System.out.println("실제 문서 삭제 중: " + deleteSql);
                jdbcTemplate.update(deleteSql, docId);
    
            } catch (Exception e) {
                System.err.println("삭제 실패: 테이블: " + docTable + ", 문서 ID: " + docId);
                e.printStackTrace();
            }
        }
    
        // Optional: 결과 처리
        System.out.println("삭제된 문서와 관련된 데이터가 모두 삭제되었습니다.");
    }

}
