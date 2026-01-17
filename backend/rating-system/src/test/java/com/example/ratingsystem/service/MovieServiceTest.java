package com.example.ratingsystem.service;

import com.example.ratingsystem.dto.MovieDTO;
import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoMovies() {
        List<MovieDTO> movies = movieService.getAllMovies();
        assertTrue(movies.isEmpty());
    }

    @Test
    void shouldReturnMovieWithCorrectAverageRating() {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie = movieRepository.save(movie);

        List<MovieDTO> movies = movieService.getAllMovies();

        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getTitle());
    }
}