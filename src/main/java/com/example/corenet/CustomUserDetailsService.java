package com.example.corenet;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UsersRepository usersRepository;

        @Override
        public UserDetails loadUserByUsername(String username)
                        throws UsernameNotFoundException {

                User user = usersRepository.findByUserId(username)
                                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

                String role = (user.getPosition() != null &&
                                (user.getPosition().getLevel() <= 2 || user.getPosition().getLevel() >= 10))
                                                ? "ROLE_ADMIN"
                                                : "ROLE_USER";

                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUserId())
                                .password(user.getPassword()) //  반드시 암호화된 비밀번호
                                .authorities(role)
                                .build();
        }
}
