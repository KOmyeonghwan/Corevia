package com.example.corenet.admin.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.corenet.admin.users.entity.Department;
import com.example.corenet.admin.users.entity.Position;
import com.example.corenet.admin.users.entity.Users;
import com.example.corenet.admin.users.service.UsersService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {

    @Autowired
    private UsersService usersService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String userId,
            @RequestParam String email,
            @RequestParam String emailDomain,
            @RequestParam String name,
            @RequestParam(required = false) String phone,
            @RequestParam String password,
            @RequestParam(required = false) Integer department_id,
            @RequestParam(required = false) Integer position_id,
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
                .userName(name)
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

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String userId,
            @RequestParam String password,
            HttpServletResponse response,
            Model model,
            HttpSession session) {

        Users user = usersService.findByUserId(userId);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "user/login";
        }

        session.setAttribute("loginUser", user);

        // role 기준으로 분기
        if (user.getRole() == 0) { // 관리자
            return "redirect:/admindashboard";
        } else { // 일반 사용자
            return "redirect:/usermain";
        }
    }

    @PostMapping("/adminuser/delete/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Integer id) {
        try {
            usersService.deleteUserById(id);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }

}