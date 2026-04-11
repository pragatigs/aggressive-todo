package com.todo.auth_service.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${spring.application.jwt.secret}")
    private String secret;

    @Value("${spring.application.jwt.expiration}")
    private long expiration;
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)              // who is user
                .setIssuedAt(new Date())        // current time
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 1 hour
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }
    public boolean isTokenValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}