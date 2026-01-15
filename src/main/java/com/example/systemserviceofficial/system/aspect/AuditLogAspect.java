package com.example.systemserviceofficial.system.aspect;

import com.example.commonserviceofficial.logging.util.TraceIdUtil;
import com.example.systemserviceofficial.system.annotation.AuditLogAction;
import com.example.systemserviceofficial.system.entity.AuditLog;
import com.example.systemserviceofficial.system.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {
    
    private final AuditLogService auditLogService;
    
    @Around("@annotation(auditLogAction)")
    public Object logAudit(ProceedingJoinPoint joinPoint, AuditLogAction auditLogAction)
            throws Throwable {
        
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = getCurrentRequest();
        
        AuditLog log = new AuditLog();
        log.setTraceId(TraceIdUtil.getOrCreate());
        log.setAction(auditLogAction.action());
        log.setResource(auditLogAction.resource());
        log.setMethod(request.getMethod());
        log.setRequestUrl(request.getRequestURL().toString());
        log.setIpAddress(getClientIp(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && 
            !"anonymousUser".equals(auth.getPrincipal())) {
            log.setUsername(auth.getName());
        }
        
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
    
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
    
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
