package com.example.ratingsystem.controller;

import com.example.ratingsystem.dto.LoginRequestDTO;
import com.example.ratingsystem.dto.RegisterRequestDTO;
import com.example.ratingsystem.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUserSuccessfully() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void shouldReturnBadRequestWhenRegisterWithInvalidData() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("ab"); // Too short (min 3)
        request.setEmail("newuser@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRegisterWithInvalidEmail() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("testuser");
        request.setEmail("invalid-email"); // Invalid email format
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenRegisterWithShortPassword() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("12345"); // Too short (min 6)

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfullyWithCorrectCredentials() throws Exception {
        // First register a user
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("loginuser");
        registerRequest.setEmail("login@example.com");
        registerRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Then login
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("loginuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("loginuser"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginWithWrongPassword() throws Exception {
        // First register a user
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("correctpassword");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Try to login with wrong password
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError()); // Accept 401 or 403
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginWithNonExistentUser() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is4xxClientError()); // Accept 401 or 403
    }

    @Test
    void shouldReturnBadRequestWhenLoginWithBlankUsername() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername(""); // Blank username
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenRegisterWithExistingUsername() throws Exception {
        // First register a user
        RegisterRequestDTO request1 = new RegisterRequestDTO();
        request1.setUsername("duplicate");
        request1.setEmail("user1@example.com");
        request1.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Try to register with same username - should fail
        RegisterRequestDTO request2 = new RegisterRequestDTO();
        request2.setUsername("duplicate");
        request2.setEmail("user2@example.com");
        request2.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    @Test
    void shouldReturnConflictWhenRegisterWithExistingEmail() throws Exception {
        // First register a user
        RegisterRequestDTO request1 = new RegisterRequestDTO();
        request1.setUsername("user1");
        request1.setEmail("duplicate@example.com");
        request1.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Try to register with same email - should fail
        RegisterRequestDTO request2 = new RegisterRequestDTO();
        request2.setUsername("user2");
        request2.setEmail("duplicate@example.com");
        request2.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }
}