package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async("asyncTaskExecutor")
    @Override
    public void sendVerificationEmail(String username, String verificationLink) {
        try {
            log.info("Начало отправки письма на {}", username);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(username);
            helper.setSubject("Подтверждение регистрации");
            helper.setText("Перейдите по ссылке для подтверждения вашей почты: " + verificationLink, true);
            helper.setFrom("avalog.auth@gmail.com");

            mailSender.send(mimeMessage);
            log.info("Письмо успешно отправлено на {}", username);
        } catch (Exception e) {
            log.error("Ошибка отправки письма на {}: {}", username, e.getMessage(), e);
        }
    }

}
