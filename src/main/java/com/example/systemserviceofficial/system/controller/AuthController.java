package com.example.systemserviceofficial.system.controller;

import com.example.commonserviceofficial.contract.BaseResponse;
import com.example.systemserviceofficial.system.annotation.AuditLogAction;
import com.example.systemserviceofficial.system.dto.request.LoginRequest;
import com.example.systemserviceofficial.system.dto.request.RefreshTokenRequest;
import com.example.systemserviceofficial.system.dto.request.RegisterRequest;
import com.example.systemserviceofficial.system.dto.response.LoginResponse;
import com.example.systemserviceofficial.system.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @AuditLogAction(action = "LOGIN", resource = "/api/auth/login")
    public BaseResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIp(httpRequest);
        LoginResponse response = authService.login(request, ipAddress);
        
        return BaseResponse.ok(response);
    }
    
    @PostMapping("/logout")
    @AuditLogAction(action = "LOGOUT", resource = "/api/auth/logout")
    public BaseResponse<String> logout(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7); // Remove "Bearer "
        authService.logout(token);
        
        return BaseResponse.ok("Đăng xuất thành công");
    }
    
    @PostMapping("/refresh")
    public BaseResponse<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return BaseResponse.ok(response);
    }
    
    @PostMapping("/register")
    @AuditLogAction(action = "REGISTER", resource = "/api/auth/register")
    public BaseResponse<String> register(
            @Valid @RequestBody RegisterRequest request) {
        
        authService.register(request);
        return BaseResponse.ok("Đăng ký thành công");
    }
    
    private String getClientIp(HttpServletRequest request) {
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
