# Generate BCrypt Password

## Cách 1: Dùng Online Tool
Truy cập: https://bcrypt-generator.com/
- Nhập password: `Admin@123`
- Rounds: 10
- Copy hash

## Cách 2: Dùng Java Code

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Admin@123";
        String encoded = encoder.encode(password);
        System.out.println("Encoded password: " + encoded);
        
        // Test verify
        boolean matches = encoder.matches(password, encoded);
        System.out.println("Matches: " + matches);
    }
}
```

## Cách 3: Dùng Spring Boot Application

Thêm vào controller test:

```java
@GetMapping("/test/encode-password")
public String encodePassword(@RequestParam String password) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.encode(password);
}
```

## Password hiện tại trong DB

Password trong migration: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`

Để test xem password này có đúng không:
1. Chạy app
2. Gọi API login với username: `admin`, password: `Admin@123`
3. Nếu lỗi, cần update lại password trong DB

## Update Password trong DB

```sql
-- Update password cho user admin
UPDATE sys_user 
SET password = '$2a$10$NEW_HASH_HERE'
WHERE username = 'admin';
```

## Lưu ý

- BCrypt mỗi lần encode sẽ tạo ra hash khác nhau (do salt ngẫu nhiên)
- Nhưng tất cả đều verify được với password gốc
- Nếu đăng ký mới và không login được, có thể do:
  1. PasswordEncoder bean không được inject đúng
  2. Password không được encode khi save
  3. Verify logic bị sai
