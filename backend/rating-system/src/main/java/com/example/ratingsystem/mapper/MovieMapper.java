package com.example.ratingsystem.mapper;

import com.example.ratingsystem.dto.MovieDTO;
import com.example.ratingsystem.dto.ReviewDTO;
import com.example.ratingsystem.entity.Movie;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Component
public class MovieMapper {
    public MovieDTO toDto(Movie movie, double avgRating) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDirector(movie.getDirector());
        dto.setDescription(movie.getDescription());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setAverageRating(avgRating);

        if (movie.getReviews() != null) {
            dto.setReviews(movie.getReviews().stream().map(rev -> {
                ReviewDTO rDto = new ReviewDTO();
                rDto.setId(rev.getId());
                rDto.setRating(rev.getRating());
                rDto.setComment(rev.getComment());
                if (rev.getUser() != null) {
                    rDto.setUsername(rev.getUser().getUsername());
                } else {
                    rDto.setUsername("Anonim");
                }
                return rDto;
            }).collect(Collectors.toList()));
        } else {
            dto.setReviews(new ArrayList<>());
        }
        // ----------------------------------------------

        return dto;
    }
}