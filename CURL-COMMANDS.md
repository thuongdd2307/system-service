# System Service API - CURL Commands

## Base URL
```
http://localhost:8083
```

## Authentication APIs

### 1. Login
```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123"
  }'
```

**Test Users:**
- Username: `admin` / Password: `Admin@123` (Super Admin)
- Username: `manager` / Password: `Admin@123` (Manager)
- Username: `user` / Password: `Admin@123` (User)

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

### 2. Logout
```bash
curl -X POST http://localhost:8083/api/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 3. Refresh Token
```bash
curl -X POST http://localhost:8083/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

### 4. Register (Public - Đăng ký tài khoản thường)
```bash
curl -X POST http://localhost:8083/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "Password@123",
    "email": "newuser@example.com",
    "fullName": "New User",
    "phone": "0123456789"
  }'
```

**Note:** API này public, không cần token. Tài khoản được tạo sẽ có role USER mặc định.

## User Management APIs

### 1. Get All Users
```bash
curl -X GET "http://localhost:8083/api/users?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 2. Get User by ID
```bash
curl -X GET http://localhost:8083/api/users/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 3. Create User (Admin - Tạo tài khoản với role tùy chỉnh)
```bash
curl -X POST http://localhost:8083/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "username": "testuser",
    "password": "Test@123",
    "email": "test@example.com",
    "fullName": "Test User",
    "phone": "0987654321",
    "roleIds": [4]
  }'
```

**Note:** API này cần quyền admin (USER_CREATE permission). Có thể chỉ định role khi tạo.

### 4. Update User
```bash
curl -X PUT http://localhost:8083/api/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "email": "updated@example.com",
    "fullName": "Updated Name",
    "phone": "0999999999"
  }'
```

### 5. Delete User
```bash
curl -X DELETE http://localhost:8083/api/users/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 6. Assign Roles to User
```bash
curl -X POST http://localhost:8083/api/users/1/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "roleIds": [1, 2]
  }'
```

### 7. Change Password
```bash
curl -X PUT http://localhost:8083/api/users/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "oldPassword": "Admin@123",
    "newPassword": "NewPassword@123"
  }'
```

## Audit Log APIs

### 1. Get All Audit Logs
```bash
curl -X GET "http://localhost:8083/api/audit-logs?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 2. Search Audit Logs
```bash
curl -X GET "http://localhost:8083/api/audit-logs/search?username=admin&action=LOGIN&startDate=2026-01-01&endDate=2026-12-31&page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 3. Export Audit Logs to Excel
```bash
curl -X GET "http://localhost:8083/api/audit-logs/export?username=admin&action=LOGIN" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -o audit-logs.xlsx
```

## Health Check

### Actuator Health
```bash
curl -X GET http://localhost:8083/actuator/health
```

### Actuator Info
```bash
curl -X GET http://localhost:8083/actuator/info
```

## Notes

1. Replace `YOUR_ACCESS_TOKEN` with the actual token received from login
2. Replace `YOUR_REFRESH_TOKEN` with the actual refresh token
3. All authenticated endpoints require `Authorization: Bearer TOKEN` header
4. Default password for test users is `Admin@123`
5. Server runs on port `8083`

## Windows PowerShell Alternative

If using PowerShell, use `Invoke-RestMethod`:

```powershell
# Login
$body = @{
    username = "admin"
    password = "Admin@123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8083/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body

$token = $response.data.accessToken

# Get Users
Invoke-RestMethod -Uri "http://localhost:8083/api/users?page=0&size=10" `
    -Method GET `
    -Headers @{Authorization = "Bearer $token"}
```
