package com.cinevault.cinevault.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    
    public EmailAlreadyRegisteredException (String message) {
        super(message);
    }
}
