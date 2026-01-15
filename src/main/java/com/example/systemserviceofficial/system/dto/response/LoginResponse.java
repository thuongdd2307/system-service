package com.example.systemserviceofficial.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
}
