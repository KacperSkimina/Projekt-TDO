package com.example.ratingsystem.service;

import com.example.ratingsystem.dto.MovieDTO;
import com.example.ratingsystem.mapper.MovieMapper;
import com.example.ratingsystem.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final RatingService ratingService;

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
            .stream()
            .map(movie -> {
                double avg = ratingService.calculateAverage(movie);
                return movieMapper.toDto(movie, avg);
            })
            .toList();
    }
}