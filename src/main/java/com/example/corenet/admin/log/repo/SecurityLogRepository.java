package com.example.corenet.admin.log.repo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.corenet.entity.SecurityLog;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Integer> {
        List<SecurityLog> findByEventTypeAndUser_UserNameContainingIgnoreCaseAndCreatedAtBetween(
                        SecurityLog.EventType eventType, String userName, LocalDateTime from, LocalDateTime to);
        List<SecurityLog> findByCreatedAtBetween(LocalDateTime From, LocalDateTime To);
        Page<SecurityLog> findByEventTypeAndUser_UserNameContainingIgnoreCaseAndCreatedAtBetween(
                        SecurityLog.EventType eventType,
                        String userName,
                        LocalDateTime from,
                        LocalDateTime to,
                        Pageable pageable);
        Page<SecurityLog> findByCreatedAtBetween(
                        LocalDateTime from,
                        LocalDateTime to,
                        Pageable pageable);
        @EntityGraph(attributePaths = "user")
        Page<SecurityLog> findByEventTypeAndUser_UserNameContainingIgnoreCaseAndIpAddressContainingIgnoreCaseAndCreatedAtBetween(
                        SecurityLog.EventType eventType,
                        String userName,
                        String ipAddress,
                        LocalDateTime from,
                        LocalDateTime to,
                        Pageable pageable);
        @EntityGraph(attributePaths = "user")
        Page<SecurityLog> findByUser_UserNameContainingIgnoreCaseAndIpAddressContainingIgnoreCaseAndCreatedAtBetween(
                        String userName,
                        String ipAddress,
                        LocalDateTime from,
                        LocalDateTime to,
                        Pageable pageable);
        @EntityGraph(attributePaths = "user")
        Page<SecurityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}