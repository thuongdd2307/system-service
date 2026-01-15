package com.example.systemserviceofficial.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    
    private Long id;
    private String traceId;
    private Long userId;
    private String username;
    private String action;
    private String resource;
    private String method;
    private String requestUrl;
    private Integer responseStatus;
    private String ipAddress;
    private Integer executionTime;
    private String status;
    private LocalDateTime createdAt;
}
