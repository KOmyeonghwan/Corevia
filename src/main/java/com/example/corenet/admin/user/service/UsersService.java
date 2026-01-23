package com.example.corenet.admin.user.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.corenet.admin.department.repo.DepartmentRepository;
import com.example.corenet.admin.log.serv.SecurityLogService;
import com.example.corenet.admin.user.dto.TodayUsersDTO;
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

    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,50}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        //  부서 변경
        if (departmentId != null) {
            if (user.getDepartment() == null ||
                    !user.getDepartment().getId().equals(departmentId)) {

                Department dept = departmentRepository.findById(departmentId)
                        .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다."));
                user.setDepartment(dept);
            }
        }

        //  직책 변경
        if (positionId != null) {

            Integer oldPositionId = user.getPosition() != null
                    ? user.getPosition().getId()
                    : null;

            Position newPosition = positionRepository.findById(positionId)
                    .orElseThrow(() -> new IllegalArgumentException("직책을 찾을 수 없습니다."));

            Integer deptId = user.getDepartment().getId();

            //  비부장 → 부장
            if (positionId == 2) {

                // 기존 부장 강등
                List<User> existingManagers = usersRepository.findByDepartment_IdAndPosition_Id(deptId, 2);

                Position employeePosition = positionRepository.findById(5) // 사원
                        .orElseThrow();

                for (User manager : existingManagers) {
                    if (!manager.getId().equals(user.getId())) {
                        manager.setPosition(employeePosition);

                        // 🔥 기존 부장 jobcode 재발급
                        manager.setJobcode(generateJobcode(deptId));
                        usersRepository.save(manager);
                    }
                }

                // 새 부장 jobcode = 001
                user.setJobcode(deptId * 1000 + 1);

                // 관리자 권한
                user.setRole(0);
            }

            // 부장 → 비부장
            if (oldPositionId != null && oldPositionId == 2 && positionId != 2) {
                user.setJobcode(generateJobcode(deptId));
            }

            user.setPosition(newPosition);

            // 🔐 보안 로그
            securityLogService.logEvent(
                    user,
                    SecurityLog.EventType.role_change,
                    "직책 변경 → " + newPosition.getPositionTitle()
                            + " (by " + adminUser.getUserName() + ")",
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent"),
                    request.getRequestURI());
        }

        usersRepository.save(user);
    }

    /**
      로그인한 사용자 기준으로 조회 가능한 사용자 목록 반환
     */
    public List<User> getUsersForViewer(LoginUserDTO loginUser) {

        Integer positionId = loginUser.getPosition_id();
        Integer departmentId = loginUser.getDepartment_id();

        if (positionId == null) {
            return List.of();
        }

        // CEO
        if (positionId == 1) {
            return usersRepository.findAllOrderByCeoFirst();
        }

        // 부장 / 과장
        if (positionId == 2 || positionId == 3) {
            if (departmentId != null) {
                return usersRepository.findByDepartmentOrderByManagerFirst(departmentId);
            }
            return List.of();
        }

        // 그 외 (대리/사원)
        return usersRepository.findById(loginUser.getUserPk())
                .map(List::of)
                .orElse(List.of());
    }

    /**
     부서별 부장 지정
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
            if (!manager.getId().equals(newManager.getId())) {

                // 직책 강등
                manager.setPosition(employeePosition);

                // 사번 재발급 (001 유지 금지)
                Integer newJobcode = generateJobcode(department.getId());
                manager.setJobcode(newJobcode);

                usersRepository.save(manager);
            }
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

        // 비밀번호 정책 검사
        validatePassword(newPassword);

        User user = usersRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이전 비밀번호 재사용 방지 ⭐
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("이전 비밀번호는 사용할 수 없습니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        //  강제 변경 상태 해제
        user.setPasswordResetRequired(false);

        //  보안 로그 기록
        securityLogService.logEvent(
                user,
                SecurityLog.EventType.password_change,
                "비밀번호 변경",
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                request.getRequestURI());

        //  세션 무효화 (강제 로그아웃)
        request.getSession().invalidate();
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

    // 오늘의 사원 3명 정보 랜덤으로 받아오기
    public List<TodayUsersDTO> getTodayUsers() {
        return usersRepository.findRandomTodayUsersDTO()
                .stream()
                .map(user -> new TodayUsersDTO(
                        user.getUserName(),
                        user.getCompanyEmail(),
                        user.getTodayDepartmentName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void resetPasswordByAdmin(Integer userId, User adminUser, HttpServletRequest request) {

        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        //  고정 비밀번호
        String resetPassword = "123456789";

        user.setPassword(passwordEncoder.encode(resetPassword));
        user.setPasswordResetRequired(true); // 비밀번호 변경 강제

        securityLogService.logEvent(
                user,
                SecurityLog.EventType.password_change,
                "관리자에 의해 비밀번호 초기화 (123456789) - by " + adminUser.getUserName(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                request.getRequestURI());
    }

    private void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "비밀번호는 6~50자이며 대문자와 특수문자를 포함해야 합니다.");
        }
    }

    @Transactional(readOnly = true)
    public LoginUserDTO getLoginUserById(Integer userPk) {
        User user = usersRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return LoginUserDTO.builder()
                .userPk(user.getId())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .companyEmail(user.getCompanyEmail())
                .jobcode(user.getJobcode())
                .role(user.getRole())
                // Position 객체에서 정보 가져오기
                .positionLevel(user.getPosition() != null ? user.getPosition().getLevel() : null)
                .position_id(user.getPosition() != null ? user.getPosition().getId() : null)
                .positionTitle(user.getPosition() != null ? user.getPosition().getPositionTitle() : null)
                // Department 객체에서 정보 가져오기
                .department_id(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getDepartmentName() : null)
                // 로그인 시간은 User 엔터티에 없으므로 createdAt이나 updatedAt 사용
                .loginDateTime(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                .build();
    }

}

