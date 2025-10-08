package com.example.corenet.client.users.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.corenet.common.dto.CustomUserDetails;
import com.example.corenet.common.entity.User;

@Controller
public class UserShowPageController {

    // 사용자 메인
    @GetMapping("/usermain")
    public String showUserMainPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User user = userDetails.getUser();
        model.addAttribute("loginUser", user);
        return "user/main";
    }

    // 사용자 메시지
    @GetMapping("/usermessage")
    public String showUserMessage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/message";
    }

    // 사용자 스케줄
    @GetMapping("/userschedule")
    public String showUserSchedule(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/schedule";
    }

    // 사용자 메일
    @GetMapping("/usermail")
    public String showUserMail(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/user-mail";
    }

    // 사용자 메일 디테일
    @GetMapping("/usermaildetail")
    public String showUserMailDetail(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/user-mail-detail";
    }

    // 사용자 보드
    @GetMapping("/userboard")
    public String showUserBoard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/board";
    }

    // 사용자 보드 디테일
    @GetMapping("/userboarddetail")
    public String showUserBoardDetail(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/board-detail";
    }

    // 사용자 보드 디테일 편집
    @GetMapping("/userboarddetailedit")
    public String showUserBoardDetailEdit(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/board-detail-edit";
    }

    // 사용자 전자결재
    @GetMapping("/userdoc")
    public String showUserDoc(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/doc";
    }

    // 사용자 전자결재 디테일
    @GetMapping("/userdocdetail")
    public String showUserDocDetail(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/doc-detail";
    }

    // 사용자 전자결재 편집
    @GetMapping("/userdocedit")
    public String showUserDocEdit(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        model.addAttribute("loginUser", userDetails.getUser());
        return "user/doc-edit";
    }

    // ===================
    // 채팅 모달
    @GetMapping("/chat-modal")
    public String showChatModal(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("loginUser", userDetails != null ? userDetails.getUser() : null);
        return "user/includes/chat_modal";
    }

    // 새 채팅
    @GetMapping("/new_chat")
    public String showNewChat() {
        return "user/includes/new_chat";
    }

    // ===================

    // // 로그아웃
    // @PostMapping("/logout")
    // public String logout(HttpSession session) {
    // session.invalidate();
    // return "redirect:/login";
    // }

}
