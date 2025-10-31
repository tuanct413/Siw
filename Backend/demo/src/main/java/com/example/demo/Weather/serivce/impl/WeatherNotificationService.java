package com.example.demo.Weather.serivce.impl;

import com.example.demo.User.repository.UserRepository;
import com.example.demo.common.mail.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherNotificationService {

    private final UserRepository userRepository;
    private final MailService mailService;

    @Async // ⚡ gửi email song song, không chặn request chính
    public void sendWeatherAlertAsync(Long userId, String city, String subject, String content) {
        try {
            String email = userRepository.findEmailById(userId);
            mailService.sendWarningEmail(email, subject, content);
            System.out.println("📧 [ASYNC] Đã gửi email cảnh báo cho userId: " + userId);
        } catch (MessagingException e) {
            System.err.println("❌ [ASYNC] Lỗi khi gửi mail cảnh báo: " + e.getMessage());
        }
    }
}
