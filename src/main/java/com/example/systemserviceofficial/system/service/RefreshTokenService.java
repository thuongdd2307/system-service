package com.example.systemserviceofficial.system.service;

import com.example.commonserviceofficial.security.JwtTokenProvider;
import com.example.systemserviceofficial.system.entity.RefreshToken;
import com.example.systemserviceofficial.system.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Value("${security.jwt.refresh-expiration-seconds:604800}")
    private long refreshExpirationSeconds;
    
    @Transactional
    public void saveRefreshToken(String token, Long userId, String accessToken) {
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
            expirationDate.toInstant(),
            java.time.ZoneId.systemDefault()
        );
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(userId);
        refreshToken.setAccessToken(accessToken);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setRevoked(false);
        
        refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token saved for user: {}", userId);
    }
    
    @Transactional
    public void revokeByAccessToken(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken).ifPresent(token -> {
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
            log.debug("Refresh token revoked for access token");
        });
    }
    
    @Transactional
    public void revokeAllByUserId(Long userId) {
        refreshTokenRepository.findByUserId(userId).forEach(token -> {
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
        });
        log.info("All refresh tokens revoked for user: {}", userId);
    }
    
    public boolean isValid(String token) {
        return refreshTokenRepository.findByToken(token)
            .map(rt -> !rt.getRevoked() && rt.getExpiresAt().isAfter(LocalDateTime.now()))
            .orElse(false);
    }
    
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteByExpiresAtBefore(now);
        log.info("Cleaned up expired refresh tokens");
    }
}
