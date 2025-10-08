package com.example.corenet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.corenet.common.security.CustomLoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable()); // csrf 공격 방지 비활성

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                            "/login", "/user/login", "/css/**", "/js/**", "/images/**", "/**" // 추가
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/user/**").hasAuthority("ROLE_USER")
                        .requestMatchers("/db/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .anyRequest().permitAll());

        http
                .formLogin(form -> form
                        .loginPage("/login") // 사용자 정의 로그인
                        .usernameParameter("userId") // 여기서 name="userId"와 매핑
                        .passwordParameter("password")
                        .successHandler(customLoginSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll());

        http
                .sessionManagement((auth) -> auth
                        .maximumSessions(2)
                        .maxSessionsPreventsLogin(true) // 새로운 로그인 차단
                );

        http
                .sessionManagement((auth) -> auth
                        .sessionFixation().changeSessionId() // 세션 고정 보호
                );

        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login"));
        /*
         * http
         * .csrf().disable() // CSRF 비활성화
         * .authorizeHttpRequests(auth -> auth
         * .anyRequest().permitAll() // 모든 요청 허용
         * )
         * .formLogin().disable() // 로그인 화면 비활성화
         * .httpBasic().disable(); // 기본 인증 비활성화
         */
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }

}