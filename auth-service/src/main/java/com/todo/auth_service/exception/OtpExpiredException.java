package com.todo.auth_service.exception;

public class OtpExpiredException extends RuntimeException{

    public OtpExpiredException(String msg){
        super(msg);
    }

}
