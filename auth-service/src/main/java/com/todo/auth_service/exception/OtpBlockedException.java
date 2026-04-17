package com.todo.auth_service.exception;

public class OtpBlockedException extends RuntimeException {

    public OtpBlockedException (String msg){
        super(msg);
    }
    
}
