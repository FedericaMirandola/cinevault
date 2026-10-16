package com.cinevault.cinevault.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class MovieDetailsDto extends MovieDto {

    @JsonProperty("release_date")
    private String releaseDate;

    private Integer runtime;

    private List<GenreDto> genres;
    
}
