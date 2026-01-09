package com.example.corenet.common.service;


import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetPwService {
    // private final ResetPwRepository resetPwRepository;
    // private final EmailService emailService;
    // private final PasswordEncoder passwordEncoder;

    // public boolean sendResetLink(String userId, String email) {
    // Optional<Users> userOpt =
    // resetPwRepository.findByUserIdAndcompanyEmail(userId, email);

    // if (userOpt.isPresent()) {
    // String resetLink = "https://yourdomain.com/resetPwForm?user=" + userId;
    // String subject = "[CoReNet] 비밀번호 재설정 안내";
    // String content = "<p>안녕하세요 " + userOpt.get().getUserName() + "님.</p>"
    // + "<p>비밀번호를 재설정하려면 아래 링크를 클릭하세요.</p>"
    // + "<a href='" + resetLink + "'>비밀번호 재설정</a>";

    // return emailService.sendHtmlEmail(email, subject, content);
    // } else {
    // return false;
    // }
    // }

    // public boolean resetPassword(String token, String newPassword) {
    //     Optional<Users> userOpt = resetPwRepository.findByResetToken(token);
    //     if (userOpt.isEmpty())
    //         return false;

    //     Users user = userOpt.get();
    //     if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
    //         return false; // 토큰 만료
    //     }

    //     user.setPassword(passwordEncoder.encode(newPassword));
    //     user.setResetToken(null);
    //     user.setResetTokenExpiry(null);
    //     usersRepository.save(user);
    //     return true;
    // }

}
