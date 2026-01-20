package com.example.corenet.client.board.service;

import org.springframework.stereotype.Service;

import com.example.corenet.admin.board.dto.BoardContentDetailDTO;
import com.example.corenet.admin.board.repo.BoardContentRepository;
import jakarta.transaction.Transactional;

@Service
public class BoardService {

    private final BoardContentRepository boardContentRepository;

    public BoardService(
            BoardContentRepository boardContentRepository) {
        this.boardContentRepository = boardContentRepository;
    }

    // 게시판 파일 다운
    @Transactional
    public BoardContentDetailDTO getUserBoardDetailAndFile(
            String boardCode,
            Long id) {

        BoardContentDetailDTO board = boardContentRepository.findUserBoardDetail(boardCode, id);

        return board;
    }

}
