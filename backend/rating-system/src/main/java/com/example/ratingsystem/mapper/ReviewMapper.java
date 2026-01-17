package com.example.ratingsystem.mapper;

import com.example.ratingsystem.dto.ReviewDTO;
import com.example.ratingsystem.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDTO toDto(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        dto.setMovieId(
            review.getMovie() != null ? review.getMovie().getId() : null
        );

        dto.setUserId(
            review.getUser() != null ? review.getUser().getId() : null
        );

        return dto;
    }
}
