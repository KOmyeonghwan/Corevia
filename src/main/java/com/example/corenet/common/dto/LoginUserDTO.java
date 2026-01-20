package com.example.corenet.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDTO {
    private Integer userPk; // id에서 userPk로 수정함(.getId할 때 다른 세션과 이름 충돌남)
    private String userId;
    private String userName;
    private String email;
    private String phone;
    private String companyEmail;

    private Integer jobcode;

    private Integer role; // 0=관리자, 2=사용자

    private Integer positionLevel; // 추가 0~2까지 관리자 권한 페이지 3,4는 사용자 페이지 10,11은 관리자 페이지로

    private Integer position_id; // 포지션 아이디 값은 필요 없을거 같음 ...
    private Integer department_id;
    private String positionTitle; // position_title
    private String departmentName; // department_name

    private String loginDateTime;
}
