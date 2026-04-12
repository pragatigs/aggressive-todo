package com.todo.auth_service.exception;

public class EmailSendingException extends RuntimeException {

    public EmailSendingException(String msg){
        super(msg);
    }
}
