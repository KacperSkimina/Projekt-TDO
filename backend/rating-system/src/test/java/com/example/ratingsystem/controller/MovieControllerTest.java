package com.example.ratingsystem.controller;

import com.example.ratingsystem.dto.MovieRequestDTO;
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
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authToken;

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
    }

    @Test
    void shouldGetAllMoviesWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetMovieByIdWithoutAuth() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Test Movie");
        request.setDirector("Test Director");
        request.setReleaseYear(2024);

        String response = mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long movieId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.title").value("Test Movie"))
                .andExpect(jsonPath("$.director").value("Test Director"));
    }

    @Test
    void shouldSearchMoviesByTitle() throws Exception {
        MovieRequestDTO request1 = new MovieRequestDTO();
        request1.setTitle("The Dark Knight");
        request1.setDirector("Christopher Nolan");
        request1.setReleaseYear(2008);

        MovieRequestDTO request2 = new MovieRequestDTO();
        request2.setTitle("Inception");
        request2.setDirector("Christopher Nolan");
        request2.setReleaseYear(2010);

        mockMvc.perform(post("/api/movies")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/movies")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/movies?search=dark"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("The Dark Knight"));
    }

    @Test
    void shouldCreateMovieWithAuth() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("New Movie");
        request.setDirector("New Director");
        request.setDescription("A great movie");
        request.setReleaseYear(2024);

        mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Movie"))
                .andExpect(jsonPath("$.director").value("New Director"))
                .andExpect(jsonPath("$.description").value("A great movie"))
                .andExpect(jsonPath("$.releaseYear").value(2024))
                .andExpect(jsonPath("$.averageRating").value(0.0));
    }

    @Test
    void shouldNotCreateMovieWithoutAuth() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("New Movie");
        request.setDirector("Some Director");
        request.setReleaseYear(2024);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateMovieWithAuth() throws Exception {
        MovieRequestDTO createRequest = new MovieRequestDTO();
        createRequest.setTitle("Original Title");
        createRequest.setDirector("Original Director");
        createRequest.setReleaseYear(2023);

        String createResponse = mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long movieId = objectMapper.readTree(createResponse).get("id").asLong();

        MovieRequestDTO updateRequest = new MovieRequestDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDirector("Updated Director");
        updateRequest.setDescription("Updated description");
        updateRequest.setReleaseYear(2024);

        mockMvc.perform(put("/api/movies/" + movieId)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.director").value("Updated Director"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.releaseYear").value(2024));
    }

    @Test
    void shouldNotUpdateMovieWithoutAuth() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Updated Title");

        mockMvc.perform(put("/api/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteMovieWithAuth() throws Exception {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Movie to Delete");
        request.setDirector("Director to Delete");
        request.setReleaseYear(2024);

        String response = mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long movieId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/movies/" + movieId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        // Verify it's deleted - should return error
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldNotDeleteMovieWithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/movies/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnErrorForNonExistentMovie() throws Exception {
        // Trying to get a movie that doesn't exist should return 500 with error message
        mockMvc.perform(get("/api/movies/99999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Movie not found with id: 99999"));
    }

    @Test
    void shouldValidateMovieRequest() throws Exception {
        MovieRequestDTO invalidRequest = new MovieRequestDTO();
        // Missing title

        mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}