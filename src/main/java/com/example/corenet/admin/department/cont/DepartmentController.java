package com.example.corenet.admin.department.cont;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.corenet.admin.department.serv.DepartmentService;
import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.entity.Department;

@Controller
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UsersRepository usersRepository;

    /**
     * 부서 추가
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Department> addDepartment(@RequestBody Department department) {

        // department.id가 null인 경우 — 자동 생성됨 (IDENTITY)
        Department newDept = Department.builder()
                .departmentName(department.getDepartmentName())
                .build();

        Department savedDept = departmentService.saveDepartment(newDept);
        return ResponseEntity.ok(savedDept);        
    }

    /**
     * 부서 삭제
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteDepartment(@RequestBody Map<String, Integer> req) {
        Integer deptId = req.get("id");
        if (deptId == null) {
            return ResponseEntity.badRequest().body("부서 ID가 없습니다.");
        }

        // 부서에 속한 직원 수 확인
        int memberCount = usersRepository.countByDepartmentId(deptId);
        if (memberCount > 0) {
            return ResponseEntity.badRequest().body("해당 부서에 직원이 있어 삭제할 수 없습니다.");
        }

        departmentService.deleteDepartment(deptId);
        return ResponseEntity.ok("부서가 삭제되었습니다.");
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Department> updateDepartment(@RequestBody Department dept) {
        Department existing = departmentService.getDepartmentById(dept.getId());
        if (existing != null) {
            existing.setDepartmentName(dept.getDepartmentName());
            Department saved = departmentService.saveDepartment(existing);
            return ResponseEntity.ok(saved);
        }
        return ResponseEntity.badRequest().build();
    }
}
