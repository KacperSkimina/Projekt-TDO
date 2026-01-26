package com.example.ratingsystem.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new User("testuser", "password", new ArrayList<>());
    }

    @Test
    void shouldGenerateToken() {
        // When
        String token = jwtUtil.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void shouldExtractUsernameFromToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        String username = jwtUtil.extractUsername(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void shouldExtractExpirationFromToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date())); // Token should expire in the future
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void shouldFailValidationForWrongUser() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        UserDetails wrongUser = new User("wronguser", "password", new ArrayList<>());

        // When
        Boolean isValid = jwtUtil.validateToken(token, wrongUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    void shouldNotBeExpiredForNewToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertTrue(expiration.after(new Date()));
    }
}