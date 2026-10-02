package com.cinevault.cinevault.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cinevault.cinevault.exception.TmdbException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TmdbException.class) //Questo metodo deve essere eseguito quando da un Controller viene propagata una TmdbException
    public ResponseEntity<String> handleTmdbException(TmdbException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ex.getMessage() + " - HTTP " + ex.getStatus().value()); //recupera il messaggio che abbiamo passato quando abbiamo creato l'eccezione:
    }
}
