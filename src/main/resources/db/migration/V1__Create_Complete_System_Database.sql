-- =====================================================
-- HDDT System Service - Complete Database Schema
-- Version: 1.0
-- Created: 2026-02-05
-- =====================================================

-- =====================================================
-- 1. SYSTEM TABLES
-- =====================================================

-- Permissions Table
CREATE TABLE sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    resource VARCHAR(255),
    action VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Roles Table
CREATE TABLE sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Role-Permission Mapping Table
CREATE TABLE sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
);

-- Users Table
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE',
    failed_login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP NULL,
    last_login_at TIMESTAMP NULL,
    last_login_ip VARCHAR(45),
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- User-Role Mapping Table
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

-- Refresh Tokens Table
CREATE TABLE sys_refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
);

-- Token Blacklist Table
CREATE TABLE sys_token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit Log Table
CREATE TABLE sys_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trace_id VARCHAR(100),
    user_id BIGINT,
    username VARCHAR(100),
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(255),
    method VARCHAR(10),
    url VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent TEXT,
    request_body TEXT,
    response_body TEXT,
    status_code INT,
    execution_time BIGINT,
    success BOOLEAN DEFAULT TRUE,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE SET NULL
);

-- =====================================================
-- 2. INDEXES FOR PERFORMANCE
-- =====================================================

-- User indexes
CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_user_email ON sys_user(email);
CREATE INDEX idx_user_status ON sys_user(status);
CREATE INDEX idx_user_created_at ON sys_user(created_at);

-- Role indexes
CREATE INDEX idx_role_code ON sys_role(code);
CREATE INDEX idx_role_status ON sys_role(status);

-- Permission indexes
CREATE INDEX idx_permission_code ON sys_permission(code);
CREATE INDEX idx_permission_resource ON sys_permission(resource);

-- Refresh token indexes
CREATE INDEX idx_token ON sys_refresh_token(token);
CREATE INDEX idx_user_id ON sys_refresh_token(user_id);
CREATE INDEX idx_expires_at ON sys_refresh_token(expires_at);

-- Token blacklist indexes
CREATE INDEX idx_token ON sys_token_blacklist(token);
CREATE INDEX idx_expires_at ON sys_token_blacklist(expires_at);

-- Audit log indexes
CREATE INDEX idx_audit_user_id ON sys_audit_log(user_id);
CREATE INDEX idx_audit_username ON sys_audit_log(username);
CREATE INDEX idx_audit_action ON sys_audit_log(action);
CREATE INDEX idx_audit_created_at ON sys_audit_log(created_at);
CREATE INDEX idx_audit_trace_id ON sys_audit_log(trace_id);

-- =====================================================
-- 3. INITIAL DATA
-- =====================================================

-- Insert System Permissions
INSERT INTO sys_permission (code, name, description, resource, action, created_by) VALUES
-- User Management
('USER_VIEW', 'View Users', 'Permission to view user list and details', 'USER', 'READ', 'SYSTEM'),
('USER_CREATE', 'Create User', 'Permission to create new users', 'USER', 'CREATE', 'SYSTEM'),
('USER_UPDATE', 'Update User', 'Permission to update user information', 'USER', 'UPDATE', 'SYSTEM'),
('USER_DELETE', 'Delete User', 'Permission to delete users', 'USER', 'DELETE', 'SYSTEM'),
('USER_ASSIGN_ROLE', 'Assign User Role', 'Permission to assign roles to users', 'USER', 'ASSIGN_ROLE', 'SYSTEM'),

-- Role Management
('ROLE_VIEW', 'View Roles', 'Permission to view role list and details', 'ROLE', 'READ', 'SYSTEM'),
('ROLE_CREATE', 'Create Role', 'Permission to create new roles', 'ROLE', 'CREATE', 'SYSTEM'),
('ROLE_UPDATE', 'Update Role', 'Permission to update role information', 'ROLE', 'UPDATE', 'SYSTEM'),
('ROLE_DELETE', 'Delete Role', 'Permission to delete roles', 'ROLE', 'DELETE', 'SYSTEM'),
('ROLE_ASSIGN_PERMISSION', 'Assign Role Permission', 'Permission to assign permissions to roles', 'ROLE', 'ASSIGN_PERMISSION', 'SYSTEM'),

