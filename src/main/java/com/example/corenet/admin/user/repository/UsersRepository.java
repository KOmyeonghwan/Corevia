package com.example.corenet.admin.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.corenet.admin.user.dto.TodayUsersDTO;
import com.example.corenet.entity.Department;
import com.example.corenet.entity.Position;
import com.example.corenet.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
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

    // [추가] Department ID 기반 조회
    List<User> findByDepartmentId(Integer departmentId);

    // [추가] ID 기반 조회 (서비스에서 자기 자신 조회용)
    Optional<User> findById(Integer id);

    List<User> findByDepartmentAndPosition(Department department, Position position);

    // 부서장 조회 (position_id = 2, 부장)
    User findFirstByDepartmentIdAndPositionId(Integer departmentId, Integer positionId);

    // 인원수 조회
    int countByDepartmentId(Integer departmentId);

    @Query("SELECT u FROM User u JOIN FETCH u.position WHERE u.userId = :userId")
    User findByUserIdWithPosition(@Param("userId") String userId);

    // 특정 채팅방 참여자 조회
    @Query("SELECT u FROM User u JOIN ChatRoomParticipant p ON u.id = p.userId WHERE p.roomId = :roomId")
    List<User> findByChatRoomId(@Param("roomId") Long roomId);

    // 특정 채팅방 참여자 수 조회
    @Query("SELECT COUNT(u) FROM User u JOIN ChatRoomParticipant p ON u.id = p.userId WHERE p.roomId = :roomId")
    int countByChatRoomId(@Param("roomId") Long roomId);

    Optional<User> findByCompanyEmail(String companyEmail);

    @Query("""
                SELECT u FROM User u
                JOIN FETCH u.position
                JOIN FETCH u.department
                WHERE u.userId = :userId
            """)
    Optional<User> findLoginUser(@Param("userId") String userId);

    // 오늘의 사원 3명
    @Query(value = "SELECT u.user_name AS userName, u.company_email AS email, d.department_name AS departmentName " +
            "FROM users u " +
            "LEFT JOIN departments d ON u.department_id = d.id " +
            "WHERE u.user_name NOT IN ('대표이사','시스템관리자','외부시스템관리자') " +
            "ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<TodayUsersDTO> findRandomTodayUsersDTO();

}