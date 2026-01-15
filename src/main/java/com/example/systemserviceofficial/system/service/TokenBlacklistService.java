package com.example.systemserviceofficial.system.service;

import com.example.commonserviceofficial.security.JwtTokenProvider;
import com.example.systemserviceofficial.system.entity.TokenBlacklist;
import com.example.systemserviceofficial.system.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    
    @Transactional
    public void blacklistToken(String token, String reason) {
        // Check if already blacklisted
        if (isBlacklisted(token)) {
            return;
        }
        
        // Get expiration date
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
            expirationDate.toInstant(),
            java.time.ZoneId.systemDefault()
        );
        
        // Save to database
        TokenBlacklist blacklist = new TokenBlacklist();
        blacklist.setToken(token);
        blacklist.setReason(reason);
        blacklist.setExpiresAt(expiresAt);
        tokenBlacklistRepository.save(blacklist);
        
        // Save to Redis cache
        long ttl = expirationDate.getTime() - System.currentTimeMillis();
        if (ttl > 0) {
            redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                true,
                ttl,
                TimeUnit.MILLISECONDS
            );
        }
        
        log.info("Token blacklisted: reason={}", reason);
    }
    
    public boolean isBlacklisted(String token) {
        // Check Redis first
        Boolean cached = (Boolean) redisTemplate.opsForValue().get(BLACKLIST_PREFIX + token);
        if (Boolean.TRUE.equals(cached)) {
            return true;
        }
        
        // Check database
        return tokenBlacklistRepository.existsByToken(token);
    }
    
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenBlacklistRepository.deleteByExpiresAtBefore(now);
        log.info("Cleaned up expired blacklisted tokens");
    }
}
