package com.hiss.avalor_backend.controller;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final JavaMailSender mailSender;

    @SneakyThrows
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    @GetMapping
    public ResponseEntity<?> sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        helper.setFrom("avalog.auth@gmail.com");

        mailSender.send(mimeMessage);
        return ResponseEntity.ok().build();
    }

}
