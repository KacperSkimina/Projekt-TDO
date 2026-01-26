package com.example.ratingsystem.service;

import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RatingServiceTest {

    @Autowired
    private RatingService ratingService;

    @Test
    void shouldReturnZeroForMovieWithNoReviews() {
        Movie movie = new Movie();
        movie.setTitle("Movie without reviews");
        movie.setReviews(new ArrayList<>());

        double average = ratingService.calculateAverage(movie);

        assertEquals(0.0, average);
    }

    @Test
    void shouldReturnZeroForMovieWithNullReviews() {
        Movie movie = new Movie();
        movie.setTitle("Movie with null reviews");
        movie.setReviews(null);

        double average = ratingService.calculateAverage(movie);

        assertEquals(0.0, average);
    }

    @Test
    void shouldCalculateAverageForSingleReview() {
        Movie movie = new Movie();
        movie.setTitle("Movie with one review");

        Review review = new Review();
        review.setRating(8);
        movie.setReviews(Arrays.asList(review));

        double average = ratingService.calculateAverage(movie);

        assertEquals(8.0, average);
    }

    @Test
    void shouldCalculateAverageForMultipleReviews() {
        Movie movie = new Movie();
        movie.setTitle("Movie with multiple reviews");

        Review review1 = new Review();
        review1.setRating(7);

        Review review2 = new Review();
        review2.setRating(9);

        Review review3 = new Review();
        review3.setRating(8);

        movie.setReviews(Arrays.asList(review1, review2, review3));

        double average = ratingService.calculateAverage(movie);

        assertEquals(8.0, average);
    }

    @Test
    void shouldRoundAverageToOneDecimalPlace() {
        Movie movie = new Movie();
        movie.setTitle("Movie with reviews needing rounding");

        Review review1 = new Review();
        review1.setRating(7);

        Review review2 = new Review();
        review2.setRating(8);

        Review review3 = new Review();
        review3.setRating(8);

        movie.setReviews(Arrays.asList(review1, review2, review3));

        double average = ratingService.calculateAverage(movie);

        // (7 + 8 + 8) / 3 = 7.666... should round to 7.7
        assertEquals(7.7, average);
    }

    @Test
    void shouldHandleAllSameRatings() {
        Movie movie = new Movie();
        movie.setTitle("Movie with same ratings");

        Review review1 = new Review();
        review1.setRating(10);

        Review review2 = new Review();
        review2.setRating(10);

        Review review3 = new Review();
        review3.setRating(10);

        movie.setReviews(Arrays.asList(review1, review2, review3));

        double average = ratingService.calculateAverage(movie);

        assertEquals(10.0, average);
    }

    @Test
    void shouldHandleMinimumRatings() {
        Movie movie = new Movie();
        movie.setTitle("Movie with minimum ratings");

        Review review1 = new Review();
        review1.setRating(1);

        Review review2 = new Review();
        review2.setRating(1);

        movie.setReviews(Arrays.asList(review1, review2));

        double average = ratingService.calculateAverage(movie);

        assertEquals(1.0, average);
    }

    @Test
    void shouldHandleMixedRatings() {
        Movie movie = new Movie();
        movie.setTitle("Movie with mixed ratings");

        Review review1 = new Review();
        review1.setRating(1);

        Review review2 = new Review();
        review2.setRating(10);

        movie.setReviews(Arrays.asList(review1, review2));

        double average = ratingService.calculateAverage(movie);

        assertEquals(5.5, average);
    }
}