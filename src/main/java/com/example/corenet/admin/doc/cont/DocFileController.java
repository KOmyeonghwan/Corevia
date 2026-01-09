package com.example.corenet.admin.doc.cont;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.corenet.admin.doc.serv.DocFileService;

@Controller
public class DocFileController {
    
    @Autowired
    private DocFileService docFileService;

    // 문서 삭제 요청을 처리하는 메서드
    @DeleteMapping("/deletePermanentDocuments")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePermanentDocuments(@RequestParam("docType") String docType) {
        try {
            // 서비스에서 영구 삭제 메서드 호출
            docFileService.deleteDocument(docType);
            
            // 성공 응답 반환
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "폐기문서가 영구 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 발생 시 처리
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "문서 삭제에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    
}
