package com.example.corenet;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.corenet.admin.log.repo.SecurityLogRepository;
import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.entity.SecurityLog;
import com.example.corenet.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final SecurityLogRepository securityLogRepository;
    private final UsersRepository usersRepository;

    public CustomAuthenticationFailureHandler(SecurityLogRepository securityLogRepository,
            UsersRepository usersRepository) {
        this.securityLogRepository = securityLogRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String userId = request.getParameter("userId");
        User user = usersRepository.findByUserId(userId).orElse(null);

        SecurityLog log = SecurityLog.builder()
                .user(user)
                .eventType(SecurityLog.EventType.login_failure)
                .eventDescription("비밀번호 3회 실패")
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .pageUrl(request.getRequestURI())
                .createdAt(LocalDateTime.now())
                .build();

        securityLogRepository.save(log);

        response.sendRedirect("/login?error");
    }
}
