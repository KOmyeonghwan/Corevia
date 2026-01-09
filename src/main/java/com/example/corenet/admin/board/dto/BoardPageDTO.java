package com.example.corenet.admin.board.dto;

import java.util.ArrayList;
import java.util.List;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPageDTO {

    private List<BoardAdContentDTO> contents;

    private int currentPage;
    private int totalPages;

    private boolean hasNext;
    private boolean hasPrevious;

    private List<PageNumberDTO> pageNumbers;

    public void generatePageNumbers() {
        pageNumbers = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNumbers.add(new PageNumberDTO(i, i == currentPage));
        }
    }

    public int getCurrentPageMinusOne() {
        return currentPage - 1;
    }

    public int getCurrentPagePlusOne() {
        return currentPage + 1;
    }
}
