package com.example.corenet.admin.department.serv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.corenet.admin.department.repo.DepartmentRepository;
import com.example.corenet.entity.Department;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("부서를 찾을 수 없습니다."));
    }

    /*
     * 부서 추가
     */
    public Department createDepartment(Integer deptCode, String departmentName) {

        if (deptCode == null) {
            throw new IllegalArgumentException("부서 코드는 필수입니다.");
        }

        if (departmentRepository.existsByDeptCode(deptCode)) {
            throw new IllegalArgumentException("이미 존재하는 부서 코드입니다.");
        }

        if (departmentRepository.existsByDepartmentName(departmentName)) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
        }

        Department dept = Department.builder()
                .deptCode(deptCode)
                .departmentName(departmentName)
                .build();

        return departmentRepository.save(dept);
    }

    /*
     * 부서 수정
     */
    public Department updateDepartment(Integer id, Integer deptCode, String departmentName) {

        Department dept = getDepartmentById(id);

        if (!dept.getDeptCode().equals(deptCode)
                && departmentRepository.existsByDeptCode(deptCode)) {
            throw new IllegalArgumentException("이미 존재하는 부서 코드입니다.");
        }

        if (!dept.getDepartmentName().equals(departmentName)
                && departmentRepository.existsByDepartmentName(departmentName)) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
        }

        dept.setDeptCode(deptCode);
        dept.setDepartmentName(departmentName);

        return departmentRepository.save(dept);
    }

    /*
     * 부서 삭제
     */
    public void deleteDepartment(Integer id) {
        departmentRepository.delete(getDepartmentById(id));
    }

    public long countDepartments() {
        return departmentRepository.countExcludeException();
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }
}

