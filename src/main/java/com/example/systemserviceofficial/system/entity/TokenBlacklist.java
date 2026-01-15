package com.example.systemserviceofficial.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_token_blacklist", indexes = {
    @Index(name = "idx_token", columnList = "token"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
public class TokenBlacklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 500)
    private String token;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(length = 100)
    private String reason;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
