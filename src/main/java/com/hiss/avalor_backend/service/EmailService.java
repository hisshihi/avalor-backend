package com.hiss.avalor_backend.service;

public interface EmailService {

    void sendVerificationEmail(String username, String verificationLink);

}
