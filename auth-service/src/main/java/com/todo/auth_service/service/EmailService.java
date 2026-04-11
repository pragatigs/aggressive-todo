package com.todo.auth_service.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // generates constructor for final fields only + @NonNull fields — excludes @Value fields 
public class EmailService {

    private final JavaMailSender javaMailSender; // only this is included in the constructor, because it is a final field
    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(String ToEmail, String otp ) {
        String subject = "Your OTP for TODO application";
        String body = "Please enter the below OTP to verify your email!!\n"+otp;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ToEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(from);

        javaMailSender.send(message);
}
}
