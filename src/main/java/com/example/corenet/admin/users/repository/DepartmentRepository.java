package com.example.corenet.admin.users.repository;

import com.example.corenet.admin.users.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    
}