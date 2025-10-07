package com.example.corenet.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginUserDTO {
    private Integer id;
    private String userId;
    private String userName;
    private String email;
    private Integer jobcode;
    private Integer role; // 0=관리자, 1=사용자
}
