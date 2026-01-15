package com.example.systemserviceofficial.system.service;

import com.example.commonserviceofficial.exception.BusinessException;
import com.example.systemserviceofficial.system.dto.request.CreateUserRequest;
import com.example.systemserviceofficial.system.dto.request.UpdateUserRequest;
import com.example.systemserviceofficial.system.dto.response.UserResponse;
import com.example.systemserviceofficial.system.entity.Role;
import com.example.systemserviceofficial.system.entity.User;
import com.example.systemserviceofficial.system.repository.RoleRepository;
import com.example.systemserviceofficial.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findByDeletedAtIsNull(pageable)
            .map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<UserResponse> search(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, pageable)
            .map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
                "USER_NOT_FOUND",
                "Không tìm thấy người dùng"
            ));
        return toResponse(user);
    }
    
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        // Validate
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(
                "USERNAME_EXISTS",
                "Tên đăng nhập đã tồn tại"
            );
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                "EMAIL_EXISTS",
                "Email đã được sử dụng"
            );
        }
        
        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setStatus(request.getStatus());
        user.setFailedLoginAttempts(0);
        
        // Assign roles
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setRoles(roles);
        }
        
        user = userRepository.save(user);
        log.info("User created: {}", user.getUsername());
        
        return toResponse(user);
    }
    
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
                "USER_NOT_FOUND",
                "Không tìm thấy người dùng"
            ));
        
        // Update fields
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        user = userRepository.save(user);
        log.info("User updated: {}", user.getUsername());
        
        return toResponse(user);
    }
    
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException(
                "USER_NOT_FOUND",
                "Không tìm thấy người dùng"
            ));
        
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User deleted: {}", user.getUsername());
    }
    
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                "USER_NOT_FOUND",
                "Không tìm thấy người dùng"
            ));
        
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);
        
        log.info("Roles assigned to user: {}", user.getUsername());
    }
    
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .phone(user.getPhone())
            .avatarUrl(user.getAvatarUrl())
            .status(user.getStatus())
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
