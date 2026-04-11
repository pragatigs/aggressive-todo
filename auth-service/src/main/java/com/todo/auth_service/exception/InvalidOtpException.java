package com.todo.auth_service.exception;

public class InvalidOtpException extends RuntimeException {
    
    public InvalidOtpException (String msg){
        super(msg);
    }
}
