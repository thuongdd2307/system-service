package com.example.systemserviceofficial.system.aspect;

import com.example.commonserviceofficial.logging.util.TraceIdUtil;
import com.example.systemserviceofficial.system.annotation.AuditLogAction;
import com.example.systemserviceofficial.system.entity.AuditLog;
import com.example.systemserviceofficial.system.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
//@Aspect  // Tắt tạm thời
@Component
@RequiredArgsConstructor
public class AuditLogAspect {
    
    private final AuditLogService auditLogService;
    
    @Around("@annotation(auditLogAction)")
    public Object logAudit(ProceedingJoinPoint joinPoint, AuditLogAction auditLogAction)
            throws Throwable {
        
        long startTime = System.currentTimeMillis();
        
        AuditLog log = new AuditLog();
        log.setTraceId(TraceIdUtil.getOrCreate());
        log.setAction(auditLogAction.action());
        log.setResource(auditLogAction.resource());
        log.setMethod("POST"); // Default for now
        
        // Get current user from reactive context
        ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName)
            .subscribe(username -> log.setUsername(username));
        
        try {
            Object result = joinPoint.proceed();
            
            log.setStatus("SUCCESS");
            log.setResponseStatus(200);
            log.setExecutionTime((int)(System.currentTimeMillis() - startTime));
            
            auditLogService.saveAsync(log);
            
            return result;
            
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
            log.setResponseStatus(500);
            log.setExecutionTime((int)(System.currentTimeMillis() - startTime));
            
            auditLogService.saveAsync(log);
            
            throw e;
        }
    }
}
