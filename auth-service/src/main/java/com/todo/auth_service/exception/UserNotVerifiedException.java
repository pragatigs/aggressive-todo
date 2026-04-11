package com.todo.auth_service.exception;

public class UserNotVerifiedException extends RuntimeException {
    
    public UserNotVerifiedException (String msg){
        super(msg);
    }
}
