package com.todo.auth_service.dto.response;

import java.time.LocalDateTime;

// import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String email;
    private String status;
    private String msg;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accountCreated;
}
