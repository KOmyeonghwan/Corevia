package com.example.corenet.client.board.controller;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.corenet.admin.board.dto.BoardContentDetailDTO;
import com.example.corenet.client.board.service.BoardService;
import com.example.corenet.common.dto.LoginUserDTO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor

public class BoardController {

    private final BoardService boardService;

    @Value("${boardfile.upload.path}")
    private String uploadDir;

    
    @GetMapping("/user/board/{boardCode}/{boardId}/attachment")
    public void downloadAttachment(
            @PathVariable(value = "boardId") Long boardId,
            @PathVariable(value = "boardCode") String boardCode,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            HttpServletResponse response) {

        try {
            BoardContentDetailDTO board = boardService.getUserBoardDetailAndFile(boardCode, boardId);

            if (board.getFileUrl() == null ||board.getFileUrl().isBlank()) {
                throw new RuntimeException("첨부파일 없음");
            }

            Path file = Paths.get(uploadDir, board.getFileUrl()); 

            if (!Files.exists(file)) {
                throw new RuntimeException("파일 없음");
            }

            response.setContentType("application/octet-stream");

            String encodedFileName = URLEncoder.encode(
                board.getFileName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" + encodedFileName + "\"");

            Files.copy(file, response.getOutputStream());
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
