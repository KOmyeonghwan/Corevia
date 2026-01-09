package com.example.corenet.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.corenet.admin.user.service.UsersService;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.entity.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UsersController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/adminuser/delete/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable("id") Integer id) {
        try {
            usersService.deleteUserById(id);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }

    @PostMapping("/adminuser/update/{id}")
    @ResponseBody
    public String updateUser(
            @PathVariable("id") Integer id,
            @RequestParam(name = "department_id", required = false) Integer departmentId,
            @RequestParam(name = "position_id", required = false) Integer positionId,
            @RequestParam(name = "role", required = false) Integer role,
            @SessionAttribute("loginUser") LoginUserDTO loginUser,
            HttpServletRequest request) {

        try {
            // 🔑 세션에서 관리자 User 조회
            User adminUser = usersService.findById(loginUser.getUserPk().longValue())
                    .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다."));

            usersService.updateUser(
                    id,
                    departmentId,
                    positionId,
                    role,
                    adminUser,
                    request);

            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

}