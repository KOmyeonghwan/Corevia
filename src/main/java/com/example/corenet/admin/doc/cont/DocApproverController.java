package com.example.corenet.admin.doc.cont;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.corenet.admin.doc.dto.ApprovalRequest;
import com.example.corenet.admin.doc.entity.DocApprover;
import com.example.corenet.admin.doc.serv.DocApproverService;

@Controller
@RequestMapping("/api/approval")
public class DocApproverController {

    @Autowired
    private DocApproverService docApproverService;

    
    // 결재 상태 업데이트
    @PostMapping("/updateStatus/{docId}")
    public ResponseEntity<Map<String, Object>> updateApprovalStatus(
            @PathVariable(name = "docId") Long docId,
            @RequestBody ApprovalRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 결재 상태 업데이트
            DocApprover docApprover = docApproverService.updateApprovalStatus(
                    docId,
                    request.getApproverEmpNo(),
                    request.getApprovalStatus(),
                    request.getApprovalComments(),
                    request.getDocType());

            // 성공적으로 처리된 경우
            response.put("success", true);
            response.put("message", "Document approval status updated successfully with ID: " + docApprover.getDocId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 실패한 경우
            response.put("success", false);
            response.put("message", "Approval status update failed: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }
}
