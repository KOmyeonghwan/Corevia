package com.example.corenet.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageNumberDTO {
    private int number;
    private boolean isCurrent;
}
