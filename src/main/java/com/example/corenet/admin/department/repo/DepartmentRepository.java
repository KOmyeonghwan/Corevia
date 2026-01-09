package com.example.corenet.admin.department.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.corenet.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Query("SELECT COUNT(d) FROM Department d WHERE d.id <> 1")
    long countExcludeException();

}