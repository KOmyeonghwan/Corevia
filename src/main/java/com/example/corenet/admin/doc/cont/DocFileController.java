package com.example.corenet.admin.doc.cont;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.corenet.admin.board.dto.BoardContentDetailDTO;
import com.example.corenet.admin.doc.dto.DocFileDTO;
import com.example.corenet.admin.doc.entity.DocFile;
import com.example.corenet.admin.doc.repo.DocFileRepository;
import com.example.corenet.admin.doc.serv.DocFileService;
import com.example.corenet.common.dto.LoginUserDTO;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class DocFileController {

    @Autowired
    private DocFileService docFileService;

    @Autowired
    private DocFileRepository docFileRepository;

    @Value("${doc.upload.path}")
    private String docDir;

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

    // 전자결재 파일 다운로드
    @GetMapping("/doc/{docCode}/{docId}/attachment")
    public void downloadAttachment(
            @PathVariable("docCode") String docType,
            @PathVariable("docId") Long docId,
            HttpServletResponse response) {

        try {
            // 첫 번째 파일 조회
            DocFile docFile = docFileRepository
                    .findFirstByDocTypeAndDocId(docType, docId) // Optional<DocFile>
                    .orElseThrow(() -> new RuntimeException("첨부파일 없음"));

            // 실제 파일 경로
            Path realPath = Paths.get(docDir)
                    .resolve(docFile.getFilePath())
                    .normalize();

            System.out.println("DOWNLOAD FILE > " + realPath);

            if (!Files.exists(realPath)) {
                throw new RuntimeException("파일이 서버에 존재하지 않음");
            }

            // 다운로드용 응답 헤더 설정
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(Files.size(realPath));

            String encodedFileName = URLEncoder.encode(
                    docFile.getFileName(),
                    StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" + encodedFileName + "\"");

            // 파일 스트림 복사
            Files.copy(realPath, response.getOutputStream());
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
