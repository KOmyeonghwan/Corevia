package com.example.corenet.admin.doc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamLeaderDataDTO {
    private String userName;      
    private Integer departmentId;   // 부서 ID
    private String employeeNo;      // 사원 번호
    private String positionName;     // 직책 이름
}
