package com.example.corenet.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.corenet.entity.User;

public interface FindUserInfoRepository extends JpaRepository<User, Integer> {
    
    // 아이디 찾기
    Optional<User> findByUserNameAndEmail(String userName, String email);

}
