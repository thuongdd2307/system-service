package com.example.systemserviceofficial.system.controller;

import com.example.commonserviceofficial.contract.BaseResponse;
import com.example.commonserviceofficial.security.annotation.HasRole;
import com.example.systemserviceofficial.system.dto.response.AuditLogResponse;
import com.example.systemserviceofficial.system.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {
    
    private final AuditLogService auditLogService;
    
    @GetMapping
    @HasRole(role = "ADMIN")
    public BaseResponse<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<AuditLogResponse> logs = auditLogService.findByFilters(
            userId, action, startDate, endDate, pageable
        );
        
        return BaseResponse.ok(logs);
    }
    
    @GetMapping("/{id}")
    @HasRole(role = "ADMIN")
    public BaseResponse<AuditLogResponse> getAuditLog(@PathVariable Long id) {
        AuditLogResponse log = auditLogService.findById(id);
        return BaseResponse.ok(log);
    }
    
    @GetMapping("/user/{userId}")
    @HasRole(role = "ADMIN")
    public BaseResponse<Page<AuditLogResponse>> getUserAuditLogs(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<AuditLogResponse> logs = auditLogService.findByUserId(userId, pageable);
        return BaseResponse.ok(logs);
    }
    
    @GetMapping("/user/{userId}/recent")
    public BaseResponse<List<AuditLogResponse>> getRecentUserAuditLogs(
            @PathVariable Long userId) {
        
        List<AuditLogResponse> logs = auditLogService.findRecentByUserId(userId);
        return BaseResponse.ok(logs);
    }
}
