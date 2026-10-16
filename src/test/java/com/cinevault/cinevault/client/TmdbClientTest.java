package com.cinevault.cinevault.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.cinevault.cinevault.dto.MovieDetailsDto;
import com.cinevault.cinevault.dto.MovieDto;
import com.cinevault.cinevault.dto.MovieResponseDto;
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

        MovieResponseDto result = tmdbClient.searchMovies("Interstellar", 1);

        assertEquals("Interstellar", result.getResults().get(0).getTitle());
        assertEquals(1, result.getPage());
        assertEquals(2, result.getResults().size());

        // voglio verificare: Il mio codice ha costruito correttamente la richiesta che
        // volevo mandare?
        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals(
                "/search/movie?query=Interstellar&page=1",
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
                                    "vote_average": 8.5,
                                    "release_date": "2014-11-24",
                                    "runtime": 169,
                                    "genres": [
                                        {
                                        "id": 12,
                                        "name": "Sci-fi"
                                        }
                                    ]
                                }
                                """));

        MovieDetailsDto result = tmdbClient.getMovieDetails(157336);

        assertEquals(157336, result.getId());
        assertEquals("Interstellar", result.getTitle());
        assertEquals("Un film di fantascienza", result.getOverview());
        assertEquals(8.5, result.getVoteAverage());
        assertEquals("2014-11-24", result.getReleaseDate());
        assertEquals(169, result.getRuntime());
        assertEquals(12, result.getGenres().get(0).getId());
        assertEquals("Sci-fi", result.getGenres().get(0).getName());

        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals(
                "/movie/157336",
                request.getPath());
    }

    @Test
    void searchMovies_shouldReturnEmptyList_whenNoResultsFound() throws Exception {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody("""
                                {
                                    "page": 1,
                                    "results": [

                                    ]
                                    }
                                """));

        MovieResponseDto result = tmdbClient.searchMovies("FilmTest", 1);

        // assertEquals(0, result.size());
        assertTrue(result.getResults().isEmpty());

    }

    @Test
    void searchMovies_shouldPassPageToTmdb() throws Exception {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody("""
                                {
                                    "page": 2,
                                    "total_pages": 5,
                                    "total_results": 42,
                                    "results": [
                                        {
                                            "id": 1,
                                            "title": "Interstellar",
                                            "overview": "Un film di fantascienza",
                                            "vote_average": 8.6
                                        }
                                     ]
                                    }
                                """));

        MovieResponseDto result = tmdbClient.searchMovies("Interstellar", 2);

        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals(
                "/search/movie?query=Interstellar&page=2",
                request.getPath());
        assertEquals(2, result.getPage());
        assertEquals(5, result.getTotalPages());
        assertEquals(42, result.getTotalResults());

    }
}