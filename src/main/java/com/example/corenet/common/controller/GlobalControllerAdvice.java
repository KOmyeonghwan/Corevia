package com.example.corenet.common.controller;

import com.example.corenet.common.dto.LoginUserDTO;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // 모든 컨트롤러에 적용되는 전역 설정
public class GlobalControllerAdvice {

    private final HttpSession session;

    public GlobalControllerAdvice(HttpSession session) {
        this.session = session;
    }

    // 로그인 사용자 전역 노출
    @ModelAttribute("loginUser")
    public LoginUserDTO addLoginUserToModel() {
        return (LoginUserDTO) session.getAttribute("loginUser");
    }

    // 시스템 관리자 여부 전역 노출
    @ModelAttribute("isSystemAdmin")
    public boolean addIsSystemAdmin() {
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("loginUser");

        return loginUser != null && loginUser.getPosition_id() == 6;
    }

    //  CSRF 토큰 전역 노출 (Mustache용)
    @ModelAttribute("_csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
