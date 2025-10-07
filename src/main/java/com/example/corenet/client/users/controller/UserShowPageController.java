package com.example.corenet.client.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.corenet.admin.users.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserShowPageController {

    // 사용자 홈
    @GetMapping("/")
    public String showFirst(HttpSession session, Model model) {
        return "user/login";
    }

    // 사용자 메인
    // @GetMapping("/usermain")
    // public String showUserMainPage(HttpSession session, Model model) {
    //     Users user = (Users) session.getAttribute("loginUser");
    //     if (user == null)
    //         return "redirect:/login";

    //     model.addAttribute("loginUser", user);
    //     return "user/main";
    // }

    // 사용자 메시지
    @GetMapping("/usermessage")
    public String showUserMessage(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/message";
    }

    // 사용자 스케쥴
    @GetMapping("/userschedule")
    public String showUserSchedule(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/schedule";
    }

    // 사용자 메일
    @GetMapping("/usermail")
    public String showUserMail(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/user-mail";
    }

    // 사용자 메일 디테일
    @GetMapping("/usermaildetail")
    public String showUserMailDetail(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/user-mail-detail";
    }

    // 사용자 보드
    @GetMapping("/userboard")
    public String showUserBoard(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/board";
    }

    // 사용자 보드 디테일
    @GetMapping("/userboarddetail")
    public String showUserBoardDetail(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/board-detail";
    }

    // 사용자 보드 디테일
    @GetMapping("/userboarddetailedit")
    public String showUserBoardDetailEdit(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/board-detail-edit";
    }

    // 사용자 전자결재
    @GetMapping("/userdoc")
    public String showUserDoc(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/doc";
    }
    // 사용자 전자결재 디테일
    @GetMapping("/userdocdetail")
    public String showUserDocDetail(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/doc-detail";
    }
    // 사용자 전자결재 edit
    @GetMapping("/userdocedit")
    public String showUserDocEdit(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("loginUser", user);
        return "user/doc-edit";
    }

    // ==================

    // 채탕 모달
    @GetMapping("/chat-modal")
    public String showChatModal(HttpSession session, Model model) {
        Users user = (Users) session.getAttribute("loginUser");
        model.addAttribute("loginUser", user);
        return "user/includes/chat_modal";
    }

    // 뉴 채팅
    @GetMapping("/new_chat")
    public String showNewChat() {
        return "user/includes/new_chat";
    }

    // ===================

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
