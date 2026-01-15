package com.example.systemserviceofficial.system.scheduled;


import com.example.systemserviceofficial.system.service.RefreshTokenService;
import com.example.systemserviceofficial.system.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.token", name = "cleanup-enabled", havingValue = "true", matchIfMissing = true)
public class TokenCleanupJob {
    
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    
    @Scheduled(cron = "${app.token.cleanup-cron:0 0 2 * * ?}")
    public void cleanupExpiredTokens() {
        log.info("Starting token cleanup job...");
        
        try {
            refreshTokenService.cleanupExpiredTokens();
            tokenBlacklistService.cleanupExpiredTokens();
            
            log.info("Token cleanup job completed successfully");
        } catch (Exception e) {
            log.error("Error during token cleanup", e);
        }
    }
}
