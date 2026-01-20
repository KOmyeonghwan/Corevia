package com.example.corenet.admin.doc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocManagerDTO {

    private String docCode;  
    private String docName;  
    private Integer deptCode; 
}