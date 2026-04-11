package com.todo.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailRequest {
    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email cannot be blank")
    private String email;
}
