package com.todo.auth_service.controller;

// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todo.auth_service.dto.request.EmailRequest;
import com.todo.auth_service.dto.request.LoginRequest;
import com.todo.auth_service.dto.request.PasswordRequest;
import com.todo.auth_service.dto.request.ResetPasswordRequest;
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

        return authService.getUserByEmail(email);
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody EmailRequest req) {

        String userEmail = req.getEmail();
        return authService.initiateRegistration(userEmail);
    }

    @PostMapping("/verifyOtp")
    public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest req) {

        String email = req.getEmail();
        String otp = req.getOtp();

        return authService.verifyOtp(email, otp);
    }

    @PostMapping("/setPassword")
    public AuthResponse setPassword(@Valid @RequestBody PasswordRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        return authService.setPassword(email, password);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {

        String email = req.getEmail();
        String password = req.getPassword();

        authService.userLogin(email, password);

        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);

        System.out.println(accessToken);
        System.out.println(refreshToken);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // true in prod
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24); // 1 day

        response.addCookie(refreshCookie);

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);  // true in production
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60);

        response.addCookie(accessCookie);

        return AuthResponse.builder()
                .email(email)
                .status("Success")
                .msg("Login successful")
                .build();
    }

    @PostMapping("/forgot-password")
    public AuthResponse forgotPassword (@RequestBody EmailRequest req) {
        System.out.println("================================= request "+req);

        return authService.forgotPassword(req.getEmail());
        // return AuthResponse.builder()
        //     .status("Success")
        //     .msg("Endpoint hit")
        //     .build();
    }

    @GetMapping("/reset-info")
    public AuthResponse resetInfo(@RequestParam String token) {
        return authService.resetInfo(token);
    }
    @PostMapping("/reset-password")
    public AuthResponse resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest request) {

        return authService.resetPassword(token, request.getNewPassword());
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {

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

        Cookie accessCookie = new Cookie("accessToken", newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60);

        response.addCookie(accessCookie);

        return AuthResponse.builder()
                .email(email)
                .build();
    }
}