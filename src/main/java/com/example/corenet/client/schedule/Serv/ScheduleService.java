package com.example.corenet.client.schedule.Serv;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.corenet.client.schedule.Repo.ScheduleRepository;
import com.example.corenet.common.dto.ScheduleDto;
import com.example.corenet.entity.Schedule;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // 일정 가져오기 (유저 + 기간)
    public List<ScheduleDto> getSchedules(Long userId, String start, String end) {
        LocalDateTime startDate = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end).atTime(23, 59, 59);
        return scheduleRepository.findByUserIdAndStartDatetimeBetween(userId, startDate, endDate)
                .stream().map(ScheduleDto::fromEntity).collect(Collectors.toList());
    }

    // 일정 추가
    public ScheduleDto addSchedule(ScheduleDto dto) {
        Schedule entity = dto.toEntity();
        Schedule saved = scheduleRepository.save(entity);
        return ScheduleDto.fromEntity(saved);
    }

    // 일정 수정
    public ScheduleDto updateSchedule(Long id, ScheduleDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));
        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setStartDatetime(dto.getStartDatetime());
        schedule.setEndDatetime(dto.getEndDatetime());
        schedule.setIsAdminView(dto.getIsAdminView());
        return ScheduleDto.fromEntity(scheduleRepository.save(schedule));
    }

    // 일정 삭제
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    // public List<ScheduleDto> getDepartmentSchedules(Long departmentId) {
    // List<Schedule> schedules =
    // scheduleRepository.findByIsAdminViewTrueAndUserDepartmentId(departmentId);
    // return
    // schedules.stream().map(ScheduleDto::fromEntity).collect(Collectors.toList());
    // }
}