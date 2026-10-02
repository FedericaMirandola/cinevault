package com.cinevault.cinevault.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cinevault.cinevault.handler.GlobalExceptionHandler;

public class GlobalExceptionHandlerTest {

    @Test
    void handleTmdbException_shouldReturnBadGateway() {

        TmdbException exception = new TmdbException(
                "Errore di risposta da TMDB",
                HttpStatus.NOT_FOUND

        );

        // response contiene la risposta che il nostro handler costruisce.
        ResponseEntity<String> response = new GlobalExceptionHandler().handleTmdbException(exception);

        // Verifichiamo lo status HTTP
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());

        // Verifichiamo il body
        assertEquals(
                "Errore di risposta da TMDB - HTTP 404",
                response.getBody());
    }
}
