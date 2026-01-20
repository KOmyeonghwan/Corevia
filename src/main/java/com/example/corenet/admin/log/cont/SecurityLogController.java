package com.example.corenet.admin.log.cont;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.corenet.admin.log.serv.SecurityLogService;
import com.example.corenet.common.dto.SecurityLogDTO;
import com.example.corenet.entity.SecurityLog;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
public class SecurityLogController {

    private final SecurityLogService securityLogService;

    @GetMapping
    public Page<SecurityLogDTO> getLogs(
            @RequestParam(value = "eventType", required = false) SecurityLog.EventType eventType,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "ip", required = false) String ip, // 여기 추가
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return securityLogService.filterLogs(
                eventType,
                userName != null ? userName : "",
                fromDateTime,
                toDateTime,
                ip != null ? ip : "", // IP 전달
                pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLog(@PathVariable("id") Integer id) {
        try {
            securityLogService.deleteLogById(id);
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
    }
}