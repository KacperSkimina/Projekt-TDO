package com.example.ratingsystem.service;

import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.entity.Review;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    public double calculateAverage(Movie movie) {
        if (movie.getReviews() == null || movie.getReviews().isEmpty()) {
            return 0.0;
        }

        double avg = movie.getReviews().stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);

        return Math.round(avg * 10.0) / 10.0;
    }
}
