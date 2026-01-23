package com.example.corenet.client.mail.Cont;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.corenet.client.mail.Serv.MailService;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.common.dto.MailDetailDTO;
import com.example.corenet.common.dto.MailSendDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserMailController {

    private final MailService mailService;
    // private final HttpSession session;

    @Value("${email.upload.path}")
    private String uploadDir;

    // 받은 메일함 페이지
    @GetMapping("/usermail")
    public String inbox(@ModelAttribute("loginUser") LoginUserDTO loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 로그인 사용자 이메일로 받은 메일 리스트 가져오기
        model.addAttribute("mailList",
                mailService.getInbox(loginUser.getCompanyEmail()));

        // CSRF 토큰 처리
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken == null) {
            HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
            csrfToken = repo.generateToken(request);
            request.getSession().setAttribute(HttpSessionCsrfTokenRepository.class.getName() + ".CSRF_TOKEN",
                    csrfToken);
        }
        Map<String, String> csrf = new HashMap<>();
        csrf.put("token", csrfToken.getToken());
        csrf.put("headerName", csrfToken.getHeaderName());
        model.addAttribute("_csrf", csrf);

        return "user/user-mail";
    }

    // 메일 전송
    @PostMapping("/user/mail/send")
    @ResponseBody
    public String sendMail(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            MailSendDTO dto,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment) {

        if (loginUser == null) {
            return "unauthorized"; // redirect 쓰면 안 됨 (@ResponseBody)
        }

        String attachmentPath = null;

        try {
            //  여기서 기존 if 블록을 이걸로 "교체"
            if (attachment != null
                    && !attachment.isEmpty()
                    && attachment.getOriginalFilename() != null
                    && !attachment.getOriginalFilename().isBlank()) {

                Files.createDirectories(Paths.get(uploadDir));

                String savedFileName = System.currentTimeMillis() + "_"
                        + attachment.getOriginalFilename();

                Path savePath = Paths.get(uploadDir, savedFileName);
                attachment.transferTo(savePath.toFile());

                attachmentPath = savedFileName;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }

        mailService.sendMail(
                loginUser.getCompanyEmail(),
                loginUser.getUserName(),
                dto,
                attachmentPath);

        return "success";
    }

    // 메일 상세
    @GetMapping("/user/mail/detail/{mailId}")
    public String mailDetail(@ModelAttribute("loginUser") LoginUserDTO loginUser,
            @PathVariable("mailId") Long mailId,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        //  먼저 DB에서 DTO 가져오기
        MailDetailDTO mail = mailService.getMailDetail(mailId, loginUser.getCompanyEmail());

        //  Mustache 안전하게 null 처리
        mail = MailDetailDTO.builder()
                .id(mail.getId())
                .senderEmail(mail.getSenderEmail() != null ? mail.getSenderEmail() : "")
                .senderName(mail.getSenderName() != null ? mail.getSenderName() : "")
                .recipientEmail(mail.getRecipientEmail() != null ? mail.getRecipientEmail() : "")
                .title(mail.getTitle() != null ? mail.getTitle() : "")
                .description(mail.getDescription() != null ? mail.getDescription() : "")
                .attachmentPath(mail.getAttachmentPath() != null ? mail.getAttachmentPath() : "")
                .status(mail.getStatus() != null ? mail.getStatus() : "")
                .createdAt(mail.getCreatedAt())
                .build();

        // 화면에 필요한 데이터 추가
        model.addAttribute("mail", mail);
        model.addAttribute("mailCreatedAt",
                mail.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        model.addAttribute("loginUser", loginUser);

        // 첨부파일 처리
        List<String> attachments = new ArrayList<>();
        if (mail.getAttachmentPath() != null && !mail.getAttachmentPath().trim().isEmpty()) {
            attachments.add(mail.getAttachmentPath());
        }
        model.addAttribute("attachments", attachments);

        //  CSRF 토큰 처리
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken == null) {
            HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
            csrfToken = repo.generateToken(request);
            request.getSession().setAttribute(HttpSessionCsrfTokenRepository.class.getName() + ".CSRF_TOKEN",
                    csrfToken);
        }
        Map<String, String> csrf = new HashMap<>();
        csrf.put("token", csrfToken.getToken());
        csrf.put("headerName", csrfToken.getHeaderName());
        model.addAttribute("_csrf", csrf);

        return "user/user-mail-detail";
    }

    // @GetMapping("/user/mail/download/{fileName}")
    // public void downloadAttachment(
    // @PathVariable String fileName,
    // HttpServletResponse response) {

    // try {
    // Path file = Paths.get(uploadDir, fileName);

    // if (!Files.exists(file)) {
    // throw new RuntimeException("파일 없음");
    // }

    // response.setContentType("application/octet-stream");

    // String encodedFileName = URLEncoder.encode(
    // fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

    // response.setHeader(
    // "Content-Disposition",
    // "attachment; filename=\"" + encodedFileName + "\"");
    // response.setHeader(
    // "Content-Disposition",
    // "attachment; filename=\"" + fileName + "\"");

    // Files.copy(file, response.getOutputStream());
    // response.getOutputStream().flush();

    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    @GetMapping("/user/mail/{mailId}/attachment")
    public void downloadAttachment(
            @PathVariable(value = "mailId") Long mailId,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            HttpServletResponse response) {

        try {
            MailDetailDTO mail = mailService.getMailDetail(mailId, loginUser.getCompanyEmail());

            if (mail.getAttachmentPath() == null || mail.getAttachmentPath().isBlank()) {
                throw new RuntimeException("첨부파일 없음");
            }

            Path file = Paths.get(uploadDir, mail.getAttachmentPath());

            if (!Files.exists(file)) {
                throw new RuntimeException("파일 없음");
            }

            response.setContentType("application/octet-stream");

            String encodedFileName = URLEncoder.encode(
                    mail.getAttachmentPath(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=\"" + encodedFileName + "\"");

            Files.copy(file, response.getOutputStream());
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 보낸 메일함 페이지
    @GetMapping("/usermail/sent")
    public String sentMail(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            HttpServletRequest request,
            Model model) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 내가 보낸 메일 리스트
        model.addAttribute("mailList",
                mailService.getSentMail(loginUser.getCompanyEmail()));

        // 화면에서 구분용
        model.addAttribute("boxType", "sent");

        // 기존 방식 그대로 CSRF 처리
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken == null) {
            HttpSessionCsrfTokenRepository repo = new HttpSessionCsrfTokenRepository();
            csrfToken = repo.generateToken(request);
            request.getSession().setAttribute(
                    HttpSessionCsrfTokenRepository.class.getName() + ".CSRF_TOKEN",
                    csrfToken);
        }
        Map<String, String> csrf = new HashMap<>();
        csrf.put("token", csrfToken.getToken());
        csrf.put("headerName", csrfToken.getHeaderName());
        model.addAttribute("_csrf", csrf);

        return "user/user-mail";
    }

    @PostMapping("/user/mail/delete/{mailId}")
    @ResponseBody
    public String deleteMail(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            @PathVariable("mailId") Long mailId) {

        if (loginUser == null) {
            return "unauthorized";
        }

        mailService.deleteMail(mailId, loginUser.getCompanyEmail());
        return "success";
    }

}
