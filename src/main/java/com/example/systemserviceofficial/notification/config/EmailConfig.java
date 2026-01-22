package com.example.systemserviceofficial.notification.config;

import com.example.commonserviceofficial.notification.service.EmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

/**
 * Configuration cho Email Service
 */
@Configuration
public class EmailConfig {

    @Bean
    public EmailService emailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        return new EmailService(mailSender, templateEngine);
    }
}