package com.example.ratingsystem.service;

import com.example.ratingsystem.dto.CreateReviewRequestDTO;
import com.example.ratingsystem.dto.ReviewDTO;
import com.example.ratingsystem.dto.UpdateReviewRequestDTO;
import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.repository.MovieRepository;
import com.example.ratingsystem.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private EntityManager entityManager;


    private Movie testMovie;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        movieRepository.deleteAll();

        testMovie = new Movie();
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("A test movie");
        testMovie.setReleaseYear(2024);
        testMovie = movieRepository.save(testMovie);
    }

    @Test
    void shouldCreateReview() {
        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(8);
        request.setComment("Great movie!");
        request.setMovieId(testMovie.getId());

        ReviewDTO created = reviewService.createReview(request);

        assertNotNull(created.getId());
        assertEquals(8, created.getRating());
        assertEquals("Great movie!", created.getComment());
        assertEquals(testMovie.getId(), created.getMovieId());
    }

    @Test
    void shouldUpdateReview() {
        CreateReviewRequestDTO createRequest = new CreateReviewRequestDTO();
        createRequest.setRating(7);
        createRequest.setComment("Good");
        createRequest.setMovieId(testMovie.getId());
        ReviewDTO created = reviewService.createReview(createRequest);

        UpdateReviewRequestDTO updateRequest = new UpdateReviewRequestDTO();
        updateRequest.setRating(9);
        updateRequest.setComment("Excellent!");

        ReviewDTO updated = reviewService.updateReview(created.getId(), updateRequest);

        assertEquals(9, updated.getRating());
        assertEquals("Excellent!", updated.getComment());
    }

    @Test
    void shouldDeleteReview() {
        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(6);
        request.setMovieId(testMovie.getId());
        ReviewDTO created = reviewService.createReview(request);

        reviewService.deleteReview(created.getId());
        entityManager.flush();
        entityManager.clear();

        assertFalse(reviewRepository.existsById(created.getId()));
    }

    @Test
    void shouldThrowExceptionWhenReviewNotFound() {
        assertThrows(RuntimeException.class, () -> {
            reviewService.getReviewById(999L);
        });
    }
}
