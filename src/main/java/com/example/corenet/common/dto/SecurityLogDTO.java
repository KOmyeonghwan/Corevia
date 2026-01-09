package com.example.corenet.common.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityLogDTO {

    private Integer id;
    private String userName; // username
    private String eventType;
    private String eventDescription;
    private String ipAddress;
    private String userAgent;
    private String pageUrl;
    private LocalDateTime createdAt;
}