package com.cinevault.cinevault.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieResponseDto {

    private Integer page;

    private List<MovieDto> results;

}
