package com.todo.auth_service.exception;

import com.todo.auth_service.dto.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public AuthResponse handleUserAlreadyExists(UserAlreadyExistsException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public AuthResponse handleInvalidCredentials(InvalidCredentialsException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(InvalidOtpException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AuthResponse handleInvalidOtp(InvalidOtpException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(OtpExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    @ResponseBody
    public AuthResponse handleOtpExpiredResponse(OtpExpiredException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(ResetTokenExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    @ResponseBody
    public AuthResponse handleResetTokenExpiredResponse(ResetTokenExpiredException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public AuthResponse handleUserNotFound(UserNotFoundException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public AuthResponse handleUserNotVerified(UserNotVerifiedException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(EmailSendingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AuthResponse handleEmailUnsent(EmailSendingException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(OtpBlockedException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ResponseBody
    public AuthResponse handleOtpBlocked(OtpBlockedException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(OtpCooldownException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ResponseBody
    public AuthResponse handleOtpCooldown(OtpCooldownException e) {
        return AuthResponse.builder()
                .status("Failed")
                .msg(e.getMessage())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public AuthResponse handleUnauthorized(RuntimeException e) {
        if ("Unauthorized".equals(e.getMessage())) {
            return AuthResponse.builder()
                    .status("Failed")
                    .msg("Unauthorized - Invalid or missing token")
                    .build();
        }
        // Fall through to generic exception handler for other RuntimeExceptions
        return handleGenericException(e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public AuthResponse handleGenericException(Exception e) {
        System.out.println("EXCEPTION CAUGHT: " + e.getClass().getName() + " - " + e.getMessage());
        return AuthResponse.builder()
                .status("Failed")
                .msg("Something went wrong. Please try again.")
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AuthResponse handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid input");

        return AuthResponse.builder()
                .status("Failed")
                .msg(message)
                .build();
    }
}