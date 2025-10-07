package com.example.corenet.admin.users.service;

import com.example.corenet.admin.users.entity.Users;
import com.example.corenet.admin.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    @Transactional
    public Users registerUser(Users user) {
        // 비밀번호 암호화
        user.encryptPassword();
        return usersRepository.save(user);
    }

    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    public boolean isUserIdTaken(String userId) {
        return usersRepository.findByUserId(userId).isPresent();
    }

    public Integer generateJobcode(Integer departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("부서가 지정되지 않아 jobcode를 생성할 수 없습니다.");
        }

        // 부서별 최대 jobcode 조회
        Integer maxJobcode = usersRepository.findMaxJobcodeByDepartment(departmentId);

        int deptCode = departmentId; // 예: 101
        int newNumber;

        if (maxJobcode == null) {
            newNumber = 1; // 첫 번째 사원
        } else {
            // 마지막 3자리 추출
            int lastNumber = maxJobcode % 1000;
            newNumber = lastNumber + 1;
        }

        // 부서코드 + 부서내 순번 3자리 합치기
        return deptCode * 1000 + newNumber; // 예: 101001, 101002 ...
    }

    public Users findByUserId(String userId) {
        return usersRepository.findByUserId(userId).orElse(null);
    }

    public List<Users> findAllUsers() {
        return usersRepository.findAll();
    }

    @Transactional
    public void deleteUserById(Integer id) {
        usersRepository.deleteById(id);
    }


    public List<Users> findByUserName(String name) {
        return usersRepository.findByUserNameContaining(name);
    }

    public List<Users> findByDepartmentName(String deptName) {
        return usersRepository.findByDepartment_DepartmentNameContaining(deptName);
    }  

    

}
