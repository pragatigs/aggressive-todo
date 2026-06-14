package com.todo.auth_service.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.todo.auth_service.dto.response.AuthResponse;
import com.todo.auth_service.entity.UserEntity;
import com.todo.auth_service.exception.InvalidCredentialsException;
import com.todo.auth_service.exception.ResetTokenExpiredException;
import com.todo.auth_service.exception.UserAlreadyExistsException;
import com.todo.auth_service.exception.UserNotFoundException;
import com.todo.auth_service.exception.UserNotVerifiedException;
import com.todo.auth_service.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final StringRedisTemplate stringRedisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;

    public AuthResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findById(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return AuthResponse.builder()
                .email(email)
                .status("Success")
                .msg("User found")
                .accountCreated(user.getAccountCreated())
                .build();
    }

    public AuthResponse initiateRegistration(String email) {
        if (userRepository.existsById(email)) {
            throw new UserAlreadyExistsException(email + " already exists! Please login");
        } else {
            String generatedOtp = otpService.generateAndStoreOtp(email);
            String subject = "Your TodoAuth verification code";
            emailService.sendEmail(email, generatedOtp, subject, EmailService.EmailType.OTP);

            return AuthResponse.builder().email(email)
                    .status("Success").msg("OTP generated for email successfully: " + email)
                    .accessToken(null).build();
        }
    }

    public AuthResponse verifyOtp(String email, String otp) {
        otpService.validateAndDeleteOtp(email, otp);
        stringRedisTemplate.opsForValue().set("verified:" + email, "true", 10, TimeUnit.MINUTES);
        return AuthResponse.builder().email(email)
                .status("Success").msg("Email verified successfully: " + email)
                .accessToken(null).build();
    }

    public AuthResponse setPassword(String email, String password) {

        String verified = stringRedisTemplate.opsForValue().get("verified:" + email);
        if (verified == null) {
            throw new UserNotVerifiedException("Email not verified. Please verify your email first");
        }
        String hashedPassword = passwordEncoder.encode(password);
        UserEntity userEntity = new UserEntity(email, hashedPassword);
        userEntity.setEmailVerified(true);
        userEntity.setLastActive(LocalDateTime.now());
        userRepository.save(userEntity);
        stringRedisTemplate.delete("verified:" + email);
        return AuthResponse.builder().email(email)
                .status("Success").msg("Email password set successfully: " + email)
                .accessToken(null).build();

    }

    public AuthResponse userLogin(String email, String password) {
        UserEntity user = userRepository.findById(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password!"));

        if (!user.isEmailVerified()) {
            throw new UserNotVerifiedException("Email not verified. Please verify your email first");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password!");
        }
        String jwtToken = jwtService.generateAccessToken(email);
        UserEntity userEntity = new UserEntity();
        userEntity.setLastActive(LocalDateTime.now());
        return AuthResponse.builder().email(email)
                .status("Success").msg("Login successful: " + email)
                .accessToken(jwtToken).build();

    }

    public AuthResponse forgotPassword (String email){
        Optional<UserEntity> user = userRepository.findById(email);
        System.out.println("============================== "+user);
        if (user.isEmpty()) {
            System.out.println("============================== user is empty");
            return AuthResponse.builder()
                    .status("Success")
                    .msg("If account exists, reset link sent").build();
        }

        UUID token = UUID.randomUUID();

        stringRedisTemplate.opsForValue().set("reset:" + token, email, 10, TimeUnit.MINUTES);

        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
        String subject = "Password Reset Link";

        emailService.sendEmail(email, resetUrl, subject, EmailService.EmailType.RESET_LINK);
        return AuthResponse.builder().email(email)
                .status("Success").msg("Reset Link sent successfully to " + email).build();
    }

    public AuthResponse resetInfo(String token){
        String email = stringRedisTemplate.opsForValue().get("reset:" + token);
        if (email == null) {
            throw new ResetTokenExpiredException("Reset link expired or already used");
        }
        return AuthResponse.builder()
            .email(email)
            .status("Success")
            .build();
    }

    public AuthResponse resetPassword (String token, String password){
        String email = stringRedisTemplate.opsForValue().get("reset:" + token);
        if (email == null){
            throw new ResetTokenExpiredException("Password Reset Link Expired. Please try again!");
        }

        UserEntity user = userRepository.findById(email)
        .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        stringRedisTemplate.delete("reset:"+token);
        return AuthResponse.builder()
                    .status("Success")
                    .msg("Password Reset successful!").build();

    }
}