-- Permission Management
('PERMISSION_VIEW', 'View Permissions', 'Permission to view permission list', 'PERMISSION', 'READ', 'SYSTEM'),
('PERMISSION_CREATE', 'Create Permission', 'Permission to create new permissions', 'PERMISSION', 'CREATE', 'SYSTEM'),
('PERMISSION_UPDATE', 'Update Permission', 'Permission to update permissions', 'PERMISSION', 'UPDATE', 'SYSTEM'),
('PERMISSION_DELETE', 'Delete Permission', 'Permission to delete permissions', 'PERMISSION', 'DELETE', 'SYSTEM'),

-- Audit Log Management
('AUDIT_VIEW', 'View Audit Logs', 'Permission to view audit logs', 'AUDIT', 'READ', 'SYSTEM'),
('AUDIT_EXPORT', 'Export Audit Logs', 'Permission to export audit logs', 'AUDIT', 'EXPORT', 'SYSTEM'),

-- System Management
('SYSTEM_CONFIG', 'System Configuration', 'Permission to configure system settings', 'SYSTEM', 'CONFIG', 'SYSTEM'),
('SYSTEM_MONITOR', 'System Monitoring', 'Permission to monitor system health', 'SYSTEM', 'MONITOR', 'SYSTEM');

-- Insert System Roles
INSERT INTO sys_role (code, name, description, status, created_by) VALUES
('SUPER_ADMIN', 'Super Administrator', 'Full system access with all permissions', 'ACTIVE', 'SYSTEM'),
('ADMIN', 'Administrator', 'Administrative access with most permissions', 'ACTIVE', 'SYSTEM'),
('MANAGER', 'Manager', 'Management level access', 'ACTIVE', 'SYSTEM'),
('USER', 'Regular User', 'Basic user access', 'ACTIVE', 'SYSTEM');

-- Assign All Permissions to Super Admin Role
INSERT INTO sys_role_permission (role_id, permission_id, created_by)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'SUPER_ADMIN'),
    p.id,
    'SYSTEM'
FROM sys_permission p;

-- Assign Most Permissions to Admin Role (exclude some system-level permissions)
INSERT INTO sys_role_permission (role_id, permission_id, created_by)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'ADMIN'),
    p.id,
    'SYSTEM'
FROM sys_permission p
WHERE p.code NOT IN ('SYSTEM_CONFIG', 'PERMISSION_CREATE', 'PERMISSION_UPDATE', 'PERMISSION_DELETE');

-- Assign Limited Permissions to Manager Role
INSERT INTO sys_role_permission (role_id, permission_id, created_by)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'MANAGER'),
    p.id,
    'SYSTEM'
FROM sys_permission p
WHERE p.code IN (
    'USER_VIEW', 'USER_CREATE', 'USER_UPDATE', 
    'ROLE_VIEW', 
    'AUDIT_VIEW', 'AUDIT_EXPORT',
    'SYSTEM_MONITOR'
);

-- Assign Basic Permissions to User Role
INSERT INTO sys_role_permission (role_id, permission_id, created_by)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'USER'),
    p.id,
    'SYSTEM'
FROM sys_permission p
WHERE p.code IN ('USER_VIEW', 'AUDIT_VIEW');

