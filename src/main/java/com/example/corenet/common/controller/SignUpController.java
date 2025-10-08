package com.example.corenet.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.corenet.admin.users.entity.Department;
import com.example.corenet.admin.users.entity.Position;
import com.example.corenet.admin.users.entity.Users;
import com.example.corenet.admin.users.service.UsersService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class SignUpController {
    @Autowired
    private UsersService usersService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public String registerUser(
            @RequestParam("userId") String userId,
            @RequestParam("email") String email,
            @RequestParam("emailDomain") String emailDomain,
            @RequestParam("name") String userName, // name="name", Users 엔티티는 userName
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam("password") String password,
            @RequestParam(value = "department_id", required = false) Integer department_id,
            @RequestParam(value = "position_id", required = false) Integer position_id,
            Model model,
            HttpServletResponse response) {

        String fullEmail = email + "@" + emailDomain;

        if (usersService.isEmailTaken(fullEmail)) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return "redirect:/";
        }

        if (usersService.isUserIdTaken(userId)) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return "redirect:/";
        }

        // position_id 기본값
        Position position = (position_id != null)
                ? Position.builder().id(position_id).build()
                : Position.builder().id(5).build();

        Integer jobcode = department_id != null ? usersService.generateJobcode(department_id) : null;

        Users user = Users.builder()
                .userId(userId)
                .email(fullEmail)
                .userName(userName)
                .phone(phone)
                .password(passwordEncoder.encode(password)) // BCrypt로 암호화
                .role(2) // 일반 회원
                .department(department_id != null ? Department.builder().id(department_id).build() : null)
                .position(position)
                .jobcode(jobcode)
                .build();

        usersService.registerUser(user);

        return "redirect:/login";
    }
}
