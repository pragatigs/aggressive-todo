package com.todo.auth_service.service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.todo.auth_service.dto.response.AuthResponse;
import com.todo.auth_service.dto.response.AuthResponse.AuthResponseBuilder;
import com.todo.auth_service.entity.UserEntity;
import com.todo.auth_service.exception.InvalidCredentialsException;
import com.todo.auth_service.exception.UserAlreadyExistsException;
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

    public AuthResponseBuilder initiateRegistration (String email){
        if (userRepository.existsById(email)){
            throw new UserAlreadyExistsException(email + " already exists! Please login");
        }
        else{
            String generatedOtp = otpService.generateAndStoreOtp(email);

            emailService.sendEmail(email, generatedOtp);

            return AuthResponse.builder().email(email)
            .status("Success").msg("OTP generated for email successfully: "+email)
            .token(null);
        }
    }

    public AuthResponseBuilder verifyOtp (String email, String otp){
        otpService.validateAndDeleteOtp(email, otp);
        stringRedisTemplate.opsForValue().set("verified:" + email, "true", 10, TimeUnit.MINUTES);
        return AuthResponse.builder().email(email)
            .status("Success").msg("Email verified successfully: "+email)
            .token(null);
    }

    public AuthResponseBuilder setPassword(String email, String password){

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
            .status("Success").msg("Email password set successfully: "+email)
            .token(null);

    }

    public AuthResponseBuilder userLogin(String email, String password){
        UserEntity user = userRepository.findById(email)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password!"));
        
        if (!user.isEmailVerified()) {
            throw new UserNotVerifiedException("Email not verified. Please verify your email first");
        }

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password!");
        }
        String jwtToken = jwtService.generateToken(email);
        return AuthResponse.builder().email(email)
        .status("Success").msg("Login successful: "+email)
        .token(jwtToken);

    }
    
}
