package com.example.corenet.common.advuce;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.corenet.common.dto.CustomUserDetails;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public CustomUserDetails getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails;
    }
    //{{currentUser.필드명}} 
}