-- Insert Default Users
-- Password: Admin@123 (BCrypt encoded)
INSERT INTO sys_user (username, password, email, full_name, phone, status, created_by) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRdvX1PM3G/wJvz8hQLGdHr4H8C', 'admin@hddt.com', 'System Administrator', '0901234567', 'ACTIVE', 'SYSTEM'),
('manager', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRdvX1PM3G/wJvz8hQLGdHr4H8C', 'manager@hddt.com', 'System Manager', '0901234568', 'ACTIVE', 'SYSTEM'),
('user', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRdvX1PM3G/wJvz8hQLGdHr4H8C', 'user@hddt.com', 'Regular User', '0901234569', 'ACTIVE', 'SYSTEM');

-- Assign Roles to Users
INSERT INTO sys_user_role (user_id, role_id, created_by) VALUES
-- Admin user gets SUPER_ADMIN role
((SELECT id FROM sys_user WHERE username = 'admin'), (SELECT id FROM sys_role WHERE code = 'SUPER_ADMIN'), 'SYSTEM'),
-- Manager user gets MANAGER role
((SELECT id FROM sys_user WHERE username = 'manager'), (SELECT id FROM sys_role WHERE code = 'MANAGER'), 'SYSTEM'),
-- Regular user gets USER role
((SELECT id FROM sys_user WHERE username = 'user'), (SELECT id FROM sys_role WHERE code = 'USER'), 'SYSTEM');

-- =====================================================
-- 4. SAMPLE AUDIT LOG ENTRIES
-- =====================================================

INSERT INTO sys_audit_log (trace_id, user_id, username, action, resource, method, url, ip_address, user_agent, status_code, execution_time, success, created_at) VALUES
('trace-001', (SELECT id FROM sys_user WHERE username = 'admin'), 'admin', 'LOGIN', 'AUTH', 'POST', '/api/auth/login', '127.0.0.1', 'Mozilla/5.0', 200, 150, TRUE, NOW() - INTERVAL 1 DAY),
('trace-002', (SELECT id FROM sys_user WHERE username = 'admin'), 'admin', 'USER_VIEW', 'USER', 'GET', '/api/users', '127.0.0.1', 'Mozilla/5.0', 200, 50, TRUE, NOW() - INTERVAL 1 DAY),
('trace-003', (SELECT id FROM sys_user WHERE username = 'manager'), 'manager', 'LOGIN', 'AUTH', 'POST', '/api/auth/login', '192.168.1.100', 'Mozilla/5.0', 200, 120, TRUE, NOW() - INTERVAL 2 HOUR),
('trace-004', (SELECT id FROM sys_user WHERE username = 'user'), 'user', 'LOGIN', 'AUTH', 'POST', '/api/auth/login', '192.168.1.101', 'Mozilla/5.0', 401, 80, FALSE, NOW() - INTERVAL 1 HOUR);

-- =====================================================
-- 5. CLEANUP PROCEDURES (Optional)
-- =====================================================

-- Create event to cleanup expired tokens (requires EVENT_SCHEDULER to be ON)
-- DELIMITER $$
-- CREATE EVENT IF NOT EXISTS cleanup_expired_tokens
-- ON SCHEDULE EVERY 1 HOUR
-- DO
-- BEGIN
--     DELETE FROM sys_refresh_token WHERE expires_at < NOW();
--     DELETE FROM sys_token_blacklist WHERE expires_at < NOW();
-- END$$
-- DELIMITER ;

-- =====================================================
-- 6. VIEWS FOR COMMON QUERIES
-- =====================================================

-- User with Roles View
CREATE VIEW v_user_roles AS
SELECT 
    u.id as user_id,
    u.username,
    u.email,
    u.full_name,
    u.status as user_status,
    r.id as role_id,
    r.code as role_code,
    r.name as role_name,
    r.status as role_status
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id;

-- Role with Permissions View
CREATE VIEW v_role_permissions AS
SELECT 
    r.id as role_id,
    r.code as role_code,
    r.name as role_name,
    p.id as permission_id,
    p.code as permission_code,
    p.name as permission_name,
    p.resource,
    p.action
FROM sys_role r
LEFT JOIN sys_role_permission rp ON r.id = rp.role_id
LEFT JOIN sys_permission p ON rp.permission_id = p.id;

-- User with All Permissions View
CREATE VIEW v_user_permissions AS
SELECT DISTINCT
    u.id as user_id,
    u.username,
    p.id as permission_id,
    p.code as permission_code,
    p.name as permission_name,
    p.resource,
    p.action
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE u.status = 'ACTIVE' AND r.status = 'ACTIVE';

-- =====================================================
-- END OF MIGRATION
-- =====================================================