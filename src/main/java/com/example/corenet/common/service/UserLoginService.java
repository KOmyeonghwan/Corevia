package com.example.corenet.common.service;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.corenet.common.entity.User;
import com.example.corenet.common.repository.UserRepository;

@Service
public class UserLoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 사용자 ID로 조회
    public Optional<User> findByUserId(String userId){
        return userRepository.findByUserId(userId);
    }

    // 사용자ID 존재 여부 확인
    public boolean exiexistsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // email로 조회
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    // 비밀번호 변경
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // 사용자 저장
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 사용자 삭제
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    // ID로 사용자 조회
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

}
