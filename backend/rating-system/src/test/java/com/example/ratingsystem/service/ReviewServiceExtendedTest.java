package com.example.ratingsystem.service;

import com.example.ratingsystem.dto.CreateReviewRequestDTO;
import com.example.ratingsystem.dto.ReviewDTO;
import com.example.ratingsystem.dto.UpdateReviewRequestDTO;
import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.entity.Role;
import com.example.ratingsystem.entity.User;
import com.example.ratingsystem.repository.MovieRepository;
import com.example.ratingsystem.repository.ReviewRepository;
import com.example.ratingsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewServiceExtendedTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Movie testMovie;
    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();
        SecurityContextHolder.clearContext();

        testMovie = new Movie();
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("A test movie");
        testMovie.setReleaseYear(2024);
        testMovie = movieRepository.save(testMovie);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole(Role.USER);
        testUser = userRepository.save(testUser);

        anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword(passwordEncoder.encode("password"));
        anotherUser.setRole(Role.USER);
        anotherUser = userRepository.save(anotherUser);
    }

    private void authenticateUser(String username) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username, "password", new ArrayList<>());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldCreateReviewWithAuthenticatedUser() {
        authenticateUser("testuser");

        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(8);
        request.setComment("Great movie!");
        request.setMovieId(testMovie.getId());

        ReviewDTO created = reviewService.createReview(request);

        assertNotNull(created.getId());
        assertEquals(8, created.getRating());
        assertEquals("Great movie!", created.getComment());
        assertEquals(testMovie.getId(), created.getMovieId());
        assertEquals(testUser.getId(), created.getUserId());
        assertEquals("testuser", created.getUsername());
    }

    @Test
    void shouldGetAllReviews() {
        CreateReviewRequestDTO request1 = new CreateReviewRequestDTO();
        request1.setRating(7);
        request1.setMovieId(testMovie.getId());
        reviewService.createReview(request1);

        CreateReviewRequestDTO request2 = new CreateReviewRequestDTO();
        request2.setRating(9);
        request2.setMovieId(testMovie.getId());
        reviewService.createReview(request2);

        List<ReviewDTO> reviews = reviewService.getAllReviews();

        assertEquals(2, reviews.size());
    }

    @Test
    void shouldGetReviewsByMovieId() {
        Movie anotherMovie = new Movie();
        anotherMovie.setTitle("Another Movie");
        anotherMovie.setReleaseYear(2023);
        anotherMovie = movieRepository.save(anotherMovie);

        CreateReviewRequestDTO request1 = new CreateReviewRequestDTO();
        request1.setRating(7);
        request1.setMovieId(testMovie.getId());
        reviewService.createReview(request1);

        CreateReviewRequestDTO request2 = new CreateReviewRequestDTO();
        request2.setRating(8);
        request2.setMovieId(testMovie.getId());
        reviewService.createReview(request2);

        CreateReviewRequestDTO request3 = new CreateReviewRequestDTO();
        request3.setRating(6);
        request3.setMovieId(anotherMovie.getId());
        reviewService.createReview(request3);

        List<ReviewDTO> movieReviews = reviewService.getReviewsByMovieId(testMovie.getId());

        assertEquals(2, movieReviews.size());
        assertTrue(movieReviews.stream().allMatch(r -> r.getMovieId().equals(testMovie.getId())));
    }

    @Test
    void shouldUpdateOwnReview() {
        authenticateUser("testuser");

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
    void shouldNotUpdateOtherUsersReview() {
        authenticateUser("testuser");

        CreateReviewRequestDTO createRequest = new CreateReviewRequestDTO();
        createRequest.setRating(7);
        createRequest.setMovieId(testMovie.getId());
        ReviewDTO created = reviewService.createReview(createRequest);

        // Switch to another user
        authenticateUser("anotheruser");

        UpdateReviewRequestDTO updateRequest = new UpdateReviewRequestDTO();
        updateRequest.setRating(9);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(created.getId(), updateRequest);
        });

        assertEquals("You can only edit your own reviews", exception.getMessage());
    }

    @Test
    void shouldDeleteOwnReview() {
        authenticateUser("testuser");

        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(6);
        request.setMovieId(testMovie.getId());
        ReviewDTO created = reviewService.createReview(request);

        reviewService.deleteReview(created.getId());

        assertFalse(reviewRepository.existsById(created.getId()));
    }

    @Test
    void shouldNotDeleteOtherUsersReview() {
        authenticateUser("testuser");

        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(6);
        request.setMovieId(testMovie.getId());
        ReviewDTO created = reviewService.createReview(request);

        // Switch to another user
        authenticateUser("anotheruser");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(created.getId());
        });

        assertEquals("You can only delete your own reviews", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMovieNotFoundForReview() {
        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(8);
        request.setMovieId(999L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.createReview(request);
        });

        assertTrue(exception.getMessage().contains("Movie not found"));
    }

    @Test
    void shouldUpdateOnlyRatingWhenCommentIsNull() {
        authenticateUser("testuser");

        CreateReviewRequestDTO createRequest = new CreateReviewRequestDTO();
        createRequest.setRating(7);
        createRequest.setComment("Original comment");
        createRequest.setMovieId(testMovie.getId());
        ReviewDTO created = reviewService.createReview(createRequest);

        UpdateReviewRequestDTO updateRequest = new UpdateReviewRequestDTO();
        updateRequest.setRating(9);

        ReviewDTO updated = reviewService.updateReview(created.getId(), updateRequest);

        assertEquals(9, updated.getRating());
        assertEquals("Original comment", updated.getComment());
    }

    @Test
    void shouldUpdateOnlyCommentWhenRatingIsNull() {
        authenticateUser("testuser");

        CreateReviewRequestDTO createRequest = new CreateReviewRequestDTO();
        createRequest.setRating(7);
        createRequest.setComment("Original comment");
        createRequest.setMovieId(testMovie.getId());
        ReviewDTO created = reviewService.createReview(createRequest);

        UpdateReviewRequestDTO updateRequest = new UpdateReviewRequestDTO();
        updateRequest.setComment("Updated comment");

        ReviewDTO updated = reviewService.updateReview(created.getId(), updateRequest);

        assertEquals(7, updated.getRating());
        assertEquals("Updated comment", updated.getComment());
    }
}