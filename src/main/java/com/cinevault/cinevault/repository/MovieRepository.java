package com.cinevault.cinevault.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinevault.cinevault.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long>{

    Optional<Movie> findByTmdbId(Long tmdbId);

    Integer deleteByTmdbId(Long tmdbId);
    
}
