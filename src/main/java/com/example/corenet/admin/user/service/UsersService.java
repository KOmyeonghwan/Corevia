package com.example.corenet.admin.user.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.corenet.admin.department.repo.DepartmentRepository;
import com.example.corenet.admin.log.serv.SecurityLogService;
import com.example.corenet.admin.user.repository.PositionRepository;
import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.entity.Department;
import com.example.corenet.entity.Position;
import com.example.corenet.entity.SecurityLog;
import com.example.corenet.entity.User;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityLogService securityLogService;

    @Transactional
    public User registerUser(User user) {
        // 비밀번호 암호화
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

    public User findByUserId(String userId) {
        return usersRepository.findByUserId(userId).orElse(null);
    }

    public List<User> findAllUsers() {
        return usersRepository.findAll();
    }

    @Transactional
    public void deleteUserById(Integer id) {
        usersRepository.deleteById(id);
    }

    public List<User> findByUserName(String name) {
        return usersRepository.findByUserNameContaining(name);
    }

    public List<User> findByDepartmentName(String deptName) {
        return usersRepository.findByDepartment_DepartmentNameContaining(deptName);
    }

    public Page<User> findByUserName(String name, Pageable pageable) {
        return usersRepository.findByUserNameContaining(name, pageable);
    }

    public Page<User> findByDepartmentName(String deptName, Pageable pageable) {
        return usersRepository.findByDepartment_DepartmentNameContaining(deptName, pageable);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }

    @Transactional
    public void updateUser(
            Integer userId,
            Integer departmentId,
            Integer positionId,
            Integer role,
            User adminUser,
            HttpServletRequest request) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        // 직책 변경
        if (positionId != null) {

            Position beforePosition = user.getPosition();

            // 대표 직책 체크
            if (positionId == 1) {
                boolean hasCEO = usersRepository.existsOtherCEO(1, userId);
                if (hasCEO) {
                    throw new IllegalArgumentException("이미 대표가 존재합니다. 대표로 변경할 수 없습니다.");
                }
            }

            Position newPosition = positionRepository.findById(positionId)
                    .orElseThrow(() -> new IllegalArgumentException("직책을 찾을 수 없습니다. id=" + positionId));

            // 실제 변경일 때만
            if (beforePosition == null || !beforePosition.getId().equals(positionId)) {
                user.setPosition(newPosition);

                securityLogService.logEvent(
                        user,
                        SecurityLog.EventType.role_change, // 🔥 이벤트 타입 재사용 or 새로 만듦
                        "직책 변경: "
                                + (beforePosition != null ? beforePosition.getPositionTitle() : "없음")
                                + " → "
                                + newPosition.getPositionTitle()
                                + " (by " + adminUser.getUserName() + ")",
                        request.getRemoteAddr(),
                        request.getHeader("User-Agent"),
                        request.getRequestURI());
            }
        }

        // 부서 변경
        if (departmentId != null) {
            // 기존 부서와 다른 경우만 처리
            if (user.getDepartment() == null || !user.getDepartment().getId().equals(departmentId)) {
                Department dept = departmentRepository.findById(departmentId)
                        .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다. id=" + departmentId));
                user.setDepartment(dept);

                // 부서 변경 시 새로운 사번 생성
                Integer newJobcode = generateJobcode(departmentId);
                user.setJobcode(newJobcode);
            }
        }

        usersRepository.save(user);
    }

    /**
     * 로그인한 사용자 기준으로 조회 가능한 사용자 목록 반환
     */
    public List<User> getUsersForViewer(LoginUserDTO loginUser) {

        Integer positionId = loginUser.getPosition_id();
        Integer departmentId = loginUser.getDepartment_id();

        if (positionId == null) {
            return List.of(); // 직책 정보 없으면 조회 불가
        }

        if (positionId == 1) { // CEO
            return usersRepository.findAll(); // 전체 조회
        } else if (positionId == 2 || positionId == 3) { // 부장 또는 과장
            if (departmentId != null) {
                // 부서 ID 기반 조회
                return usersRepository.findByDepartmentId(departmentId);
            } else {
                return List.of(); // 부서 없는 경우
            }
        } else { // 나머지 직책(대리, 사원 등)
            return usersRepository.findById(loginUser.getUserPk())
                    .map(List::of)
                    .orElse(List.of());
        }
    }

    /**
     * 부서별 부장 지정
     */
    @Transactional
    public void assignDepartmentManager(Integer userId, Integer departmentId) {
        User newManager = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다."));

        // 기존 부장이 있으면 사원으로 변경
        Position managerPosition = positionRepository.findById(2) // 부장
                .orElseThrow(() -> new IllegalArgumentException("직책 부장을 찾을 수 없습니다."));

        Position employeePosition = positionRepository.findById(5) // 사원
                .orElseThrow(() -> new IllegalArgumentException("직책 사원을 찾을 수 없습니다."));

        List<User> existingManagers = usersRepository.findByDepartmentAndPosition(department, managerPosition);

        for (User manager : existingManagers) {
            manager.setPosition(employeePosition);
            usersRepository.save(manager);
        }

        // 새 부장 지정
        newManager.setPosition(managerPosition);
        newManager.setDepartment(department);
        usersRepository.save(newManager);
    }

    public Position getPositionById(Integer positionId) {
        return positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("직책을 찾을 수 없습니다. id=" + positionId));
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return usersRepository.findById(id.intValue()); // usersRepository는 Integer PK로 되어 있음
    }

    @Transactional
    public void changePassword(Integer userPk, String newPassword, HttpServletRequest request) {

        User user = usersRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        securityLogService.logEvent(
                user,
                SecurityLog.EventType.password_change,
                "비밀번호 변경",
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                request.getRequestURI());
    }

    @Transactional
    public void changeEmail(Integer userPk, String newEmail) {

        // 이메일 중복 체크
        if (usersRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = usersRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setEmail(newEmail);
    }

    public long countToday() {
        return usersRepository.count();
    }

}
