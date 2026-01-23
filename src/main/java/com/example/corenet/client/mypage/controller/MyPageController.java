package com.example.corenet.client.mypage.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.corenet.admin.user.service.UsersService;
import com.example.corenet.client.mypage.service.MypageService;
import com.example.corenet.common.dto.EmailChangeDTO;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.common.dto.PasswordChangeDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/mypage")
@RequiredArgsConstructor
public class MyPageController {

        private final UsersService usersService;
        private final MypageService mypageService;

        @PostMapping("/password")
        public Map<String, Object> changePassword(
                        @RequestBody PasswordChangeDTO dto,
                        @SessionAttribute("loginUser") LoginUserDTO loginUser, HttpServletRequest request) {

                usersService.changePassword(
                                loginUser.getUserPk(),
                                dto.getNewPassword(),
                                request);

                // 세션 무효화
                request.getSession().invalidate();

                return Map.of(
                                "success", true,
                                "message", "비밀번호가 변경되었습니다.");
        }

        @PostMapping("/email")
        public Map<String, Object> changeEmail(
                        @RequestBody EmailChangeDTO dto,
                        @SessionAttribute("loginUser") LoginUserDTO loginUser) {

                usersService.changeEmail(loginUser.getUserPk(), dto.getEmail());

                // 세션 정보도 업데이트
                loginUser.setEmail(dto.getEmail());

                return Map.of(
                                "success", true,
                                "message", "이메일이 변경되었습니다.");
        }

}
