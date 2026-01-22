# CURL Commands - System Service Email Integration

## 🚀 Test Commands cho Email Integration trong System Service

### 1. User Registration (Gửi Welcome Email)

```bash
# Đăng ký user mới - sẽ tự động gửi welcome email
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "testuser@example.com",
    "fullName": "Test User",
    "phone": "+84901234567"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Đăng ký thành công. Vui lòng kiểm tra email để kích hoạt tài khoản.",
  "data": null
}
```

### 2. Forgot Password (Gửi Reset Password Email)

```bash
# Yêu cầu reset password - sẽ gửi email với reset token
curl -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Email hướng dẫn đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư.",
  "data": null
}
```

### 3. Reset Password với Token

```bash
# Reset password với token từ email
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "your-reset-token-from-email",
    "newPassword": "newpassword123",
    "confirmPassword": "newpassword123"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Đặt lại mật khẩu thành công. Vui lòng đăng nhập với mật khẩu mới.",
  "data": null
}
```

### 4. Login (Gửi Login Notification Email)

```bash
# Đăng nhập - sẽ gửi email thông báo đăng nhập (nếu enabled)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -H "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36" \
  -d '{
    "username": "testuser",
    "password": "newpassword123"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userId": 1,
    "username": "testuser",
    "email": "testuser@example.com",
    "fullName": "Test User",
    "roles": ["ROLE_USER"]
  }
}
```

### 5. Change Password (Gửi Password Changed Email)

```bash
# Thay đổi mật khẩu khi đã đăng nhập - sẽ gửi email thông báo
curl -X POST http://localhost:8081/api/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "oldPassword": "newpassword123",
    "newPassword": "changedpassword123",
    "confirmPassword": "changedpassword123"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Thay đổi mật khẩu thành công.",
  "data": null
}
```

## 🧪 Complete Test Scenarios

### Scenario 1: Full Registration Flow

```bash
echo "=== Registration Flow Test ==="

# 1. Register new user
echo "1. Registering new user..."
curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2026",
    "password": "password123",
    "email": "newuser2026@example.com",
    "fullName": "New User 2026",
    "phone": "+84987654321"
  }' | jq

echo "✅ Check your email for welcome message!"

# 2. Login with new user
echo -e "\n2. Logging in with new user..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -H "User-Agent: Test-Browser/1.0" \
  -d '{
    "username": "newuser2026",
    "password": "password123"
  }')

echo $LOGIN_RESPONSE | jq

# Extract access token
ACCESS_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.accessToken')
echo "Access Token: $ACCESS_TOKEN"

echo "✅ Check your email for login notification (if enabled)!"
```

### Scenario 2: Password Reset Flow

```bash
echo "=== Password Reset Flow Test ==="

# 1. Request password reset
echo "1. Requesting password reset..."
curl -s -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser2026@example.com"
  }' | jq

echo "✅ Check your email for password reset instructions!"
echo "⚠️  Copy the reset token from email and use it in next step"

# 2. Reset password (you need to get token from email)
read -p "Enter reset token from email: " RESET_TOKEN

echo -e "\n2. Resetting password with token..."
curl -s -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d "{
    \"token\": \"$RESET_TOKEN\",
    \"newPassword\": \"resetpassword123\",
    \"confirmPassword\": \"resetpassword123\"
  }" | jq

echo "✅ Check your email for password changed confirmation!"

# 3. Login with new password
echo -e "\n3. Logging in with new password..."
curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2026",
    "password": "resetpassword123"
  }' | jq
```

### Scenario 3: Change Password Flow

```bash
echo "=== Change Password Flow Test ==="

# 1. Login first
echo "1. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2026",
    "password": "resetpassword123"
  }')

ACCESS_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.accessToken')
echo "Logged in successfully"

# 2. Change password
echo -e "\n2. Changing password..."
curl -s -X POST http://localhost:8081/api/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "oldPassword": "resetpassword123",
    "newPassword": "finalpassword123",
    "confirmPassword": "finalpassword123"
  }' | jq

echo "✅ Check your email for password changed confirmation!"

# 3. Test login with new password
echo -e "\n3. Testing login with new password..."
curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser2026",
    "password": "finalpassword123"
  }' | jq
```

## 📧 Email Templates Test

### Test Welcome Email Template

```bash
# Register user to trigger welcome email
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "emailtest1",
    "password": "password123",
    "email": "emailtest1@example.com",
    "fullName": "Email Test User 1",
    "phone": "+84901111111"
  }'
```

### Test Password Reset Email Template

