package com.example.corenet.common.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    public String registerUser(
            @RequestParam("userId") String userId,
            @RequestParam("email") String email,
            @RequestParam("emailDomain") String emailDomain,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam(value = "department_id", required = false) Integer department_id,
            @RequestParam(value = "position_id", required = false) Integer position_id,

            Model model,
            HttpServletResponse response) {

        String fullEmail = email + "@" + emailDomain;

        if (loginService.isEmailTaken(fullEmail)) {
            model.addAttribute("error", "이미 사용 중인 이메일입니다.");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return "redirect:/";
        }

        if (loginService.isUserIdTaken(userId)) {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return "redirect:/";
        }

        // DB에서 실제 Position 조회
        Position position;
        if (position_id != null) {
            position = loginService.getPositionById(position_id);
        } else {
            position = loginService.getPositionById(5); // 기본 사원
        }

        Integer jobcode = department_id != null ? loginService.generateJobcode(department_id) : null;

        // [[jobcode 기반 회사 이메일 생성]]
        String companyEmail = jobcode + "@corenet.com";

        // [[jobcode로 폴더 생성]] -> 직급 변경시 승인 도장 저장이 필요하다면, authorizedSeal 폴더 생성 필요
        if (jobcode != null) {
            String basePath = "src/main/java/com/example/corenet/db/" + jobcode;
            File jobcodeFolder = new File(basePath);

            if (!jobcodeFolder.exists()) {
                jobcodeFolder.mkdirs();
            }

            String[] subFolders = { "email", "doc" };
            for (String sub : subFolders) {
                File subFolder = new File(basePath + "/" + sub);
                if (!subFolder.exists()) {
                    subFolder.mkdirs();
                }
            }
        }

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

        return "redirect:/login";
    }

    // 로그인(LoginDTO 사용)
    // @PostMapping("/login")
    // public String loginUser(
    //         @RequestParam("userId") String userId,
    //         @RequestParam("password") String password,
    //         HttpServletResponse response,
    //         Model model,
    //         HttpSession session) {

    //     User user = loginService.findByUserId(userId);

    //     if (!userId.isEmpty() && user == null) {
    //         model.addAttribute("error", "존재하지 않는 사용자입니다.");
    //         return "user/login";
    //     }

    //     if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
    //         model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
    //         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    //         return "user/login";
    //     }

    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
    //     String formattedLoginTime = LocalDateTime.now().format(formatter);

    //     LoginUserDTO loginUserDTO = LoginUserDTO.builder()
    //             .userPk(user.getId())
    //             .userId(user.getUserId())
    //             .userName(user.getUserName())
    //             .email(user.getEmail())
    //             .phone(user.getPhone())
    //             .companyEmail(user.getCompanyEmail())
    //             .role(user.getRole())
    //             .position_id(user.getPosition() != null ? user.getPosition().getId() : null)
    //             .department_id(user.getDepartment() != null ? user.getDepartment().getId() : null)
    //             .positionLevel(user.getPosition() != null ? user.getPosition().getLevel() : null)
    //             .positionTitle(user.getPosition().getPositionTitle())
    //             .departmentName(user.getDepartment().getDepartmentName())
    //             .jobcode(user.getJobcode())
    //             .loginDateTime(formattedLoginTime)
    //             .build();

    //     session.setAttribute("loginUser", loginUserDTO);

    //     Integer positionLevel = user.getPosition() != null ? user.getPosition().getLevel() : 5;

    //     String role;
    //     if (positionLevel <= 2 || positionLevel >= 10) {
    //         role = "ROLE_ADMIN";
    //     } else {
    //         role = "ROLE_USER";
    //     }

    //     UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
    //             loginUserDTO,
    //             null,
    //             List.of(new SimpleGrantedAuthority(role)));

    //     SecurityContextHolder.getContext().setAuthentication(authentication);

    //     session.setAttribute(
    //             "SPRING_SECURITY_CONTEXT",
    //             SecurityContextHolder.getContext());

    //     if ("ROLE_ADMIN".equals(role)) {
    //         return "redirect:/admindashboard";
    //     } else {
    //         return "redirect:/usermain";
    //     }
    // }

}
