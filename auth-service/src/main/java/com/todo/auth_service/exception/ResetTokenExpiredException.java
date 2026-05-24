package com.todo.auth_service.exception;

public class ResetTokenExpiredException extends RuntimeException {

    public ResetTokenExpiredException (String msg){
        super(msg);
    }
    
}
