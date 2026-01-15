package com.example.systemserviceofficial.system.service;

import com.example.systemserviceofficial.system.dto.response.AuditLogResponse;
import com.example.systemserviceofficial.system.entity.AuditLog;
import com.example.systemserviceofficial.system.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    @Async
    @Transactional
    public void saveAsync(AuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
            log.debug("Audit log saved: action={}, user={}", 
                auditLog.getAction(), auditLog.getUsername());
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }
    
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findByFilters(
            Long userId,
            String action,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        
        return auditLogRepository.findByFilters(userId, action, startDate, endDate, pageable)
            .map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> findByUserId(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable)
            .map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public List<AuditLogResponse> findRecentByUserId(Long userId) {
        return auditLogRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public AuditLogResponse findById(Long id) {
        return auditLogRepository.findById(id)
            .map(this::toResponse)
            .orElse(null);
    }
    
    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
            .id(log.getId())
            .traceId(log.getTraceId())
            .userId(log.getUserId())
            .username(log.getUsername())
            .action(log.getAction())
            .resource(log.getResource())
            .method(log.getMethod())
            .requestUrl(log.getRequestUrl())
            .responseStatus(log.getResponseStatus())
            .ipAddress(log.getIpAddress())
            .executionTime(log.getExecutionTime())
            .status(log.getStatus())
            .createdAt(log.getCreatedAt())
            .build();
    }
}
