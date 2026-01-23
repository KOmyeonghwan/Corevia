package com.example.corenet.admin.doc.cont;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.corenet.admin.doc.repo.DocApproverRepository;
import com.example.corenet.admin.doc.serv.DocApproverService;
import com.example.corenet.admin.doc.serv.DocManagerService;
import com.example.corenet.common.dto.LoginUserDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DocManagerController {

    @Autowired
    private final DocApproverRepository docApproverRepository;

    @Autowired
    private final DocApproverService docApproverService;

    @Autowired
    private final DocManagerService docManagerService;

    @Value("${doc.upload.path}")
    private String docDir;

    @PostMapping("/doc/create")
    public ResponseEntity<?> createDoc(@RequestBody Map<String, Object> body) {
        try {
            String docCode = (String) body.get("docCode");
            String docName = (String) body.get("docName");

            Integer deptCode = body.get("deptCode") != null
                    ? Integer.parseInt(body.get("deptCode").toString())
                    : 1;

            boolean useDocNo = body.get("useDocNo") != null && Boolean.parseBoolean(body.get("useDocNo").toString());
            boolean usePageNo = body.get("usePageNo") != null && Boolean.parseBoolean(body.get("usePageNo").toString());
            boolean useWriter = body.get("useWriter") != null && Boolean.parseBoolean(body.get("useWriter").toString());

            // 결재 검토자
            boolean useApproval = body.get("useApproval") != null
                    && Boolean.parseBoolean(body.get("useApproval").toString());

            boolean useDrafter = body.get("useDrafter") != null
                    && Boolean.parseBoolean(body.get("useDrafter").toString());
            boolean useDetailTitle = body.get("useDetailTitle") != null
                    && Boolean.parseBoolean(body.get("useDetailTitle").toString());
            boolean useDetailContent = body.get("useDetailContent") != null
                    && Boolean.parseBoolean(body.get("useDetailContent").toString());
            boolean useDetailNote = body.get("useDetailNote") != null
                    && Boolean.parseBoolean(body.get("useDetailNote").toString());

            boolean useFile = body.get("useFile") != null
                    && Boolean.parseBoolean(body.get("useFile").toString());

            docManagerService.createDocTable(
                    docCode, docName, deptCode,
                    useDocNo, usePageNo, useApproval, useWriter, useDrafter,
                    useDetailTitle, useDetailContent, useDetailNote, useFile);

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "문서가 생성되었습니다.");
            return ResponseEntity.ok(res);

        } catch (IllegalStateException e) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(res);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", "문서 생성 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    // [관리자] 문서 서식 삭제 (docCode 기반)
    @DeleteMapping("/doc/delete/{docCode}")
    public ResponseEntity<?> deleteDoc(@PathVariable("docCode") String docCode) {
        try {
            docManagerService.deleteDoc(docCode);

            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("message", "문서가 삭제되었습니다.");
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", "문서 삭제 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    // 특정 문서 코드의 전체 문서 수 반환
    @GetMapping("/docs/count")
    public Map<String, Object> getDocsCount(@RequestParam("docCode") String docCode) {
        int count = docManagerService.countByDocCode(docCode);

        // JSON 형태로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return response;
    }

    // [사용자] 전자결재 화면 구현
    @GetMapping("/doc/column-status")
    @ResponseBody
    public Map<String, Boolean> getColumnStatus(
            @RequestParam("docCode") String docCode) {

        // 화면(UI)과 매핑할 키들
        String[] columnKeys = {
                "DOC_NO",
                "PAGE_NO",
                "WRITER",
                "DRAFTER",
                "DETAIL_TITLE",
                "DETAIL_CONTENT",
                "DETAIL_NOTE",
                "FILE"
        };

        return docManagerService.getColumnStatus(docCode, columnKeys);
    }

    // 전자결재 문서 작성
    @PostMapping("/doc/save")
    @Transactional
    public ResponseEntity<String> saveDocument(
            @RequestParam("docCode") String docCode,
            @RequestPart("docData") String docDataJson,
            @RequestPart(value = "attachFile", required = false) MultipartFile attachFile) {

        if (docCode == null || docCode.isBlank()) {
            return ResponseEntity.badRequest().body("docCode 누락");
        }

        try {
            // JSON → Map 변환
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> screenData = mapper.readValue(docDataJson, new TypeReference<>() {
            });

            // 화면 데이터를 DB 컬럼명으로 매핑 + 필터링
            Map<String, Object> dbData = docManagerService.mapAndFilterScreenData(
                    docManagerService.getDocTableName(docCode),
                    screenData);

            // 1. 문서 저장 (doc 테이블)
            String status = (String) dbData.get("status");
            Long docId = docManagerService.saveDocumentAndGetId(docCode, dbData);

            // 2. 기존 첨부파일 삭제 및 새 파일 저장
            if (attachFile != null && !attachFile.isEmpty()) {
                // 기존 첨부파일 삭제
                docManagerService.deleteExistingFiles(docId);

                // 새 파일 저장
                Path baseDir = Paths.get(docDir, docCode);
                Files.createDirectories(baseDir);

                String originalFileName = attachFile.getOriginalFilename();

                // 확장자 추출
                String ext = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                }

                // UUID + 확장자
                String savedFileName = UUID.randomUUID().toString() + ext;

                // 실제 물리 경로
                Path savePath = baseDir.resolve(savedFileName);

                // 파일 저장
                attachFile.transferTo(savePath.toFile());

                // DB 저장용 상대 경로
                String dbFilePath = Paths.get(docCode, savedFileName).toString();

                Map<String, Object> fileData = new HashMap<>();
                fileData.put("doc_id", docId);
                fileData.put("doc_type", docCode);
                fileData.put("file_name", originalFileName);
                fileData.put("file_path", dbFilePath);
                fileData.put("file_size", attachFile.getSize());
                fileData.put("created_at", LocalDateTime.now());

                docManagerService.saveFileInfo(fileData);
            }

            return ResponseEntity.ok("저장 완료");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("저장 실패: " + e.getMessage());
        }
    }

    // 전자결재 문서 수정
    @PostMapping("/doc/update")
    public ResponseEntity<String> updateDocument(
            @RequestParam("docCode") String docCode,
            @RequestParam("docId") Long docId,
            @RequestPart("docData") String docDataJson,
            @RequestPart(value = "attachFile", required = false) MultipartFile attachFile) {

        // 요청 파라미터 검증
        if (docCode == null || docCode.isBlank()) {
            return ResponseEntity.badRequest().body("docCode 누락");
        }
        if (docId == null) {
            return ResponseEntity.badRequest().body("docId 누락");
        }

        try {
            // JSON → Map 변환
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> screenData = mapper.readValue(docDataJson, new TypeReference<>() {
            });

            // 화면 데이터를 DB 컬럼명으로 매핑 + 필터링
            Map<String, Object> dbData = docManagerService.mapAndFilterScreenData(
                    docManagerService.getDocTableName(docCode),
                    screenData);

            // 1. 문서 업데이트 (UPDATE)
            String docTableName = docManagerService.getDocTableName(docCode);
            docManagerService.updateDocument(docTableName, docId, dbData);

            // 문서의 상태가 draft라면 아래 코드 실행 안함
            String status = (String) dbData.get("doc_status");

            if ("draft".equalsIgnoreCase(status)) {
                docApproverService.updateApprovalStatusAndComment(docId, docCode, "DRAFT");
            } else {
                docApproverService.updateApprovalStatusAndComment(docId, docCode, "PENDING");
            }

            // 2. 기존 첨부파일 삭제 및 새 파일 저장
            if (attachFile != null && !attachFile.isEmpty()) {
                // 기존 첨부파일 삭제
                docManagerService.deleteExistingFiles(docId);

                // 새 파일 저장
                Path baseDir = Paths.get(docDir, docCode);
                Files.createDirectories(baseDir);

                String originalFileName = attachFile.getOriginalFilename();
                
                // 확장자 추출
                String ext = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                }

                // UUID + 확장자
                String savedFileName = UUID.randomUUID().toString() + ext;

                // 실제 물리 경로
                Path savePath = baseDir.resolve(savedFileName);

                // 파일 저장
                attachFile.transferTo(savePath.toFile());

                // DB 저장용 상대 경로
                String dbFilePath = Paths.get(docCode, savedFileName).toString();
                // System.out.println("updateFile > " + dbFilePath);
                
                // 파일 정보를 DB에 저장
                Map<String, Object> fileData = new HashMap<>();
                fileData.put("doc_id", docId);
                fileData.put("doc_type", docCode);
                fileData.put("file_name", originalFileName);
                fileData.put("file_path", dbFilePath.toString());
                fileData.put("file_size", attachFile.getSize());
                fileData.put("created_at", LocalDateTime.now());

                docManagerService.saveFileInfo(fileData);
            }

            return ResponseEntity.ok("업데이트 완료");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("업데이트 실패: " + e.getMessage());
        }
    }

    // 특정문서 삭제
    @DeleteMapping("/deleteonedoc/{docCode}/{docId}")
    @ResponseBody
    public ResponseEntity<?> deleteUserDoc(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            @PathVariable("docCode") String docCode,
            @PathVariable("docId") Long docId) {
        // 로그인 체크
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        try {
            docManagerService.deleteOneDoc(docCode, docId);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("문서 삭제 중 오류가 발생했습니다.");
        }
    }

}
