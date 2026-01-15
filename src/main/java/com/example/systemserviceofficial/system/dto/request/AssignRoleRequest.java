package com.example.systemserviceofficial.system.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignRoleRequest {
    
    @NotEmpty(message = "Danh sách role không được để trống")
    private List<Long> roleIds;
}
