package com.example.systemserviceofficial.test;

import com.example.commonserviceofficial.notification.dto.EmailRequest;
import com.example.commonserviceofficial.notification.dto.EmailResponse;
import com.example.commonserviceofficial.notification.service.EmailService;
import com.example.systemserviceofficial.notification.service.SystemEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Test controller cho Email Service
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailService emailService;
    private final SystemEmailService systemEmailService;

    /**
     * Test basic email
     */
    @PostMapping("/email/simple")
    public Map<String, Object> testSimpleEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String message) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            EmailRequest request = EmailRequest.builder()
                    .to(List.of(to))
                    .subject(subject)
                    .textContent(message)
                    .build();

            EmailResponse response = emailService.sendEmail(request);
            
            result.put("success", true);
            result.put("response", response);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * Test welcome email
     */
    @PostMapping("/email/welcome")
    public Map<String, Object> testWelcomeEmail(@RequestParam String to, @RequestParam String userName, @RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            CompletableFuture<EmailResponse> future = systemEmailService.sendWelcomeEmail(to, userName, userId);
            EmailResponse response = future.get();
            
            result.put("success", true);
            result.put("response", response);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * Test password reset email
     */
    @PostMapping("/email/reset-password")
    public Map<String, Object> testPasswordResetEmail(@RequestParam String to, @RequestParam String userName, @RequestParam String token) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            CompletableFuture<EmailResponse> future = systemEmailService.sendPasswordResetEmail(to, userName, token);
            EmailResponse response = future.get();
            
            result.put("success", true);
            result.put("response", response);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * Test email service health
     */
    @GetMapping("/email/health")
    public Map<String, Object> testEmailHealth() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean isHealthy = emailService.isEmailServiceHealthy();
            
            result.put("healthy", isHealthy);
            result.put("message", isHealthy ? "Email service is healthy" : "Email service is not healthy");
            
        } catch (Exception e) {
            result.put("healthy", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}