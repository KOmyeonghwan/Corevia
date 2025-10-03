package com.example.corenet.admin.users.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.corenet.admin.users.entity.Users;
import com.example.corenet.admin.users.service.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ShowPageController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/adminuser")
    public String showAdminUserPage(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Users> userList;

        if (searchType != null && keyword != null && !keyword.isEmpty()) {
            if (searchType.equals("userName")) {
                userList = usersService.findByUserName(keyword);
            } else if (searchType.equals("department")) {
                userList = usersService.findByDepartmentName(keyword);
            } else {
                userList = usersService.findAllUsers();
            }
        } else {
            userList = usersService.findAllUsers();
        }

        // role 기반 isAdmin 필드 추가
        userList.forEach(u -> u.setAdmin(u.getRole() == 0));

        model.addAttribute("users", userList);
        return "admin/adminuser";
    }

    

    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login";
    }

    @GetMapping("/usermain")
    public String showUserMainPage(HttpSession session) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        if (user.getRole() == 0) { // 관리자는 관리자 페이지로
            return "redirect:/admindashboard";
        }
        return "user/main";
    }

    @GetMapping("/admindashboard")
    public String showAdminUserPage(HttpSession session) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        if (user.getRole() != 0) { // 관리자 아니면 일반페이지
            return "redirect:/usermain";
        }
        return "admin/admindashboard";
    }

    @GetMapping("/adapprovallist")
    public String showAdminApprovalList() {
        return "admin/adapprovallist";
    }

    @GetMapping("/adboard")
    public String showAdminBoard() {
        return "admin/adboard";
    }

    @GetMapping("/adboarddetail")
    public String showAdminAdBoardDetail() {
        return "admin/adboarddetail";
    }

    @GetMapping("/adboardedit")
    public String showAdminBoardEdit() {
        return "admin/adboardedit";
    }

    @GetMapping("/adboardwrite")
    public String showAdminBoardWrite() {
        return "admin/adboardwrite";
    }

    @GetMapping("/adcomment")
    public String showAdminComment() {
        return "admin/adcomment";
    }

    @GetMapping("/adcommentdetail")
    public String showAdminCommentDetail() {
        return "admin/adcommentdetail";
    }

    @GetMapping("/adcommentedit")
    public String showAdminCommentDetailEdit() {
        return "admin/adcommentedit";
    }

    @GetMapping("/addepartment")
    public String showAdminDepartment() {
        return "admin/addepartment";
    }

    @GetMapping("/adadpprovaldetail")
    public String showAdminApprovalDetail() {
        return "admin/adpprovaldetail";
    }

}
