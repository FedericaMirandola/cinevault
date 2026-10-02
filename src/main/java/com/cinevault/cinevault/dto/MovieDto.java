package com.cinevault.cinevault.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDto {
    
    private Long id;

    private String title;

    private String overview;

    @JsonProperty("vote_average")
    private Double voteAverage;
}
