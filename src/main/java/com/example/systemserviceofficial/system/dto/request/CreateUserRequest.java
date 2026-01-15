package com.example.systemserviceofficial.system.dto.request;

import com.example.systemserviceofficial.system.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateUserRequest {
    
    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3-50 ký tự")
    private String username;
    
    @NotBlank(message = "Password không được để trống")
    @Size(min = 8, message = "Password phải có ít nhất 8 ký tự")
    private String password;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    
    private String phone;
    
    private String avatarUrl;
    
    private UserStatus status;
    
    private List<Long> roleIds;
}
