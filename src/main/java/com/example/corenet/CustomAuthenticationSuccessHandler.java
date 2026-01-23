package com.example.corenet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.corenet.admin.log.serv.SecurityLogService;
import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.entity.SecurityLog;
import com.example.corenet.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler
                implements AuthenticationSuccessHandler {

        private final UsersRepository usersRepository;

        private final SecurityLogService securityLogService;

        @Override
        public void onAuthenticationSuccess(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Authentication authentication) throws IOException {

                System.out.println("XFF = " + request.getHeader("X-Forwarded-For"));
                System.out.println("Proxy-Client-IP = " + request.getHeader("Proxy-Client-IP"));
                System.out.println("WL-Proxy-Client-IP = " + request.getHeader("WL-Proxy-Client-IP"));
                System.out.println("Remote = " + request.getRemoteAddr());

                // 1. 로그인 아이디
                String userId = authentication.getName();

                // 2. 사용자 조회
                User user = usersRepository.findByUserId(userId)
                                .orElseThrow();

                // 로그인 성공 보안 로그 추가
                String ip = IpUtil.getClientIp(request);
                String userAgent = request.getHeader("User-Agent");
                String pageUrl = request.getRequestURI();

                SecurityLog.EventType eventType = SecurityLog.EventType.login_success;

                // 외부 IP 로그인 판별
                if (!ip.startsWith("192.168.") && !ip.startsWith("10.")) {
                        eventType = SecurityLog.EventType.external_ip_login;
                }

                securityLogService.logEvent(
                                user,
                                eventType,
                                "로그인 성공",
                                ip,
                                userAgent,
                                pageUrl);
                // ==============================

                // 3. 로그인 시간
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
                String formattedLoginTime = LocalDateTime.now().format(formatter);

                // 4. LoginUserDTO 생성
                LoginUserDTO loginUserDTO = LoginUserDTO.builder()
                                .userPk(user.getId())
                                .userId(user.getUserId())
                                .userName(user.getUserName())
                                .email(user.getEmail())
                                .phone(user.getPhone())
                                .companyEmail(user.getCompanyEmail())
                                .role(user.getRole())
                                .position_id(user.getPosition() != null
                                                ? user.getPosition().getId()
                                                : null)
                                .department_id(user.getDepartment() != null
                                                ? user.getDepartment().getId()
                                                : null)
                                .positionLevel(user.getPosition() != null
                                                ? user.getPosition().getLevel()
                                                : null)
                                .positionTitle(
                                                user.getPosition() != null
                                                                ? user.getPosition().getPositionTitle()
                                                                : null)
                                .departmentName(
                                                user.getDepartment() != null
                                                                ? user.getDepartment().getDepartmentName()
                                                                : null)
                                .jobcode(user.getJobcode())
                                .loginDateTime(formattedLoginTime)
                                .build();

                // 5. 세션 저장
                request.getSession().setAttribute("loginUser", loginUserDTO);

                if (user.isPasswordResetRequired()) {
                        response.sendRedirect("/user-mypage?forcePwChange=true");
                        return;
                }
                
                // 6. 관리자 판단
                // 관리자 조건: posLevel 0~3, 10,11
                Integer positionLevel = loginUserDTO.getPositionLevel();
                boolean isAdmin = (positionLevel != null) && ((positionLevel >= 0 && positionLevel <= 3)
                                || positionLevel == 10 || positionLevel == 11);

                if (isAdmin) {
                        response.sendRedirect("/admindashboard"); // 관리자 → 대시보드
                } else {
                        response.sendRedirect("/usermain"); // 일반 사용자 → 사용자 페이지
                }
        }

}
