package com.example.systemserviceofficial.system.controller;

import com.example.commonserviceofficial.contract.BaseResponse;
import com.example.commonserviceofficial.util.WebUtils;
import com.example.systemserviceofficial.system.annotation.AuditLogAction;
import com.example.systemserviceofficial.system.dto.request.LoginRequest;
import com.example.systemserviceofficial.system.dto.request.RefreshTokenRequest;
import com.example.systemserviceofficial.system.dto.request.RegisterRequest;
import com.example.systemserviceofficial.system.dto.response.LoginResponse;
import com.example.systemserviceofficial.system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @AuditLogAction(action = "LOGIN", resource = "/api/auth/login")
    public Mono<BaseResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            ServerWebExchange exchange) {
        
        String ipAddress = WebUtils.getClientIp(exchange);
        LoginResponse response = authService.login(request, ipAddress);
        
        return Mono.just(BaseResponse.ok(response));
    }
    
    @PostMapping("/logout")
    @AuditLogAction(action = "LOGOUT", resource = "/api/auth/logout")
    public Mono<BaseResponse<String>> logout(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.substring(7); // Remove "Bearer "
        authService.logout(token);
        
        return Mono.just(BaseResponse.ok("Đăng xuất thành công"));
    }
    
    @PostMapping("/refresh")
    public Mono<BaseResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return Mono.just(BaseResponse.ok(response));
    }
    
    @PostMapping("/register")
    @AuditLogAction(action = "REGISTER", resource = "/api/auth/register")
    public Mono<BaseResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        authService.register(request);
        return Mono.just(BaseResponse.ok("Đăng ký thành công"));
    }
}
