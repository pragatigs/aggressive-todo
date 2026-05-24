package com.todo.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Password cannot be empty")
    @Size (min = 8, max = 20, message = "Password should be of minimum 8 chars")
    @Pattern (regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z0-9@$!%*?&]{8,}$", message = "Password must contain letters, numbers, and special characters")
    private String newPassword;
}
