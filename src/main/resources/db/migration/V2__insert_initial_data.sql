-- Insert Permissions
INSERT INTO sys_permission (code, name, resource, action, description, status, created_at, updated_at) VALUES
-- User Management
('USER_VIEW', 'Xem người dùng', 'USER', 'VIEW', 'Quyền xem danh sách và chi tiết người dùng', 'ACTIVE', NOW(), NOW()),
('USER_CREATE', 'Tạo người dùng', 'USER', 'CREATE', 'Quyền tạo người dùng mới', 'ACTIVE', NOW(), NOW()),
('USER_UPDATE', 'Cập nhật người dùng', 'USER', 'UPDATE', 'Quyền cập nhật thông tin người dùng', 'ACTIVE', NOW(), NOW()),
('USER_DELETE', 'Xóa người dùng', 'USER', 'DELETE', 'Quyền xóa người dùng', 'ACTIVE', NOW(), NOW()),

-- Role Management
('ROLE_VIEW', 'Xem vai trò', 'ROLE', 'VIEW', 'Quyền xem danh sách và chi tiết vai trò', 'ACTIVE', NOW(), NOW()),
('ROLE_CREATE', 'Tạo vai trò', 'ROLE', 'CREATE', 'Quyền tạo vai trò mới', 'ACTIVE', NOW(), NOW()),
('ROLE_UPDATE', 'Cập nhật vai trò', 'ROLE', 'UPDATE', 'Quyền cập nhật thông tin vai trò', 'ACTIVE', NOW(), NOW()),
('ROLE_DELETE', 'Xóa vai trò', 'ROLE', 'DELETE', 'Quyền xóa vai trò', 'ACTIVE', NOW(), NOW()),

-- Permission Management
('PERMISSION_VIEW', 'Xem quyền', 'PERMISSION', 'VIEW', 'Quyền xem danh sách và chi tiết quyền', 'ACTIVE', NOW(), NOW()),
('PERMISSION_CREATE', 'Tạo quyền', 'PERMISSION', 'CREATE', 'Quyền tạo quyền mới', 'ACTIVE', NOW(), NOW()),
('PERMISSION_UPDATE', 'Cập nhật quyền', 'PERMISSION', 'UPDATE', 'Quyền cập nhật thông tin quyền', 'ACTIVE', NOW(), NOW()),
('PERMISSION_DELETE', 'Xóa quyền', 'PERMISSION', 'DELETE', 'Quyền xóa quyền', 'ACTIVE', NOW(), NOW()),

-- Audit Log
('AUDIT_VIEW', 'Xem nhật ký', 'AUDIT', 'VIEW', 'Quyền xem nhật ký hệ thống', 'ACTIVE', NOW(), NOW()),
('AUDIT_EXPORT', 'Xuất nhật ký', 'AUDIT', 'EXPORT', 'Quyền xuất nhật ký hệ thống', 'ACTIVE', NOW(), NOW());

-- Insert Roles
INSERT INTO sys_role (code, name, description, status, created_at, updated_at) VALUES
('SUPER_ADMIN', 'Super Admin', 'Quản trị viên cấp cao nhất, có toàn quyền trên hệ thống', 'ACTIVE', NOW(), NOW()),
('ADMIN', 'Admin', 'Quản trị viên hệ thống', 'ACTIVE', NOW(), NOW()),
('MANAGER', 'Manager', 'Quản lý', 'ACTIVE', NOW(), NOW()),
('USER', 'User', 'Người dùng thông thường', 'ACTIVE', NOW(), NOW());

-- Assign all permissions to SUPER_ADMIN
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'SUPER_ADMIN'),
    id
FROM sys_permission;

-- Assign permissions to ADMIN (all except some sensitive ones)
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'ADMIN'),
    id
FROM sys_permission
WHERE code IN (
    'USER_VIEW', 'USER_CREATE', 'USER_UPDATE',
    'ROLE_VIEW', 'ROLE_CREATE', 'ROLE_UPDATE',
    'PERMISSION_VIEW',
    'AUDIT_VIEW', 'AUDIT_EXPORT'
);

-- Assign permissions to MANAGER
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'MANAGER'),
    id
FROM sys_permission
WHERE code IN (
    'USER_VIEW', 'USER_CREATE', 'USER_UPDATE',
    'ROLE_VIEW',
    'AUDIT_VIEW'
);

-- Assign permissions to USER
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 
    (SELECT id FROM sys_role WHERE code = 'USER'),
    id
FROM sys_permission
WHERE code IN ('USER_VIEW');

-- Insert default users
-- Password: Admin@123 (BCrypt hashed)
INSERT INTO sys_user (username, password, email, full_name, phone, status, created_at, updated_at) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@example.com', 'Administrator', '0123456789', 'ACTIVE', NOW(), NOW()),
('manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'manager@example.com', 'Manager User', '0123456788', 'ACTIVE', NOW(), NOW()),
('user', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user@example.com', 'Normal User', '0123456787', 'ACTIVE', NOW(), NOW());

-- Assign roles to users
INSERT INTO sys_user_role (user_id, role_id) VALUES
((SELECT id FROM sys_user WHERE username = 'admin'), (SELECT id FROM sys_role WHERE code = 'SUPER_ADMIN')),
((SELECT id FROM sys_user WHERE username = 'manager'), (SELECT id FROM sys_role WHERE code = 'MANAGER')),
((SELECT id FROM sys_user WHERE username = 'user'), (SELECT id FROM sys_role WHERE code = 'USER'));
