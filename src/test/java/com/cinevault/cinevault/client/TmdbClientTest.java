package com.cinevault.cinevault.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.exception.TmdbException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class TmdbClientTest {

    private MockWebServer mockWebServer;
    private TmdbClient tmdbClient;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Creo un server finto
        mockWebServer = new MockWebServer();
        // 2. Lo avvio
        mockWebServer.start();
        // 3. Scopro il suo indirizzo
        String baseUrl = mockWebServer.url("/").toString();
        // 4. Creo un WebClient che punta al server finto
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        // 5. Creo il vero TmdbClient usando quel WebClient
        tmdbClient = new TmdbClient(webClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    // happy path. vogliamo testare una risposta corretta
    @Test
    void getPopularMovies_shouldReturnMovies() throws Exception {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody("""
                                {
                                    "page": 1,
                                    "results": [
                                        {
                                            "id": 1,
                                            "title": "Film Uno",
                                            "overview": "Descrizione del film uno",
                                            "vote_average": 8.0
                                        },
                                        {
                                            "id": 2,
                                            "title": "Film Due",
                                            "overview": "Descrizione del film due",
                                            "vote_average": 7.5
                                        }
                                    ]
                                }
                                """));

        List<MovieDto> result = tmdbClient.getPopularMovies();

        RecordedRequest request = mockWebServer.takeRequest(); // serve per controllare la richieta HTTP inviata al
                                                               // mockWebServer

        assertEquals("/movie/popular", request.getPath()); // verifica il percorso che TmdbClient ha effettivamente
                                                           // richiesto

        assertEquals(2, result.size());
        assertEquals("Film Uno", result.get(0).getTitle());
        assertEquals("Film Due", result.get(1).getTitle());
        // per verificare il mapping di Jackson
        assertEquals(8.0, result.get(0).getVoteAverage());
    }

    // vogliamo testare una risposta di errore
    @Test
    void getPopularMovies_shouldThrowTmdbException_whenTmdbReturnsError() {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(404));

        TmdbException exception = assertThrows(
                TmdbException.class,
                () -> tmdbClient.getPopularMovies());

        assertEquals(404, exception.getStatus().value());
    }

    @Test
    void searchMovies_shouldReturnMovies() throws Exception {

        // se arriva questo JSON, il mio codice lo trasforma correttamente?
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody("""
                                {
                                    "page": 1,
                                    "results": [
                                        {
                                            "id": 1,
                                            "title": "Interstellar",
                                            "overview": "Un film di fantascienza",
                                            "vote_average": 8.6
                                        },
                                        {
                                            "id": 2,
                                            "title": "Interstellar 2",
                                            "overview": "Altro film",
                                            "vote_average": 7.5
                                        }
                                    ]
                                    }
                                """));

        List<MovieDto> result = tmdbClient.searchMovies("Interstellar");

        assertEquals(2, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
        assertEquals(8.6, result.get(0).getVoteAverage());

        // voglio verificare: Il mio codice ha costruito correttamente la richiesta che
        // volevo mandare?
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals(
                "/search/movie?query=Interstellar",
                request.getPath());

    }

    @Test
    void getMovieDetails_shouldReturnMovie() throws Exception {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody("""
                                {
                                    "id": 157336,
                                    "title": "Interstellar",
                                    "overview": "Un film di fantascienza",
                                    "vote_average": 8.5
                                }
                                """));
        
        MovieDto result = tmdbClient.getMovieDetails(157336);

       assertEquals(157336, result.getId());
       assertEquals("Interstellar", result.getTitle());
       assertEquals("Un film di fantascienza", result.getOverview());
       assertEquals(8.5, result.getVoteAverage());

       RecordedRequest request = mockWebServer.takeRequest();

        assertEquals(
                "/movie/157336",
                request.getPath());
    }
}
