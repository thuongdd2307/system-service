# Hướng Dẫn Fix Vấn Đề Password

## Vấn Đề
Đăng ký tài khoản mới rồi đăng nhập bị báo sai mật khẩu.

## Nguyên Nhân
Hash password trong migration V2 không đúng với password "Admin@123".

## Giải Pháp Đã Áp Dụng

### 1. Tạo Migration V3 để Fix Password
File: `V3__fix_user_passwords.sql`
- Update password hash đúng cho 3 user: admin, manager, user
- Hash mới: `$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi`
- Password: `Admin@123`

### 2. Cách Test

#### Bước 1: Chạy lại ứng dụng
```bash
./mvnw.cmd spring-boot:run
```

#### Bước 2: Test API encode password
```bash
curl "http://localhost:8083/api/test/encode-password?password=Admin@123"
```

Kết quả sẽ trả về hash mới. Verify rằng `matches = true`.

#### Bước 3: Test login với user có sẵn
```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}'
```

Nếu thành công → Vấn đề đã được fix!

#### Bước 4: Test đăng ký user mới
```bash
curl -X POST http://localhost:8083/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@123",
    "email": "test@example.com",
    "fullName": "Test User",
    "phone": "0123456789"
  }'
```

#### Bước 5: Test login với user mới
```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test@123"}'
```

## Nếu Vẫn Lỗi

### Cách 1: Xóa database và chạy lại
```sql
DROP DATABASE `tgs-system`;
CREATE DATABASE `tgs-system`;
```

Sau đó restart app để Flyway chạy lại tất cả migration.

### Cách 2: Chạy migration V3 thủ công
```sql
USE `tgs-system`;

UPDATE sys_user 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE username IN ('admin', 'manager', 'user');
```

### Cách 3: Generate hash mới
1. Gọi API: `GET /api/test/encode-password?password=Admin@123`
2. Copy hash từ response
3. Update vào database:
```sql
UPDATE sys_user 
SET password = 'HASH_MỚI_Ở_ĐÂY'
WHERE username = 'admin';
```

## Kiểm Tra Password Hash

### Verify password có đúng không:
```bash
curl -X POST "http://localhost:8083/api/test/verify-password?rawPassword=Admin@123&encodedPassword=\$2a\$10\$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"
```

Kết quả `matches: true` → Hash đúng
Kết quả `matches: false` → Hash sai

## Lưu Ý

1. **BCrypt mỗi lần encode tạo hash khác nhau** (do salt ngẫu nhiên)
   - Nhưng tất cả đều verify được với password gốc
   
2. **Hash trong migration phải được test trước**
   - Không nên copy hash từ nguồn không rõ ràng
   
3. **Password requirements:**
   - Tối thiểu 8 ký tự
   - Có chữ hoa
   - Có chữ thường  
   - Có số
   - (Không bắt buộc ký tự đặc biệt)

## Test Accounts Sau Khi Fix

| Username | Password | Role |
|----------|----------|------|
| admin | Admin@123 | SUPER_ADMIN |
| manager | Admin@123 | MANAGER |
| user | Admin@123 | USER |
