package com.example.ratingsystem.mapper;

import com.example.ratingsystem.dto.MovieDTO;
import com.example.ratingsystem.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {
    public MovieDTO toDto(Movie movie, double avgRating) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setAverageRating(avgRating);

        return dto;
    }
}
