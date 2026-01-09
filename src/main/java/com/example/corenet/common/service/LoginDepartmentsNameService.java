package com.example.corenet.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.corenet.common.dto.DepartmentIdAndName;
import com.example.corenet.common.repository.LoginDepartmentNameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginDepartmentsNameService {
    private final LoginDepartmentNameRepository loginDepartmentRepository;

    public void printDepartmentInfo() {
        List<DepartmentIdAndName> departments = loginDepartmentRepository.findByIdNot(1);

        for (DepartmentIdAndName dept : departments) {
            System.out.println("ID: " + dept.getId() + ", Name: " + dept.getDepartmentName());
        }
    }

    public List<DepartmentIdAndName> getAllDepartments() {
        return loginDepartmentRepository.findByIdNot(1);
    }
    
}
