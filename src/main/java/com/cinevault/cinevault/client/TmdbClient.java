package com.cinevault.cinevault.client;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.dto.MovieResponseDto;
import com.cinevault.cinevault.exception.TmdbException;

import reactor.core.publisher.Mono;

@Component
public class TmdbClient {

    private final WebClient webClient;

    public TmdbClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<MovieDto> getPopularMovies() {

        MovieResponseDto response = webClient
                .get()
                .uri("/movie/popular")
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> {
                            HttpStatusCode status = clientResponse.statusCode();
                            return Mono.error(new TmdbException("errore di risposta da TMDB", status));
                        })
                .bodyToMono(MovieResponseDto.class)
                .block();
        
        // prima controlli che response.getResults() esista e solo dopo lo restituisci
        if (response == null || response.getResults() == null) {
            throw new RuntimeException("Nessun film trovato");
        }

        return response.getResults();
    }

    public List<MovieDto> searchMovies(String query) {

        MovieResponseDto response = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/search/movie")
                .queryParam("query", query)
                .build())
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> {
                            HttpStatusCode status = clientResponse.statusCode();
                            return Mono.error(new TmdbException("errore di risposta da TMDB", status));
                        })
                .bodyToMono(MovieResponseDto.class)
                .block();

        if (response == null || response.getResults() == null) {
            throw new RuntimeException("Nessun film trovato");
        }

        return response.getResults();
        
    }

    public MovieDto getMovieDetails (int movieId) {

        MovieDto response = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/movie/{id}")
                .build(movieId))
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> {
                            HttpStatusCode status = clientResponse.statusCode();
                            return Mono.error(new TmdbException("errore di risposta da TMDB", status));
                        })
                .bodyToMono(MovieDto.class)
                .block();

          if (response == null) {
            throw new RuntimeException("Nessun dettaglio trovato");
        }
        
        return response;
    }

}
