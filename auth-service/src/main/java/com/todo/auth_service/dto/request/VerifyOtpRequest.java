package com.todo.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyOtpRequest {
    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Otp cannot be blank")
    @Size(min = 6, message = "OTP is of 6 digits")
    private String otp;
}
