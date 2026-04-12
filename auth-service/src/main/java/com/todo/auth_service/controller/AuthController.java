package com.todo.auth_service.controller;

// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todo.auth_service.dto.request.EmailRequest;
import com.todo.auth_service.dto.request.LoginRequest;
import com.todo.auth_service.dto.request.PasswordRequest;
import com.todo.auth_service.dto.request.VerifyOtpRequest;
import com.todo.auth_service.dto.response.AuthResponse;
import com.todo.auth_service.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody EmailRequest req) {

        String userEmail = req.getEmail();
        return authService.initiateRegistration(userEmail).build();
    }

    @PostMapping("/verifyOtp")
    public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {
        
        String email = req.getEmail();
        String otp = req.getOtp();
        
        return authService.verifyOtp(email, otp).build();
    }

    @PostMapping("/setPassword")
    public AuthResponse setPassword(@Valid @RequestBody PasswordRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();
        
        return authService.setPassword(email, password).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {
        String email = req.getEmail();
        String password = req.getPassword();
        
        AuthResponse resp = authService.userLogin(email, password).build();
        Cookie cookie = new Cookie("jwt", resp.getToken());
        cookie.setHttpOnly(true);     
        cookie.setSecure(false);  // true in prod for https
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);    // 1 hour
        response.addCookie(cookie);
        
        return AuthResponse.builder()
        .email(email)
        .status("Success")
        .msg("Login successful")
        .token(null)
        .build();
    }
    
    
    
}
