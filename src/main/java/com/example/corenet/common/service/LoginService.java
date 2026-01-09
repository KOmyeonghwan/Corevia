package com.example.corenet.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.corenet.admin.user.repository.PositionRepository;
import com.example.corenet.common.repository.LoginRepository;
import com.example.corenet.entity.Position;
import com.example.corenet.entity.User;

@Service
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PositionRepository positionRepository;


    @Transactional
    public User registerUser(User user) {
        return loginRepository.save(user);
    }

    public boolean isEmailTaken(String email) {
        return loginRepository.findByEmail(email).isPresent();
    }

    public boolean isUserIdTaken(String userId) {
        return loginRepository.findByUserId(userId).isPresent();
    }

    public Integer generateJobcode(Integer departmentId) {
        if (departmentId == null) {
            throw new IllegalArgumentException("부서가 지정되지 않아 jobcode를 생성할 수 없습니다.");
        }

        // 부서별 최대 jobcode 조회
        Integer maxJobcode = loginRepository.findMaxJobcodeByDepartment(departmentId);

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

    public User findByUserId(String userId) {
        return loginRepository.findByUserId(userId).orElse(null);
    }

    public List<User> findAllUsers() {
        return loginRepository.findAll();
    }

    public List<User> findByUserName(String name) {
        return loginRepository.findByUserNameContaining(name);
    }

    public List<User> findByDepartmentName(String deptName) {
        return loginRepository.findByDepartment_DepartmentNameContaining(deptName);
    }

    public Page<User> findByUserName(String name, Pageable pageable) {
        return loginRepository.findByUserNameContaining(name, pageable);
    }

    public Page<User> findByDepartmentName(String deptName, Pageable pageable) {
        return loginRepository.findByDepartment_DepartmentNameContaining(deptName, pageable);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return loginRepository.findAll(pageable);
    }

    public Position getPositionById(Integer positionId) {
        return positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("직책을 찾을 수 없습니다. id=" + positionId));
    }

}
