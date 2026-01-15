package com.example.systemserviceofficial.system.service;

import com.example.commonserviceofficial.exception.BusinessException;
import com.example.commonserviceofficial.security.JwtClaims;
import com.example.commonserviceofficial.security.JwtTokenProvider;
import com.example.systemserviceofficial.system.dto.request.LoginRequest;
import com.example.systemserviceofficial.system.dto.request.RegisterRequest;
import com.example.systemserviceofficial.system.dto.response.LoginResponse;
import com.example.systemserviceofficial.system.entity.User;
import com.example.systemserviceofficial.system.enums.UserStatus;
import com.example.systemserviceofficial.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;
    
    @Value("${app.security.max-failed-login-attempts:5}")
    private int maxFailedAttempts;
    
    @Value("${app.security.account-lock-duration-minutes:30}")
    private int lockDurationMinutes;
    
    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        // Find user
        User user = userRepository.findByUsernameWithRoles(request.getUsername())
            .orElseThrow(() -> new BusinessException(
                "INVALID_CREDENTIALS",
                "Tên đăng nhập hoặc mật khẩu không đúng"
            ));
        
        // Check if account is locked
        if (user.getLockedUntil() != null && 
            user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException(
                "ACCOUNT_LOCKED",
                "Tài khoản đã bị khóa. Vui lòng thử lại sau."
            );
        }
        
        // Check account status
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(
                "ACCOUNT_INACTIVE",
                "Tài khoản không hoạt động"
            );
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new BusinessException(
                "INVALID_CREDENTIALS",
                "Tên đăng nhập hoặc mật khẩu không đúng"
            );
        }
        
        // Reset failed attempts
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        userRepository.save(user);
        
        // Generate tokens
        List<String> roleCodes = user.getRoles().stream()
            .map(role -> role.getCode())
            .collect(Collectors.toList());
        
        String accessToken = jwtTokenProvider.generateToken(
            user.getUsername(),
            roleCodes
        );
        
        String refreshToken = jwtTokenProvider.generateRefreshToken(
            user.getUsername()
        );
        
        // Save refresh token
        refreshTokenService.saveRefreshToken(
            refreshToken,
            user.getId(),
            accessToken
        );
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600)
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(roleCodes)
            .build();
    }
    
    @Transactional
    public void logout(String accessToken) {
        // Add token to blacklist
        tokenBlacklistService.blacklistToken(accessToken, "LOGOUT");
        
        // Revoke refresh token
        refreshTokenService.revokeByAccessToken(accessToken);
        
        log.info("User logged out successfully");
    }
    
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(
                "INVALID_REFRESH_TOKEN",
                "Refresh token không hợp lệ"
            );
        }
        
        // Check if revoked
        if (!refreshTokenService.isValid(refreshToken)) {
            throw new BusinessException(
                "REFRESH_TOKEN_REVOKED",
                "Refresh token đã bị thu hồi"
            );
        }
        
        // Parse token
        JwtClaims claims = jwtTokenProvider.parseToken(refreshToken);
        String username = claims.getUsername();
        
        // Get user
        User user = userRepository.findByUsernameWithRoles(username)
            .orElseThrow(() -> new BusinessException(
                "USER_NOT_FOUND",
                "Người dùng không tồn tại"
            ));
        
        // Generate new tokens
        List<String> roleCodes = user.getRoles().stream()
            .map(role -> role.getCode())
            .collect(Collectors.toList());
        
        String newAccessToken = jwtTokenProvider.generateToken(
            user.getUsername(),
            roleCodes
        );
        
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(
            user.getUsername()
        );
        
        // Save new refresh token
        refreshTokenService.saveRefreshToken(
            newRefreshToken,
            user.getId(),
            newAccessToken
        );
        
        log.info("Token refreshed for user: {}", username);
        
        return LoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(3600)
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .roles(roleCodes)
            .build();
    }
    
    @Transactional
    public void register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(
                "USERNAME_EXISTS",
                "Tên đăng nhập đã tồn tại"
            );
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                "EMAIL_EXISTS",
                "Email đã được sử dụng"
            );
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        
        userRepository.save(user);
        
        log.info("New user registered: {}", user.getUsername());
    }
    
    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        
        if (attempts >= maxFailedAttempts) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
            log.warn("Account locked due to too many failed attempts: {}", 
                user.getUsername());
        }
        
        userRepository.save(user);
    }
}
