package com.example.systemserviceofficial.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateRoleRequest {
    
    @NotBlank(message = "Code không được để trống")
    @Size(max = 50, message = "Code không được quá 50 ký tự")
    private String code;
    
    @NotBlank(message = "Name không được để trống")
    @Size(max = 100, message = "Name không được quá 100 ký tự")
    private String name;
    
    private String description;
    
    private List<Long> permissionIds;
}
