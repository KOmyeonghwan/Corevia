package com.example.corenet.common.security;

import com.example.corenet.admin.users.entity.Users;
import com.example.corenet.common.dto.LoginUserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Users user;  // user entity 주입

    public CustomUserDetails(Users user) {
        this.user = user;
    }

    public LoginUserDTO toLoginUserDTO() {
        return LoginUserDTO.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .jobcode(user.getJobcode())
                .role(user.getRole())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole() == 0 ?
                List.of(() -> "ROLE_ADMIN") :
                List.of(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getUserId(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
