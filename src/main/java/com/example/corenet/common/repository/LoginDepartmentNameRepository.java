package com.example.corenet.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.corenet.common.dto.DepartmentIdAndName;
import com.example.corenet.entity.Department;

@Repository
public interface LoginDepartmentNameRepository extends JpaRepository<Department, Integer> {
    List<DepartmentIdAndName> findByIdNot(Integer id);
}
