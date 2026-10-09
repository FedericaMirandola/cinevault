package com.cinevault.cinevault.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.service.MovieService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
public class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @Test
    void getFavoriteMovies_shouldReturnFavoriteMovies() {

        MovieDto dto1 = new MovieDto();
        dto1.setId(123L);
        dto1.setTitle("Something");
        dto1.setOverview("This is the dto1 overview");
        dto1.setVoteAverage(2.0);

        MovieDto dto2 = new MovieDto();
        dto2.setId(456L);
        dto2.setTitle("Something2");
        dto2.setOverview("This is the dto2 overview");
        dto2.setVoteAverage(4.0);

        when(movieService.getFavoriteMovies()).thenReturn(List.of(dto1, dto2));

        MovieController movieController = new MovieController(movieService);
        List<MovieDto> result = movieController.getFavoriteMovies();

        assertEquals(2, result.size());

        assertEquals("Something", result.get(0).getTitle());
        assertEquals("Something2", result.get(1).getTitle());

        verify(movieService).getFavoriteMovies();

    }

    @Test
    void getFavoriteMovies_shouldReturnOkAndMovies() throws Exception {

        MovieDto dto1 = new MovieDto();
        dto1.setId(678L);
        dto1.setTitle("Titolo 1");
        dto1.setOverview("This is the titolo 1 overview");
        dto1.setVoteAverage(3.0);

        MovieDto dto2 = new MovieDto();
        dto2.setId(910L);
        dto2.setTitle("Titolo 2");
        dto2.setOverview("This is the titolo 2 overview");
        dto2.setVoteAverage(5.0);

        when(movieService.getFavoriteMovies()).thenReturn(List.of(dto1, dto2));

        MovieController movieController = new MovieController(movieService);

        // crea il nostro ambiente HTTP "finto"
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(movieController)
                .build();

        // simula esattamente: GET http://localhost:8080/api/movies/favorites
        // senza però avviare davvero il server.
        mockMvc.perform(get("/api/movies/favorites"))
                // controlla che il Controller risponda con HTTP 200
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Titolo 1"))
                .andExpect(jsonPath("$[1].title").value("Titolo 2"));
    }

    @Test
    void deleteFavoriteByTmdbId_shouldDeleteFavoriteMovie() throws Exception {

        when(movieService.deleteFavoriteByTmdbId(123L)).thenReturn(1);

        MovieController movieController = new MovieController(movieService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();

        mockMvc.perform(delete("/api/movies/favorites/{tmdbId}", 123L))
               .andExpect(status().isOk())
               .andExpect(content().string("1"));


        verify(movieService).deleteFavoriteByTmdbId(123L);
    }
}
