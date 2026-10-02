package com.cinevault.cinevault.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cinevault.cinevault.client.TmdbClient;
import com.cinevault.cinevault.dto.MovieDto;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private TmdbClient tmdbClient;

    private MovieService movieService;
    
    @Test
    void getPopularMovies_shouldReturnMovies() {

        MovieDto movie1 = new MovieDto();
        movie1.setId(1L);
        movie1.setTitle("primo");

        MovieDto movie2 =  new MovieDto();
        movie2.setId(2L);
        movie2.setTitle("secondo");

        when(tmdbClient.getPopularMovies()).thenReturn(List.of(movie1, movie2));

        movieService = new MovieService(tmdbClient);
        
        List<MovieDto> result = movieService.getPopularMovies();

        assertEquals(2, result.size());
        assertEquals("primo", result.get(0).getTitle());
        assertEquals("secondo", result.get(1).getTitle());
        verify(tmdbClient).getPopularMovies();

    }
}
