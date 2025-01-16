package com.hiss.avalor_backend.service.impl;

import com.hiss.avalor_backend.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @SneakyThrows
    @Override
    public void sendVerificationEmail(String username, String verificationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(username);
        helper.setSubject("Подтвеждение регистрации");
        helper.setText("Перейдите по ссылке для подтвеждения вашей почты: " + verificationLink, true);
        helper.setFrom("avalog.auth@gmail.com");

        mailSender.send(mimeMessage);
    }
}
