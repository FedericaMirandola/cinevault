package com.cinevault.cinevault.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinevault.cinevault.client.TmdbClient;
import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.entity.Movie;
import com.cinevault.cinevault.repository.MovieRepository;

@Service
public class MovieService {

    private final TmdbClient tmdbClient;
    private final MovieRepository movieRepository;

    public MovieService(TmdbClient tmdbClient, MovieRepository movieRepository) {
        this.tmdbClient = tmdbClient;
        this.movieRepository = movieRepository;
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

    private Movie toEntity(MovieDto dto) {

        Movie movie = new Movie();

        movie.setTitle(dto.getTitle());
        movie.setTmdbId(dto.getId());
        movie.setOverview(dto.getOverview());
        movie.setVoteAverage(dto.getVoteAverage());

        return movie;
    }

    public Movie saveMovie(MovieDto dto) {

        // prima mettiamo in una variabile che deve essere Optional l'eventuale tmdbId
        // restituito dal getId() del dto
        Optional<Movie> newMovie = movieRepository.findByTmdbId(dto.getId());
        // controlliamo se quel record è presente nel DB. Dobbiamo usare .get() xè è
        // come se Optional fosse una scatola
        // da cui prendiamo il record se c'è
        if (newMovie.isPresent()) {
            return newMovie.get();
        } else {
            // se il record nn esiste allora lo creiamo convertendolo prima in entity poi
            // salvandolo
            Movie movie = toEntity(dto);
            movieRepository.save(movie);
            return movie;
        }

    }

    public List<MovieDto> getFavoriteMovies() {

        List<Movie> movies = movieRepository.findAll();

        return movies.stream()
                .map(movie -> {
                    MovieDto dto = new MovieDto();
                    dto.setId(movie.getTmdbId());
                    dto.setTitle(movie.getTitle());
                    dto.setOverview(movie.getOverview());
                    dto.setVoteAverage(movie.getVoteAverage());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public int deleteFavoriteByTmdbId (Long tmdbId) {

        int movieDeleted = movieRepository.deleteByTmdbId(tmdbId);

        return movieDeleted;
    }

}
