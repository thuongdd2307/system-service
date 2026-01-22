package com.example.systemserviceofficial.notification.service;

import com.example.commonserviceofficial.notification.dto.EmailRequest;
import com.example.commonserviceofficial.notification.dto.EmailResponse;
import com.example.commonserviceofficial.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * System Email Service - Wrapper cho Common EmailService
 * Cung cấp các phương thức email cụ thể cho System Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemEmailService {

    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8082}")
    private String baseUrl;

    /**
     * Gửi email chào mừng khi đăng ký thành công
     */
    @Async
    public CompletableFuture<EmailResponse> sendWelcomeEmail(String toEmail, String userName, String userId) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("userEmail", toEmail);
            variables.put("userId", userId);
            variables.put("registrationDate", LocalDateTime.now().toString());
            variables.put("loginUrl", baseUrl + "/login");
            variables.put("supportEmail", "support@hddt.com");
            variables.put("year", LocalDateTime.now().getYear());

            EmailRequest request = EmailRequest.builder()
                    .to(List.of(toEmail))
                    .subject("Chào mừng bạn đến với hệ thống HDDT!")
                    .templateName("email/welcome")
                    .templateVariables(variables)
                    .fromName("HDDT System")
                    .category("welcome")
                    .priority(EmailRequest.EmailPriority.NORMAL)
                    .build();

            EmailResponse response = emailService.sendEmail(request);
            
            if ("SUCCESS".equals(response.getStatus())) {
                log.info("Welcome email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send welcome email to {}: {}", toEmail, response.getErrorDetails());
            }
            
            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
            EmailResponse errorResponse = EmailResponse.failed(null, List.of(toEmail), "Welcome Email", e.getMessage());
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    /**
     * Gửi email reset password
     */
    @Async
    public CompletableFuture<EmailResponse> sendPasswordResetEmail(String toEmail, String userName, String resetToken) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("userEmail", toEmail);
            variables.put("resetToken", resetToken);
            variables.put("resetUrl", baseUrl + "/reset-password?token=" + resetToken);
            variables.put("expiryMinutes", 30);
            variables.put("createdAt", LocalDateTime.now().toString());
            variables.put("supportEmail", "support@hddt.com");
            variables.put("year", LocalDateTime.now().getYear());

            EmailRequest request = EmailRequest.builder()
                    .to(List.of(toEmail))
                    .subject("Yêu cầu đặt lại mật khẩu - HDDT System")
                    .templateName("email/password-reset")
                    .templateVariables(variables)
                    .fromName("HDDT System")
                    .category("password-reset")
                    .priority(EmailRequest.EmailPriority.HIGH)
                    .build();

            EmailResponse response = emailService.sendEmailWithRetry(request); // Sử dụng retry cho email quan trọng
            
            if ("SUCCESS".equals(response.getStatus())) {
                log.info("Password reset email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send password reset email to {}: {}", toEmail, response.getErrorDetails());
            }
            
            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            EmailResponse errorResponse = EmailResponse.failed(null, List.of(toEmail), "Password Reset", e.getMessage());
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    /**
     * Gửi email OTP
     */
    @Async
    public CompletableFuture<EmailResponse> sendOtpEmail(String toEmail, String userName, String otpCode, int validityMinutes) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("userEmail", toEmail);
            variables.put("otpCode", otpCode);
            variables.put("validityMinutes", validityMinutes);
            variables.put("createdAt", LocalDateTime.now().toString());
            variables.put("supportEmail", "support@hddt.com");
            variables.put("year", LocalDateTime.now().getYear());

            EmailRequest request = EmailRequest.builder()
                    .to(List.of(toEmail))
                    .subject("Mã OTP xác thực - HDDT System")
                    .templateName("email/otp")
                    .templateVariables(variables)
                    .fromName("HDDT System")
                    .category("otp")
                    .priority(EmailRequest.EmailPriority.HIGH)
                    .build();

            EmailResponse response = emailService.sendEmailWithRetry(request);
            
            if ("SUCCESS".equals(response.getStatus())) {
                log.info("OTP email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send OTP email to {}: {}", toEmail, response.getErrorDetails());
            }
            
            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage(), e);
            EmailResponse errorResponse = EmailResponse.failed(null, List.of(toEmail), "OTP Email", e.getMessage());
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    /**
     * Gửi email thông báo đăng nhập thành công
     */
    @Async
    public CompletableFuture<EmailResponse> sendLoginNotificationEmail(String toEmail, String userName, String ipAddress, String userAgent) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("userEmail", toEmail);
            variables.put("loginTime", LocalDateTime.now().toString());
            variables.put("ipAddress", ipAddress);
            variables.put("userAgent", userAgent);
            variables.put("supportEmail", "support@hddt.com");
            variables.put("year", LocalDateTime.now().getYear());

            EmailRequest request = EmailRequest.builder()
                    .to(List.of(toEmail))
                    .subject("Thông báo đăng nhập - HDDT System")
                    .templateName("email/login-notification")
                    .templateVariables(variables)
                    .fromName("HDDT System")
                    .category("login-notification")
                    .priority(EmailRequest.EmailPriority.NORMAL)
                    .build();

            EmailResponse response = emailService.sendEmailAsync(request).get(); // Async nhưng đợi kết quả
            
            if ("SUCCESS".equals(response.getStatus())) {
                log.info("Login notification email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send login notification email to {}: {}", toEmail, response.getErrorDetails());
            }
            
            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("Failed to send login notification email to {}: {}", toEmail, e.getMessage(), e);
            EmailResponse errorResponse = EmailResponse.failed(null, List.of(toEmail), "Login Notification", e.getMessage());
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    /**
     * Gửi email thông báo thay đổi mật khẩu thành công
     */
    @Async
    public CompletableFuture<EmailResponse> sendPasswordChangedEmail(String toEmail, String userName) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", userName);
            variables.put("userEmail", toEmail);
            variables.put("changedAt", LocalDateTime.now().toString());
            variables.put("supportEmail", "support@hddt.com");
            variables.put("year", LocalDateTime.now().getYear());

            EmailRequest request = EmailRequest.builder()
                    .to(List.of(toEmail))
                    .subject("Mật khẩu đã được thay đổi - HDDT System")
                    .templateName("email/password-changed")
                    .templateVariables(variables)
                    .fromName("HDDT System")
                    .category("password-changed")
                    .priority(EmailRequest.EmailPriority.NORMAL)
                    .build();

            EmailResponse response = emailService.sendEmail(request);
            
            if ("SUCCESS".equals(response.getStatus())) {
                log.info("Password changed email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send password changed email to {}: {}", toEmail, response.getErrorDetails());
            }
            
            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("Failed to send password changed email to {}: {}", toEmail, e.getMessage(), e);
            EmailResponse errorResponse = EmailResponse.failed(null, List.of(toEmail), "Password Changed", e.getMessage());
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    /**
     * Gửi email với template tùy chỉnh
     */
    @Async
    public CompletableFuture<EmailResponse> sendCustomEmail(String toEmail, String subject, String templateName, Map<String, Object> variables) {
        try {
            // Add common variables
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("supportEmail", "support@hddt.com");
            variables.put("year", LocalDateTime.now().getYear());
            variables.put("baseUrl", baseUrl);

            EmailRequest request = EmailRequest.builder()
                    .to(List.of(toEmail))
                    .subject(subject)
                    .templateName(templateName)
                    .templateVariables(variables)
                    .fromName("HDDT System")
                    .category("custom")
                    .priority(EmailRequest.EmailPriority.NORMAL)
                    .build();

            EmailResponse response = emailService.sendEmail(request);
            
            if ("SUCCESS".equals(response.getStatus())) {
                log.info("Custom email sent successfully to: {} with template: {}", toEmail, templateName);
            } else {
                log.error("Failed to send custom email to {} with template {}: {}", toEmail, templateName, response.getErrorDetails());
            }
            
            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("Failed to send custom email to {} with template {}: {}", toEmail, templateName, e.getMessage(), e);
            EmailResponse errorResponse = EmailResponse.failed(null, List.of(toEmail), subject, e.getMessage());
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    /**
     * Gửi email đơn giản (text hoặc HTML)
     */
    @Async
    public CompletableFuture<EmailResponse> sendSimpleEmail(String toEmail, String subject, String content, boolean isHtml) {
        try {
            EmailRequest.EmailRequestBuilder builder = EmailRequest.builder()
                    .to(List.of(toEmail))
                    .subject(subject)
                    .fromName("HDDT System")
                    .category("simple")
                    .priority(EmailRequest.EmailPriority.NORMAL);

            if (isHtml) {
                builder.htmlContent(content);
            } else {
                builder.textContent(content);
            }

            EmailRequest request = builder.build();
            EmailResponse response = emailService.sendEmail(request);
            
            if ("SUCCESS".equals(response.getStatus())) {
                log.info("Simple email sent successfully to: {}", toEmail);
            } else {
                log.error("Failed to send simple email to {}: {}", toEmail, response.getErrorDetails());
            }
            
            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", toEmail, e.getMessage(), e);
            EmailResponse errorResponse = EmailResponse.failed(null, List.of(toEmail), subject, e.getMessage());
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    /**
     * Test email connection
     */
    public boolean testEmailConnection() {
        return emailService.isEmailServiceHealthy();
    }

    /**
     * Validate email request
     */
    public boolean validateEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
}