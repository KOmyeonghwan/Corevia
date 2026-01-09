package com.example.corenet.admin.log.serv;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.corenet.admin.log.repo.SecurityLogRepository;
import com.example.corenet.common.dto.SecurityLogDTO;
import com.example.corenet.entity.SecurityLog;
import com.example.corenet.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityLogService {

    private final SecurityLogRepository securityLogRepository;

    public List<SecurityLogDTO> getAllLogs() {
        return securityLogRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<SecurityLogDTO> filterLogs(
            SecurityLog.EventType eventType,
            String userName,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {
        Page<SecurityLog> logs;

        if (eventType != null && from != null && to != null) {
            logs = securityLogRepository
                    .findByEventTypeAndUser_UserNameContainingIgnoreCaseAndCreatedAtBetween(
                            eventType, userName, from, to, pageable);
        } else if (from != null && to != null) {
            logs = securityLogRepository.findByCreatedAtBetween(from, to, pageable);
        } else {
            logs = securityLogRepository.findAll(pageable);
        }

        return logs.map(this::toDTO);
    }

    // public void logEvent(User user,
    // SecurityLog.EventType type,
    // String description,
    // String ipAdress,
    // String userAgent,
    // String pageUrl) {
    // SecurityLog log = SecurityLog.builder()
    // .user(user)
    // .eventType(type)
    // .eventDescription(description)
    // .ipAddress(ipAdress)
    // .userAgent(userAgent)
    // .pageUrl(pageUrl)
    // .createdAt(LocalDateTime.now())
    // .build();
    // securityLogRepository.save(log);
    // }

    public void logEvent(User user,
            SecurityLog.EventType type,
            String description,
            String ipAdress,
            String userAgent,
            String pageUrl) {
        // 로그인 성공 이벤트는 저장하지 않음
        if (type == SecurityLog.EventType.login_success) {
            return;
        }

        SecurityLog log = SecurityLog.builder()
                .user(user)
                .eventType(type)
                .eventDescription(description)
                .ipAddress(ipAdress)
                .userAgent(userAgent)
                .pageUrl(pageUrl)
                .createdAt(LocalDateTime.now())
                .build();
        securityLogRepository.save(log);
    }

    private SecurityLogDTO toDTO(SecurityLog log) {
        return SecurityLogDTO.builder()
                .id(log.getId())
                .userName(log.getUser() != null ? log.getUser().getUserName() : "익명")
                .eventType(log.getEventType().name())
                .eventDescription(log.getEventDescription())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .pageUrl(log.getPageUrl())
                .createdAt(log.getCreatedAt())
                .build();
    }

    public Page<SecurityLogDTO> filterLogs(
            SecurityLog.EventType eventType,
            String userName,
            LocalDateTime from,
            LocalDateTime to,
            String ipAddress,
            Pageable pageable) {

        userName = userName != null ? userName : "";
        ipAddress = ipAddress != null ? ipAddress : "";
        LocalDateTime fromTime = from != null ? from : LocalDateTime.MIN;
        LocalDateTime toTime = to != null ? to : LocalDateTime.now();

        Page<SecurityLog> logs;

        if (eventType != null) {
            logs = securityLogRepository
                    .findByEventTypeAndUser_UserNameContainingIgnoreCaseAndIpAddressContainingIgnoreCaseAndCreatedAtBetween(
                            eventType, userName, ipAddress, fromTime, toTime, pageable);
        } else {
            logs = securityLogRepository
                    .findByUser_UserNameContainingIgnoreCaseAndIpAddressContainingIgnoreCaseAndCreatedAtBetween(
                            userName, ipAddress, fromTime, toTime, pageable);
        }

        return logs.map(this::toDTO);
    }

    public void deleteLogById(Integer id) {
        securityLogRepository.deleteById(id);
    }

}
