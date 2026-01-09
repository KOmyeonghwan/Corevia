package com.example.corenet.client.schedule.Repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.example.corenet.entity.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserIdAndStartDatetimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // @Query("SELECT s FROM Schedule s JOIN User u ON s.userId = u.id " +
    // "WHERE s.isAdminView = true AND u.department.id = :deptId " + "ORDER BY s.startDatetime ASC")
    // List<Schedule> findByIsAdminViewTrueAndUserDepartmentId(@Param("deptId") Long
    // deptId);
}