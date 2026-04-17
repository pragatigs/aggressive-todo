package com.todo.auth_service.exception;

public class OtpCooldownException extends RuntimeException{

    public OtpCooldownException (String msg){
        super(msg);
    }
    
}
