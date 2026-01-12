package com.example.corenet.admin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // 모든 필드 초기화 생성자
@NoArgsConstructor  // 기본 생성자
public class TodayUsersDTO {
    private String userName;
    private String companyEmail;
    private String todayDepartmentName;
}
