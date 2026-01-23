package com.example.corenet.admin.department.cont;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.corenet.admin.department.serv.DepartmentService;
import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.entity.Department;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UsersRepository usersRepository;

    /*
      부서 추가
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addDepartment(@RequestBody Map<String, Object> req) {

        Number deptCodeNum = (Number) req.get("deptCode"); // Number로 받기
        Integer deptCode = deptCodeNum.intValue(); // int로 변환
        String departmentName = (String) req.get("departmentName");

        Department saved = departmentService.createDepartment(deptCode, departmentName);
        return ResponseEntity.ok(saved);
    }

    /*
     부서 수정
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateDepartment(@RequestBody Map<String, Object> req) {

        Number idNum = (Number) req.get("id");
        Integer id = idNum.intValue();

        Number deptCodeNum = (Number) req.get("deptCode");
        Integer deptCode = deptCodeNum.intValue();

        String departmentName = (String) req.get("departmentName");

        Department updated = departmentService.updateDepartment(id, deptCode, departmentName);
        return ResponseEntity.ok(updated);
    }

    /*
     부서 삭제
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteDepartment(@RequestBody Map<String, Integer> req) {

        Integer deptId = req.get("id");
        if (deptId == null) {
            return ResponseEntity.badRequest().body("부서 ID가 없습니다.");
        }

        int memberCount = usersRepository.countByDepartmentId(deptId);
        if (memberCount > 0) {
            return ResponseEntity.badRequest()
                    .body("해당 부서에 직원이 있어 삭제할 수 없습니다.");
        }

        departmentService.deleteDepartment(deptId);
        return ResponseEntity.ok("부서가 삭제되었습니다.");
    }
}

