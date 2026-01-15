package com.example.systemserviceofficial.system.repository;

import com.example.systemserviceofficial.system.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    
    Optional<TokenBlacklist> findByToken(String token);
    
    boolean existsByToken(String token);
    
    List<TokenBlacklist> findByExpiresAtBefore(LocalDateTime dateTime);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
