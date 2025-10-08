package com.example.corenet.common.repository;

import com.example.corenet.admin.users.entity.Department;
import com.example.corenet.admin.users.entity.Position;
import com.example.corenet.common.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findByJobcode(int jobcode);
    
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
    
    Optional<User> findByUserName(String userName);
    Optional<User> findByPhone(String phone);

    Optional<User> findByRole(int role);

    Optional<User> findByPosition(Position position);
    Optional<User> findByDepartment(Department department);

}