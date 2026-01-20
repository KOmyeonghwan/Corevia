package com.example.corenet.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardMangerDTO {
    private String boardCode;
    private String boardName;
    private Integer deptCode;
}
