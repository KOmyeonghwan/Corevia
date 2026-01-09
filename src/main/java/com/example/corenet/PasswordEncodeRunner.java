package com.example.corenet;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncodeRunner {
    @Bean
    CommandLineRunner passwordEncodeRunnerTemp(PasswordEncoder passwordEncoder) {
        return args -> {
            String encoded = passwordEncoder.encode("1234");
            // System.out.println("BCrypt 결과값 :오른쪽을_가리키는_손_모양: " + encoded);
        };
    }
}