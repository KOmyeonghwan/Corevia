package com.example.corenet.common.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.corenet.common.repository.FindUserInfoRepository;
import com.example.corenet.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindUserInfoService {
    private final FindUserInfoRepository findUserInfoRepository;

    public Optional<User> findUserId(String name, String email){
        return findUserInfoRepository.findByUserNameAndEmail(name, email);
    }
}
