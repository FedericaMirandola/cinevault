package com.cinevault.cinevault.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cinevault.cinevault.client.TmdbClient;
import com.cinevault.cinevault.dto.MovieDto;

@Service
public class MovieService {

    private final TmdbClient tmdbClient;

    public MovieService(TmdbClient tmdbClient) {
        this.tmdbClient = tmdbClient;
    }

    public List<MovieDto> getPopularMovies() {

        List<MovieDto> movies = tmdbClient.getPopularMovies();

        return movies;
    }

    public List<MovieDto> searchMovies(String query) {

        List<MovieDto> movies = tmdbClient.searchMovies(query);

        return movies;

    }

    public MovieDto getMovieDetails(int movieId) {

        MovieDto details = tmdbClient.getMovieDetails(movieId);

        return details;

    }

}
