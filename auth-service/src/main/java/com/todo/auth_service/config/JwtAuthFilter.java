package com.todo.auth_service.config;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import com.todo.auth_service.service.JwtService;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException{

        jakarta.servlet.http.Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        for (jakarta.servlet.http.Cookie cookie : cookies) {
            if (cookie.getName().equals("jwt")) {
                token = cookie.getValue();
                break;
            }
        }
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = jwtService.extractEmail(token);

            if (email != null && jwtService.isTokenValid(token, email)) {
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of()
                    );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // invalid token — do nothing, Spring handles 401
        }

        filterChain.doFilter(request, response);
    }
}