package com.example.systemserviceofficial.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_audit_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_trace_id", columnList = "trace_id")
})
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "trace_id", length = 100)
    private String traceId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(length = 50)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String action;
    
    @Column(length = 100)
    private String resource;
    
    @Column(length = 10)
    private String method;
    
    @Column(name = "request_url", columnDefinition = "TEXT")
    private String requestUrl;
    
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;
    
    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;
    
    @Column(name = "response_status")
    private Integer responseStatus;
    
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "execution_time")
    private Integer executionTime;
    
    @Column(length = 20)
    private String status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
