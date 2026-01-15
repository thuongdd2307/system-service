package com.example.systemserviceofficial.system.dto.request;

import com.example.systemserviceofficial.system.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserRequest {
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String fullName;
    
    private String phone;
    
    private String avatarUrl;
    
    private UserStatus status;
}
