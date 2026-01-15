package com.example.systemserviceofficial.system.dto.response;

import com.example.systemserviceofficial.system.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    
    private Long id;
    private String code;
    private String name;
    private String description;
    private UserStatus status;
    private List<PermissionResponse> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
