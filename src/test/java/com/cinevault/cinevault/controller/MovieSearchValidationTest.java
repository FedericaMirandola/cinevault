
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

import com.cinevault.cinevault.dto.MovieDetailsDto;
import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.dto.MovieResponseDto;
import com.cinevault.cinevault.handler.GlobalExceptionHandler;
import com.cinevault.cinevault.service.MovieService;


import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(MovieController.class)
@Import(GlobalExceptionHandler.class)
class MovieSearchValidationTest {

    @Autowired // crea già il MockMvc e nn serve più il metodo .standaloneSetup()
    private MockMvc mockMvc;

    @MockitoBean // annotazione moderna per sostituire un bean con un mock
    private MovieService movieService;

    @Test
    void searchMovies_shouldRejectBlankQuery() throws Exception {

        mockMvc.perform(get("/api/movies/search")
                .param("query", "   ")
                .param("page", "1"))
                .andExpect(status().isBadRequest());// verifica che l'API risponda con HTTP 400

        verify(movieService, never()).searchMovies("   ", 1);// verifica che la richiesta non valida venga bloccata
                                                             // prima di arrivare al service.
    }

    @Test
    void searchMovies_shouldReturn200Ok_whenQueryContainsValidString() throws Exception {

        MovieDetailsDto dto1 = new MovieDetailsDto();
        dto1.setId(123L);
        dto1.setTitle("Titanic");
        dto1.setOverview("This is the dto1 overview");
        dto1.setVoteAverage(2.0);

        MovieResponseDto response = new MovieResponseDto();
        response.setPage(1);
        response.setResults(List.of(dto1));

        when(movieService.searchMovies("Titanic", 1)).thenReturn(response);
        mockMvc.perform(get("/api/movies/search")
                .param("query", "Titanic")
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.results[0].title").value("Titanic"));

        verify(movieService).searchMovies("Titanic", 1);
    }

    @Test
    void searchMovies_shouldRejectEmptyQuery() throws Exception {

        mockMvc.perform(get("/api/movies/search")
                .param("query", "")
                .param("page", "1"))
                .andExpect(status().isBadRequest());// verifica che l'API risponda con HTTP 400

        verify(movieService, never()).searchMovies("", 1);// verifica che la richiesta non valida venga bloccata prima
                                                          // di
                                                          // arrivare al service.
    }

    @Test
    void searchMovies_shouldReturnEmptyList_whenNoResultsFound() throws Exception {

        MovieResponseDto response = new MovieResponseDto();
        response.setPage(1);
        response.setResults(List.of());

        when(movieService.searchMovies("FilmTest", 1)).thenReturn(response);
        mockMvc.perform(get("/api/movies/search")
                .param("query", "FilmTest")
                .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(0)));

        verify(movieService).searchMovies("FilmTest", 1);

    }

    @Test 
    void searchMovies_shouldRejectPageZero() throws Exception {

        mockMvc.perform(get("/api/movies/search")
                .param("query", "Titanic")
                .param("page", "0"))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).searchMovies("Titanic", 0);

    }

    @Test
    void searchMovies_shouldPassPageToService() throws Exception {

        MovieDto dto = new MovieDto();
        dto.setTitle("Titanic");
        dto.setId(456L);
        dto.setOverview("overview");
        dto.setVoteAverage(3.2);

        MovieResponseDto response = new MovieResponseDto();
        response.setPage(2);
        response.setResults(List.of(dto));

        when(movieService.searchMovies("Titanic", 2)).thenReturn(response);

        mockMvc.perform(get("/api/movies/search")
                .param("query", "Titanic")
                .param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.results[0].title").value("Titanic"));
        
        verify(movieService).searchMovies("Titanic", 2);

    }
}
