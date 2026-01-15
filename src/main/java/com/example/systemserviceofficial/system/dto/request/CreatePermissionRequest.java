package com.example.systemserviceofficial.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePermissionRequest {
    
    @NotBlank(message = "Code không được để trống")
    @Size(max = 50, message = "Code không được quá 50 ký tự")
    private String code;
    
    @NotBlank(message = "Name không được để trống")
    @Size(max = 100, message = "Name không được quá 100 ký tự")
    private String name;
    
    private String resource;
    
    private String action;
    
    private String description;
}
