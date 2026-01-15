package com.example.systemserviceofficial.system.repository;

import com.example.systemserviceofficial.system.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByAccessToken(String accessToken);
    
    List<RefreshToken> findByUserId(Long userId);
    
    List<RefreshToken> findByExpiresAtBeforeAndRevokedFalse(LocalDateTime dateTime);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    void deleteByUserId(Long userId);
}
