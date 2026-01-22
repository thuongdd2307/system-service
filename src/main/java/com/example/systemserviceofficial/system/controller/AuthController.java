package com.example.systemserviceofficial.system.controller;

import com.example.commonserviceofficial.contract.BaseResponse;
import com.example.commonserviceofficial.util.WebUtils;
import com.example.systemserviceofficial.system.annotation.AuditLogAction;
import com.example.systemserviceofficial.system.dto.request.*;
import com.example.systemserviceofficial.system.dto.response.LoginResponse;
import com.example.systemserviceofficial.system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        LoginResponse response = authService.login(request, ipAddress, userAgent);
        
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
        return Mono.just(BaseResponse.ok("Đăng ký thành công. Vui lòng kiểm tra email để kích hoạt tài khoản."));
    }
    
    /**
     * Forgot password - Gửi email reset password
     */
    @PostMapping("/forgot-password")
    @AuditLogAction(action = "FORGOT_PASSWORD", resource = "/api/auth/forgot-password")
    public Mono<BaseResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        
        authService.forgotPassword(request.getEmail());
        return Mono.just(BaseResponse.ok("Email hướng dẫn đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư."));
    }
    
    /**
     * Reset password với token
     */
    @PostMapping("/reset-password")
    @AuditLogAction(action = "RESET_PASSWORD", resource = "/api/auth/reset-password")
    public Mono<BaseResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        
        // Validate confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Mono.just(BaseResponse.error("PASSWORDS_NOT_MATCH", "Mật khẩu xác nhận không khớp"));
        }
        
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return Mono.just(BaseResponse.ok("Đặt lại mật khẩu thành công. Vui lòng đăng nhập với mật khẩu mới."));
    }
    
    /**
     * Change password (khi user đã đăng nhập)
     */
    @PostMapping("/change-password")
    @AuditLogAction(action = "CHANGE_PASSWORD", resource = "/api/auth/change-password")
    public Mono<BaseResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        
        // Validate confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Mono.just(BaseResponse.error("PASSWORDS_NOT_MATCH", "Mật khẩu xác nhận không khớp"));
        }
        
        String username = authentication.getName();
        authService.changePassword(username, request.getOldPassword(), request.getNewPassword());
        return Mono.just(BaseResponse.ok("Thay đổi mật khẩu thành công."));
    }
}
