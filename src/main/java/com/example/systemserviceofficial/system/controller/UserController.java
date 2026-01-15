package com.example.systemserviceofficial.system.controller;

import com.example.commonserviceofficial.contract.BaseResponse;
import com.example.commonserviceofficial.security.annotation.HasRole;
import com.example.systemserviceofficial.system.annotation.AuditLogAction;
import com.example.systemserviceofficial.system.dto.request.AssignRoleRequest;
import com.example.systemserviceofficial.system.dto.request.CreateUserRequest;
import com.example.systemserviceofficial.system.dto.request.UpdateUserRequest;
import com.example.systemserviceofficial.system.dto.response.UserResponse;
import com.example.systemserviceofficial.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @HasRole(role = "ADMIN")
    public BaseResponse<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<UserResponse> users = search != null && !search.isEmpty()
            ? userService.search(search, pageable)
            : userService.findAll(pageable);
        
        return BaseResponse.ok(users);
    }
    
    @GetMapping("/{id}")
    @HasRole(role = "ADMIN")
    public BaseResponse<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userService.findById(id);
        return BaseResponse.ok(user);
    }
    
    @PostMapping
    @HasRole(role = "ADMIN")
    @AuditLogAction(action = "CREATE_USER", resource = "/api/users")
    public BaseResponse<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        UserResponse user = userService.create(request);
        return BaseResponse.ok(user);
    }
    
    @PutMapping("/{id}")
    @HasRole(role = "ADMIN")
    @AuditLogAction(action = "UPDATE_USER", resource = "/api/users")
    public BaseResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        
        UserResponse user = userService.update(id, request);
        return BaseResponse.ok(user);
    }
    
    @DeleteMapping("/{id}")
    @HasRole(role = "ADMIN")
    @AuditLogAction(action = "DELETE_USER", resource = "/api/users")
    public BaseResponse<String> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return BaseResponse.ok("Xóa người dùng thành công");
    }
    
    @PostMapping("/{id}/roles")
    @HasRole(role = "ADMIN")
    @AuditLogAction(action = "ASSIGN_ROLE", resource = "/api/users/roles")
    public BaseResponse<String> assignRoles(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequest request) {
        
        userService.assignRoles(id, request.getRoleIds());
        return BaseResponse.ok("Gán quyền thành công");
    }
}
