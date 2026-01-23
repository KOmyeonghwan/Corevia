package com.example.corenet;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        

        private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
        private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        public SecurityConfig(CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
                        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
                this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
                this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                //  CSRF 활성화 (로그인만 예외)
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/login"))

                                .authorizeHttpRequests(auth -> auth

                                                //  공통 정적 리소스
                                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                                                .permitAll()

                                                //  user 정적 리소스
                                                .requestMatchers(
                                                                "/user/css/**",
                                                                "/user/js/**",
                                                                "/user/images/**")
                                                .permitAll()

                                                //  admin 정적 리소스
                                                .requestMatchers(
                                                                "/admin/*.css",
                                                                "/admin/*.js",
                                                                "/admin/images/**")
                                                .permitAll()

                                                // 로그인
                                                .requestMatchers("/login").permitAll()

                                                //  회원가입
                                                .requestMatchers("/register").permitAll()

                                                // 회원가입
                                                .requestMatchers("/findId").permitAll()

                                                //  관리자 페이지
                                                .requestMatchers("/admin/**")
                                                .hasRole("ADMIN")

                                                //  사용자 페이지
                                                .requestMatchers("/user/**")
                                                .hasAnyRole("USER", "ADMIN")

                                                // 나머지
                                                .anyRequest().authenticated())

                                // Spring Security 기본 로그인 사용 안 함

                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login") // POST
                                                .usernameParameter("userId")
                                                .passwordParameter("password")
                                                .failureHandler(customAuthenticationFailureHandler)
                                                .successHandler(customAuthenticationSuccessHandler))

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))

                                //  Basic Auth 비활성화
                                .httpBasic(basic -> basic.disable())

                                //  세션 기반 인증
                                .sessionManagement(session -> session
                                                .maximumSessions(1));

                return http.build();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider(
                        CustomUserDetailsService customUserDetailsService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(customUserDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

}

