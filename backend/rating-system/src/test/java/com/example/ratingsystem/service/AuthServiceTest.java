package com.example.ratingsystem.service;

import com.example.ratingsystem.dto.AuthResponseDTO;
import com.example.ratingsystem.dto.LoginRequestDTO;
import com.example.ratingsystem.dto.RegisterRequestDTO;
import com.example.ratingsystem.entity.Role;
import com.example.ratingsystem.entity.User;
import com.example.ratingsystem.repository.UserRepository;
import com.example.ratingsystem.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUser() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponseDTO response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Bearer", response.getType());
        assertNotNull(response.getId());

        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assertNotNull(savedUser);
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void shouldThrowExceptionWhenUsernameExists() {
        User existingUser = new User();
        existingUser.setUsername("existing");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("password"));
        existingUser.setRole(Role.USER);
        userRepository.save(existingUser);

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("existing");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        User existingUser = new User();
        existingUser.setUsername("user1");
        existingUser.setEmail("duplicate@example.com");
        existingUser.setPassword(passwordEncoder.encode("password"));
        existingUser.setRole(Role.USER);
        userRepository.save(existingUser);

        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("user2");
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void shouldLoginSuccessfully() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("loginuser");
        registerRequest.setEmail("login@example.com");
        registerRequest.setPassword("password123");
        authService.register(registerRequest);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("loginuser");
        loginRequest.setPassword("password123");

        AuthResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("loginuser", response.getUsername());
        assertEquals("login@example.com", response.getEmail());

        String username = jwtUtil.extractUsername(response.getToken());
        assertEquals("loginuser", username);
    }

    @Test
    void shouldThrowExceptionWhenLoginWithWrongPassword() {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("user");
        registerRequest.setEmail("user@example.com");
        registerRequest.setPassword("correctpassword");
        authService.register(registerRequest);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("wrongpassword");

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void shouldThrowExceptionWhenLoginWithNonExistentUser() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("password");

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void shouldGenerateValidJwtToken() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("tokenuser");
        request.setEmail("token@example.com");
        request.setPassword("password123");

        AuthResponseDTO response = authService.register(request);

        assertNotNull(response.getToken());
        assertFalse(response.getToken().isEmpty());

        String extractedUsername = jwtUtil.extractUsername(response.getToken());
        assertEquals("tokenuser", extractedUsername);
    }

    @Test
    void shouldSetUserRoleToUserByDefault() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("roletest");
        request.setEmail("roletest@example.com");
        request.setPassword("password123");

        authService.register(request);

        User savedUser = userRepository.findByUsername("roletest").orElse(null);
        assertNotNull(savedUser);
        assertEquals(Role.USER, savedUser.getRole()); // Compare enum to enum
    }
}