package com.example.corenet.common.dto;

import java.time.LocalDateTime;

import com.example.corenet.entity.Schedule;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Boolean isAdminView;

    // 엔터티 → DTO
    public static ScheduleDto fromEntity(Schedule entity) {
        ScheduleDto dto = new ScheduleDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDatetime(entity.getStartDatetime());
        dto.setEndDatetime(entity.getEndDatetime());
        dto.setIsAdminView(entity.getIsAdminView());
        return dto;
    }

    // DTO → 엔터티
    public Schedule toEntity() {
        Schedule entity = new Schedule();
        entity.setUserId(this.userId);
        entity.setTitle(this.title);
        entity.setDescription(this.description);
        entity.setStartDatetime(this.startDatetime);
        entity.setEndDatetime(this.endDatetime);
        entity.setIsAdminView(this.isAdminView != null ? this.isAdminView : false);
        return entity;
    }
}