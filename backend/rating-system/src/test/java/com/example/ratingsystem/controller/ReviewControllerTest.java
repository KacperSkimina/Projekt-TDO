package com.example.ratingsystem.controller;

import com.example.ratingsystem.dto.CreateReviewRequestDTO;
import com.example.ratingsystem.dto.UpdateReviewRequestDTO;
import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.entity.Role;
import com.example.ratingsystem.entity.User;
import com.example.ratingsystem.repository.MovieRepository;
import com.example.ratingsystem.repository.ReviewRepository;
import com.example.ratingsystem.repository.UserRepository;
import com.example.ratingsystem.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authToken;
    private Long movieId;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.USER);
        userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "testuser", "password", new ArrayList<>());
        authToken = jwtUtil.generateToken(userDetails);

        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setReleaseYear(2024);
        movie = movieRepository.save(movie);
        movieId = movie.getId();
    }

    @Test
    void shouldGetAllReviewsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetReviewsByMovieIdWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/reviews/movie/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldCreateReviewWithAuth() throws Exception {
        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(8);
        request.setComment("Great movie!");
        request.setMovieId(movieId);

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(8))
                .andExpect(jsonPath("$.comment").value("Great movie!"))
                .andExpect(jsonPath("$.movieId").value(movieId))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldNotCreateReviewWithoutAuth() throws Exception {
        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(8);
        request.setMovieId(movieId);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateOwnReview() throws Exception {
        CreateReviewRequestDTO createRequest = new CreateReviewRequestDTO();
        createRequest.setRating(7);
        createRequest.setComment("Good");
        createRequest.setMovieId(movieId);

        String createResponse = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(createResponse).get("id").asLong();

        UpdateReviewRequestDTO updateRequest = new UpdateReviewRequestDTO();
        updateRequest.setRating(9);
        updateRequest.setComment("Excellent!");

        mockMvc.perform(put("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(9))
                .andExpect(jsonPath("$.comment").value("Excellent!"));
    }

    @Test
    void shouldDeleteOwnReview() throws Exception {
        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(7);
        request.setMovieId(movieId);

        String response = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteReviewWithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldValidateReviewRating() throws Exception {
        CreateReviewRequestDTO invalidRequest = new CreateReviewRequestDTO();
        invalidRequest.setRating(11); // Invalid: max is 10
        invalidRequest.setMovieId(movieId);

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateMinimumRating() throws Exception {
        CreateReviewRequestDTO invalidRequest = new CreateReviewRequestDTO();
        invalidRequest.setRating(0); // Invalid: min is 1
        invalidRequest.setMovieId(movieId);

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRequireMovieIdForReview() throws Exception {
        CreateReviewRequestDTO invalidRequest = new CreateReviewRequestDTO();
        invalidRequest.setRating(8);
        // Missing movieId

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetReviewById() throws Exception {
        CreateReviewRequestDTO request = new CreateReviewRequestDTO();
        request.setRating(8);
        request.setComment("Test review");
        request.setMovieId(movieId);

        String response = mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long reviewId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/reviews/" + reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.rating").value(8))
                .andExpect(jsonPath("$.comment").value("Test review"));
    }
}