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
import com.todo.auth_service.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping("/me")
    public AuthResponse getUserInfo(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Unauthorized");
        }
        String email = authentication.getName();

        return authService.getUserByEmail(email).build();
    }

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

        authService.userLogin(email, password);

        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // true in prod
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24); // 1 day

        response.addCookie(refreshCookie);

        return AuthResponse.builder()
                .email(email)
                .status("Success")
                .msg("Login successful")
                .accessToken(accessToken)
                .build();
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(HttpServletRequest request) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token missing");
        }
        String email = jwtService.extractEmail(refreshToken);

        if (!jwtService.isTokenValid(refreshToken, email)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String newAccessToken = jwtService.generateAccessToken(email);
        return AuthResponse.builder()
                .email(email)
                .accessToken(newAccessToken)
                .build();
    }
}