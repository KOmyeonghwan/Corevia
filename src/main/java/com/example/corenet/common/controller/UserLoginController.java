package com.example.corenet.common.controller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.corenet.common.entity.*;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.common.service.UserLoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserLoginController {
    private final UserLoginService userLoginService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String showFirstPage() {
        return "user/login"; 
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login";
    }

    // POST
    @PostMapping("/login")
    public String loginUser(
            @RequestParam("userId") String userId,
            @RequestParam("password") String password,
            HttpServletResponse response,
            Model model,
            HttpSession session) {

        Optional<User> user = userLoginService.findByUserId(userId);

        if (user.isPresent() || !passwordEncoder.matches(password, user.get().getPassword())) {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "user/login";
        }

        // 접속 시간 처리
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String datetime = now.format(formatter);

        System.out.println(datetime);

        session.setAttribute("loginUser", user);

        // role 기준으로 분기
        if (user.get().getRole() == 0) { // 관리자
            return "redirect:/admindashboard"; 
        } else { // 일반 사용자
            return "redirect:/usermain";
        }
    }

}
