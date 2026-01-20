package com.example.corenet.common.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.corenet.common.service.LoginService;
import com.example.corenet.common.dto.DepartmentIdAndName;
import com.example.corenet.common.dto.FindIdRequestDTO;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.common.service.FindUserInfoService;
import com.example.corenet.common.service.LoginDepartmentsNameService;

import com.example.corenet.entity.Department;
import com.example.corenet.entity.Position;
import com.example.corenet.entity.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private LoginDepartmentsNameService loginDepartmentsNameService;

    @Autowired
    private FindUserInfoService findUserInfoService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 부서 가져오기
    @ModelAttribute("departments")
    public List<DepartmentIdAndName> addDepartments() {
        return loginDepartmentsNameService.getAllDepartments(); // Mustache 템플릿에서 {{#departments}} ... {{/departments}}
                                                                // 사용
    }

    // 사용자 홈
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "user/login"; // Mustache 템플릿에서 {{#departments}} ... {{/departments}} 사용
    }

    // 아이디 찾기
    @PostMapping("/findId")
    public String findId(FindIdRequestDTO form, Model model) {
        Optional<User> userOpt = findUserInfoService.findUserId(form.getName(), form.getFullEmail());

        if (userOpt.isPresent()) {
            model.addAttribute("resultMessage", "당신의 아이디는 <b>" + userOpt.get().getUserId() + "</b> 입니다.</br>");
        } else {
            model.addAttribute("resultMessage", "일치하는 정보가 없습니다.");
        }
        return "user/login";
    }

    // 회원가입
    @PostMapping("/register")
    @ResponseBody 
    public Map<String, Object> registerUser(
            @RequestParam("userId") String userId,
            @RequestParam("email") String email,
            @RequestParam("emailDomain") String emailDomain,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam(value = "department_id", required = false) Integer department_id,
            @RequestParam(value = "position_id", required = false) Integer position_id) {
        Map<String, Object> result = new HashMap<>();

        try {
            String fullEmail = email + "@" + emailDomain;

            if (loginService.isUserIdTaken(userId)) {
                result.put("success", false);
                result.put("message", "이미 사용 중인 아이디입니다.");
                return result;
            }

            if (loginService.isEmailTaken(fullEmail)) {
                result.put("success", false);
                result.put("message", "이미 사용 중인 이메일입니다.");
                return result;
            }

            // DB에서 실제 Position 조회
            Position position = (position_id != null)
                    ? loginService.getPositionById(position_id)
                    : loginService.getPositionById(5); // 기본 사원

            Integer jobcode = (department_id != null) ? loginService.generateJobcode(department_id) : null;

            String companyEmail = jobcode + "@corenet.com";

            User user = User.builder()
                    .userId(userId)
                    .email(fullEmail)
                    .userName(name)
                    .phone(phone)
                    .password(passwordEncoder.encode(password))
                    .companyEmail(companyEmail)
                    .role(2) // 일반 회원
                    .department(department_id != null ? Department.builder().id(department_id).build() : null)
                    .position(position)
                    .jobcode(jobcode)
                    .build();

            loginService.registerUser(user);

            result.put("success", true);
            result.put("message", "회원가입 성공");
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", e.getMessage());
            return result;
        }
    }

}
