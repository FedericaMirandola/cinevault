package com.cinevault.cinevault.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.entity.Movie;
import com.cinevault.cinevault.service.MovieService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController // per dire al framework che qsta classe espone delle API REST
@RequestMapping("/api/movies") // per definire il prefisso
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/popular") // crea GET /api/movies/popular
    public List<MovieDto> getPopularMovies() {
        return movieService.getPopularMovies();
    }

    @GetMapping("/search") // crea GET /api/movies/search?query = stringa da cercare
    public List<MovieDto> searchMovies(@RequestParam String query) {
        return movieService.searchMovies(query);
    }

    @GetMapping("/{id}")
    public MovieDto getMovieDetails(@PathVariable(value = "id") int movieId) {
        return movieService.getMovieDetails(movieId);
    }

    @PostMapping("/favorites")
    public Movie saveMovie(@RequestBody MovieDto dto) {
        return movieService.saveMovie(dto);
    }
    
    @GetMapping("/favorites")
    public List<MovieDto> getFavoriteMovies() {
        return movieService.getFavoriteMovies();
    }
    
    @DeleteMapping("/favorites/{tmdbId}")
    public int deleteFavoriteByTmdbId(@PathVariable(value = "tmdbId") Long tmdbId) {
        return movieService.deleteFavoriteByTmdbId(tmdbId);
    } 
}
