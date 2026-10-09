package com.cinevault.cinevault.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cinevault.cinevault.client.TmdbClient;
import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.entity.Movie;
import com.cinevault.cinevault.repository.MovieRepository;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private TmdbClient tmdbClient;

    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    @Test
    void getPopularMovies_shouldReturnMovies() {

        MovieDto movie1 = new MovieDto();
        movie1.setId(1L);
        movie1.setTitle("primo");

        MovieDto movie2 = new MovieDto();
        movie2.setId(2L);
        movie2.setTitle("secondo");

        when(tmdbClient.getPopularMovies()).thenReturn(List.of(movie1, movie2));

        movieService = new MovieService(tmdbClient, movieRepository);

        List<MovieDto> result = movieService.getPopularMovies();

        assertEquals(2, result.size());
        assertEquals("primo", result.get(0).getTitle());
        assertEquals("secondo", result.get(1).getTitle());
        verify(tmdbClient).getPopularMovies();

    }

    @Test
    void saveMovie_shouldSaveNewMovie() {

        // creo un dto e imposto dei valori
        MovieDto dto = new MovieDto();
        dto.setId(123L);
        dto.setTitle("Film di prova");
        dto.setOverview("Descrizione del film");
        dto.setVoteAverage(8.5);

        // chiamo il metodo findByTmdbId del repository passando l'id di tmdb e
        // restituico un optional vuoto
        // ossia simulo che quell'id nn sia già presente nel db
        when(movieRepository.findByTmdbId(123L)).thenReturn(Optional.empty());

        movieService = new MovieService(tmdbClient, movieRepository);
        // chiamo il metodo saveMovie del service passandogli il dto simulato
        Movie result = movieService.saveMovie(dto);

        // verifico che il risultato sia qllo atteso, nel titolo, nell'id
        assertEquals("Film di prova", result.getTitle());
        assertEquals(123L, result.getTmdbId());

        // verifico che il metodo save() venga corettamente chiamato
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void saveMovie_shouldNotSaveNewMovie_whenTmdbIdAlreadyExist() {

        // creo un dto e imposto dei valori
        MovieDto dto = new MovieDto();
        dto.setId(456L);
        dto.setTitle("Film di prova 2");
        dto.setOverview("Descrizione del film");
        dto.setVoteAverage(2.0);

        // creo un Movie che simula un film già presente nel database
        Movie existingMovie = new Movie();
        existingMovie.setTmdbId(456L);
        existingMovie.setTitle("Film già presente");

        when(movieRepository.findByTmdbId(456L)).thenReturn(Optional.of(existingMovie));

        movieService = new MovieService(tmdbClient, movieRepository);

        Movie result = movieService.saveMovie(dto);

        assertEquals("Film già presente", result.getTitle());
        assertEquals(456L, result.getTmdbId());

        verify(movieRepository, never()).save(any(Movie.class));

    }

    @Test
    void getFavoriteMovies_shouldReturnFavoriteMovies() {

        // creo due Movie che simulano i film presenti nel database
        Movie movie1 = new Movie();
        movie1.setTmdbId(123L);
        movie1.setTitle("Primo film");
        movie1.setOverview("Prima descrizione");
        movie1.setVoteAverage(8.5);

        Movie movie2 = new Movie();
        movie2.setTmdbId(456L);
        movie2.setTitle("Secondo film");
        movie2.setOverview("Seconda descrizione");
        movie2.setVoteAverage(7.5);

        // simulo che il repository restituisca questi due film
        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));

        movieService = new MovieService(tmdbClient, movieRepository);

        // chiamo il metodo che stiamo testando
        List<MovieDto> result = movieService.getFavoriteMovies();

        // verifico che siano stati restituiti due film
        assertEquals(2, result.size());

        // verifico il primo film
        assertEquals(123L, result.get(0).getId());
        assertEquals("Primo film", result.get(0).getTitle());
        assertEquals("Prima descrizione", result.get(0).getOverview());
        assertEquals(8.5, result.get(0).getVoteAverage());

        // verifico il secondo film
        assertEquals(456L, result.get(1).getId());
        assertEquals("Secondo film", result.get(1).getTitle());
        assertEquals("Seconda descrizione", result.get(1).getOverview());
        assertEquals(7.5, result.get(1).getVoteAverage());

        // verifico che il repository sia stato interrogato
        verify(movieRepository).findAll();
    }

    @Test 
    void deleteFavoriteByTmdbId_shouldDeleteFavoriteMovieByTmdbId() {

        when(movieRepository.deleteByTmdbId(456L)).thenReturn(1);

        movieService = new MovieService(tmdbClient, movieRepository);

        int result = movieService.deleteFavoriteByTmdbId(456L);

        assertEquals(1, result);

        verify(movieRepository).deleteByTmdbId(456L);

    }
}