```bash
# Request password reset to trigger reset email
curl -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "emailtest1@example.com"
  }'
```

## 🔧 Configuration Test

### Test với Email Disabled

```bash
# Set environment variable to disable email
export EMAIL_NOTIFICATION_ENABLED=false

# Restart application and test registration
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "noemailuser",
    "password": "password123",
    "email": "noemail@example.com",
    "fullName": "No Email User",
    "phone": "+84902222222"
  }'

# Should register successfully but no email sent
```

### Test với Login Notification Enabled

```bash
# Enable login notification
export LOGIN_NOTIFICATION_ENABLED=true

# Restart application and test login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -H "User-Agent: Test-Browser/1.0 (Windows NT 10.0; Win64; x64)" \
  -d '{
    "username": "emailtest1",
    "password": "password123"
  }'

# Should send login notification email
```

## 🐛 Error Scenarios Test

### Test Invalid Email Format

```bash
# Test registration with invalid email
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "invalidemailuser",
    "password": "password123",
    "email": "invalid-email",
    "fullName": "Invalid Email User",
    "phone": "+84903333333"
  }'
```

### Test Forgot Password with Non-existent Email

```bash
# Test forgot password with email that doesn't exist
curl -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com"
  }'
```

### Test Reset Password with Invalid Token

```bash
# Test reset password with invalid token
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "invalid-token-123",
    "newPassword": "newpassword123",
    "confirmPassword": "newpassword123"
  }'
```

### Test Password Mismatch

```bash
# Test reset password with mismatched passwords
curl -X POST http://localhost:8081/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "valid-token-from-email",
    "newPassword": "password123",
    "confirmPassword": "differentpassword123"
  }'
```

## 📊 Expected Email Content

### Welcome Email
- **Subject:** "Chào mừng bạn đến với hệ thống HDDT!"
- **Content:** User info, login URL, features, support contact
- **Template:** `email/welcome.html`

### Password Reset Email
- **Subject:** "Yêu cầu đặt lại mật khẩu - HDDT System"
- **Content:** Reset link, expiry time, security tips
- **Template:** `email/password-reset.html`

### Login Notification Email
- **Subject:** "Thông báo đăng nhập - HDDT System"
- **Content:** Login time, IP address, user agent
- **Template:** `email/login-notification.html`

### Password Changed Email
- **Subject:** "Mật khẩu đã được thay đổi - HDDT System"
- **Content:** Change confirmation, security advice
- **Template:** `email/password-changed.html`

## 🚀 Quick Test Script

```bash
#!/bin/bash

echo "=== System Service Email Integration Test ==="

# Test registration with welcome email
echo "1. Testing Registration + Welcome Email:"
curl -s -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "quicktest",
    "password": "password123",
    "email": "quicktest@example.com",
    "fullName": "Quick Test User",
    "phone": "+84999999999"
  }' | jq

echo -e "\n2. Testing Forgot Password:"
curl -s -X POST http://localhost:8081/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "quicktest@example.com"
  }' | jq

echo -e "\n3. Testing Login:"
curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -H "User-Agent: QuickTest-Browser/1.0" \
  -d '{
    "username": "quicktest",
    "password": "password123"
  }' | jq

echo -e "\n✅ Test completed! Check your email inbox for:"
echo "   - Welcome email"
echo "   - Password reset email"
echo "   - Login notification email (if enabled)"

echo -e "\n📧 Email Configuration:"
echo "   MAIL_HOST: ${MAIL_HOST:-smtp.gmail.com}"
echo "   MAIL_USERNAME: ${MAIL_USERNAME:-not set}"
echo "   EMAIL_NOTIFICATION_ENABLED: ${EMAIL_NOTIFICATION_ENABLED:-true}"
```

Lưu script trên thành `test-system-email.sh` và chạy:
```bash
chmod +x test-system-email.sh
./test-system-email.sh
```

## ⚙️ Environment Variables

```bash
# Email SMTP Configuration
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export MAIL_FROM_NAME="HDDT System"

# Application Configuration
export APP_BASE_URL=http://localhost:8081

# Email Notification Toggles
export EMAIL_NOTIFICATION_ENABLED=true
export WELCOME_EMAIL_ENABLED=true
export LOGIN_NOTIFICATION_ENABLED=false
export PASSWORD_RESET_EMAIL_ENABLED=true
export PASSWORD_CHANGED_EMAIL_ENABLED=true

# JWT Configuration
export JWT_SECRET=THIS_IS_A_32_BYTE_SECRET_KEY_FOR_JWT_2026
```