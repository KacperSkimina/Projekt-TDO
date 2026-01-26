package com.example.ratingsystem.service;

import com.example.ratingsystem.dto.MovieDTO;
import com.example.ratingsystem.dto.MovieRequestDTO;
import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.entity.Review;
import com.example.ratingsystem.repository.MovieRepository;
import com.example.ratingsystem.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoMovies() {
        List<MovieDTO> movies = movieService.getAllMovies();
        assertTrue(movies.isEmpty());
    }

    @Test
    void shouldCreateMovie() {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Inception");
        request.setDirector("Christopher Nolan");
        request.setDescription("A mind-bending thriller");
        request.setReleaseYear(2010);

        MovieDTO created = movieService.createMovie(request);

        assertNotNull(created.getId());
        assertEquals("Inception", created.getTitle());
        assertEquals("Christopher Nolan", created.getDirector());
        assertEquals("A mind-bending thriller", created.getDescription());
        assertEquals(2010, created.getReleaseYear());
        assertEquals(0.0, created.getAverageRating());
    }

    @Test
    void shouldUpdateMovie() {
        MovieRequestDTO createRequest = new MovieRequestDTO();
        createRequest.setTitle("Original Title");
        createRequest.setDirector("Original Director");
        createRequest.setReleaseYear(2020);
        MovieDTO created = movieService.createMovie(createRequest);

        MovieRequestDTO updateRequest = new MovieRequestDTO();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDirector("Updated Director");
        updateRequest.setDescription("New description");

        MovieDTO updated = movieService.updateMovie(created.getId(), updateRequest);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Director", updated.getDirector());
        assertEquals("New description", updated.getDescription());
        assertEquals(2020, updated.getReleaseYear());
    }

    @Test
    void shouldDeleteMovie() {
        MovieRequestDTO request = new MovieRequestDTO();
        request.setTitle("Movie to Delete");
        request.setDirector("Some Director");
        request.setReleaseYear(2021);
        MovieDTO created = movieService.createMovie(request);

        movieService.deleteMovie(created.getId());

        assertFalse(movieRepository.existsById(created.getId()));
    }

    @Test
    void shouldCalculateAverageRating() {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setDirector("Test Director");
        movie.setReleaseYear(2022);
        movie = movieRepository.save(movie);

        Review review1 = new Review();
        review1.setRating(8);
        review1.setMovie(movie);
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setRating(10);
        review2.setMovie(movie);
        reviewRepository.save(review2);

        MovieDTO movieDTO = movieService.getMovieById(movie.getId());

        assertEquals(9.0, movieDTO.getAverageRating());
    }

    @Test
    void shouldSearchMoviesByTitle() {
        MovieRequestDTO request1 = new MovieRequestDTO();
        request1.setTitle("The Dark Knight");
        request1.setDirector("Christopher Nolan");
        request1.setReleaseYear(2008);
        movieService.createMovie(request1);

        MovieRequestDTO request2 = new MovieRequestDTO();
        request2.setTitle("The Dark Knight Rises");
        request2.setDirector("Christopher Nolan");
        request2.setReleaseYear(2012);
        movieService.createMovie(request2);

        MovieRequestDTO request3 = new MovieRequestDTO();
        request3.setTitle("Inception");
        request3.setDirector("Christopher Nolan");
        request3.setReleaseYear(2010);
        movieService.createMovie(request3);

        List<MovieDTO> results = movieService.searchMoviesByTitle("dark knight");

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(m ->
            m.getTitle().toLowerCase().contains("dark knight")));
    }

    @Test
    void shouldThrowExceptionWhenMovieNotFound() {
        assertThrows(RuntimeException.class, () -> {
            movieService.getMovieById(999L);
        });
    }
}