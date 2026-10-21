package com.cinevault.cinevault.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.cinevault.cinevault.exception.EmailAlreadyRegisteredException;
import com.cinevault.cinevault.exception.FavoriteMovieNotFoundException;
import com.cinevault.cinevault.exception.TmdbException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TmdbException.class) //Questo metodo deve essere eseguito quando da un Controller viene propagata una TmdbException
    public ResponseEntity<String> handleTmdbException(TmdbException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ex.getMessage() + " - HTTP " + ex.getStatus().value()); //recupera il messaggio che abbiamo passato quando abbiamo creato l'eccezione:
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FavoriteMovieNotFoundException.class)
    public String movieNotFound (FavoriteMovieNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(ConstraintViolationException.class) 
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage()); 
    }
    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException except) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Dati di registrazione non validi");
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<String> handleEmailAlreadyRegisteredException(EmailAlreadyRegisteredException exc) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(exc.getMessage());
    }

}