package com.example.corenet.common.security;

import com.example.corenet.common.dto.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
                                        throws IOException, ServletException {

        // 안전하게 principal 가져오기
        Object principal = authentication.getPrincipal();

        String redirectUrl = "/usermain"; // 기본 사용자 페이지

        if (principal instanceof CustomUserDetails userDetails) {
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            boolean isUser = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

            if (isAdmin) {
                redirectUrl = "/admindashboard";
            } else if (isUser) {
                redirectUrl = "/usermain";
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
