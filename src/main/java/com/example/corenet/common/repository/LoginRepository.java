package com.example.corenet.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.corenet.entity.Department;
import com.example.corenet.entity.Position;
import com.example.corenet.entity.User;

@Repository
public interface LoginRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);

    // UsersRepository.java
    @Query("SELECT MAX(u.jobcode) FROM User u WHERE u.department.id = :departmentId")
    Integer findMaxJobcodeByDepartment(@Param("departmentId") Integer departmentId);

    // 이름 검색
    List<User> findByUserNameContaining(String name);

    // 부서 이름 검색 (Department 엔티티와 연관)
    List<User> findByDepartment_DepartmentNameContaining(String deptName);

    // 기존 findByUserNameContaining -> 페이징 추가
    Page<User> findByUserNameContaining(String name, Pageable pageable);

    // 기존 findByDepartment_DepartmentNameContaining -> 페이징 추가
    Page<User> findByDepartment_DepartmentNameContaining(String deptName, Pageable pageable);

    // 전체 조회 페이징 추가
    Page<User> findAll(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.position.id = :positionId AND u.id <> :userId")
    boolean existsOtherCEO(@Param("positionId") Integer positionId, @Param("userId") Integer userId);

    List<User> findByDepartment(Department department);

    List<User> findByDepartmentAndPosition(Department department, Position position);

    // 부서장 조회 (position_id = 2, 부장)
    User findFirstByDepartmentIdAndPositionId(Integer departmentId, Integer positionId);

    // 인원수 조회
    int countByDepartmentId(Integer departmentId);

    @Query("SELECT u FROM User u JOIN FETCH u.position WHERE u.userId = :userId")
    User findByUserIdWithPosition(@Param("userId") String userId);

}
