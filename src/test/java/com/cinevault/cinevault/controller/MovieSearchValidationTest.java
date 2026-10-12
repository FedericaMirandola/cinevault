
package com.cinevault.cinevault.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;


import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.handler.GlobalExceptionHandler;
import com.cinevault.cinevault.service.MovieService;

@WebMvcTest(MovieController.class)
@Import(GlobalExceptionHandler.class)
class MovieSearchValidationTest {

    @Autowired //crea già il MockMvc e nn serve più il metodo .standaloneSetup()
    private MockMvc mockMvc;

    @MockitoBean //annotazione moderna per sostituire un bean con un mock
    private MovieService movieService;

    @Test
    void searchMovies_shouldRejectBlankQuery() throws Exception {

        mockMvc.perform(get("/api/movies/search")
                .param("query", "   "))
                .andExpect(status().isBadRequest());//verifica che l'API risponda con HTTP 400

        verify(movieService, never()).searchMovies("   ");//verifica che la richiesta non valida venga bloccata prima di arrivare al service.
    }

    @Test
    void searchMovies_shouldReturn200Ok_whenQueryContainsValidString() throws Exception {

        MovieDto dto1 = new MovieDto();
        dto1.setId(123L);
        dto1.setTitle("Titanic");
        dto1.setOverview("This is the dto1 overview");
        dto1.setVoteAverage(2.0);

        when(movieService.searchMovies("Titanic")).thenReturn(List.of(dto1));
        mockMvc.perform(get("/api/movies/search")
                .param("query", "Titanic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Titanic"));

        verify(movieService).searchMovies("Titanic");
    }

    @Test
    void searchMovies_shouldRejectEmptyQuery() throws Exception {

        mockMvc.perform(get("/api/movies/search")
                .param("query", ""))
                .andExpect(status().isBadRequest());//verifica che l'API risponda con HTTP 400

        verify(movieService, never()).searchMovies("");//verifica che la richiesta non valida venga bloccata prima di arrivare al service.
    }
}
