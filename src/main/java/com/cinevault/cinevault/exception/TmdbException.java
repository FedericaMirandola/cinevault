package com.cinevault.cinevault.exception;

import org.springframework.http.HttpStatusCode;

public class TmdbException extends RuntimeException {

    private final HttpStatusCode status;
    
    public TmdbException (String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

}
