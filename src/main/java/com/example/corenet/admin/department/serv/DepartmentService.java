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
        return departmentRepository.findById(id).orElse(null);
    }

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Integer id) {
        departmentRepository.deleteById(id);
    }

    public long countDepartments() {
        return departmentRepository.countExcludeException();
    }

    public List<Department> findAll(){
        return departmentRepository.findAll();
    }

}
