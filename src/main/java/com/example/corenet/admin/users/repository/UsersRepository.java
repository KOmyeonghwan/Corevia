package com.example.corenet.admin.users.repository;

import com.example.corenet.admin.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);

    Optional<Users> findByUserId(String userId);

    // UsersRepository.java
    @Query("SELECT MAX(u.jobcode) FROM Users u WHERE u.department.id = :departmentId")
    Integer findMaxJobcodeByDepartment(@Param("departmentId") Integer departmentId);


        // 이름 검색
    List<Users> findByUserNameContaining(String name);

    // 부서 이름 검색 (Department 엔티티와 연관)
    List<Users> findByDepartment_DepartmentNameContaining(String deptName);

    

}