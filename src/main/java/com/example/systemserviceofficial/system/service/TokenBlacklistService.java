package com.example.systemserviceofficial.system.service;

import com.example.commonserviceofficial.security.JwtTokenProvider;
import com.example.systemserviceofficial.system.entity.TokenBlacklist;
import com.example.systemserviceofficial.system.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final CacheManager cacheManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    private static final String BLACKLIST_CACHE = "tokens";
    
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
        
        // Save to cache
        Cache cache = cacheManager.getCache(BLACKLIST_CACHE);
        if (cache != null) {
            cache.put("blacklist:" + token, true);
        }
        
        log.info("Token blacklisted: reason={}", reason);
    }
    
    public boolean isBlacklisted(String token) {
        // Check cache first
        Cache cache = cacheManager.getCache(BLACKLIST_CACHE);
        if (cache != null) {
            Boolean cached = cache.get("blacklist:" + token, Boolean.class);
            if (Boolean.TRUE.equals(cached)) {
                return true;
            }
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
