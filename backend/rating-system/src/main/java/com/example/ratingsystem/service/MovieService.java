package com.example.ratingsystem.service;

import com.example.ratingsystem.dto.MovieDTO;
import com.example.ratingsystem.dto.MovieRequestDTO;
import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.mapper.MovieMapper;
import com.example.ratingsystem.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final RatingService ratingService;

    @Transactional(readOnly = true)
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::mapWithAvgRating)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        return mapWithAvgRating(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::mapWithAvgRating)
                .toList();
    }

    @Transactional
    public MovieDTO createMovie(MovieRequestDTO request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDirector(request.getDirector());
        movie.setDescription(request.getDescription());
        movie.setReleaseYear(request.getReleaseYear());

        Movie savedMovie = movieRepository.save(movie);
        return mapWithAvgRating(savedMovie);
    }

    @Transactional
    public MovieDTO updateMovie(Long id, MovieRequestDTO request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            movie.setTitle(request.getTitle());
        }
        if (request.getDirector() != null) {
            movie.setDirector(request.getDirector());
        }
        if (request.getDescription() != null) {
            movie.setDescription(request.getDescription());
        }
        if (request.getReleaseYear() != null) {
            movie.setReleaseYear(request.getReleaseYear());
        }

        Movie updatedMovie = movieRepository.save(movie);
        return mapWithAvgRating(updatedMovie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }

    private MovieDTO mapWithAvgRating(Movie movie) {
        double avgRating = ratingService.calculateAverage(movie);
        MovieDTO dto = movieMapper.toDto(movie, avgRating);

        if (movie.getReviews() != null) {
            dto.setReviews(movie.getReviews().stream().map(review -> {
                com.example.ratingsystem.dto.ReviewDTO rDto = new com.example.ratingsystem.dto.ReviewDTO();
                rDto.setId(review.getId());
                rDto.setRating(review.getRating());
                rDto.setComment(review.getComment());
                rDto.setMovieId(movie.getId());
                if (review.getUser() != null) {
                    rDto.setUsername(review.getUser().getUsername());
                    rDto.setUserId(review.getUser().getId());
                }
                return rDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

}