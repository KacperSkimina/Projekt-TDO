package com.example.ratingsystem.service;

import com.example.ratingsystem.dto.CreateReviewRequestDTO;
import com.example.ratingsystem.dto.ReviewDTO;
import com.example.ratingsystem.dto.UpdateReviewRequestDTO;
import com.example.ratingsystem.entity.Movie;
import com.example.ratingsystem.entity.Review;
import com.example.ratingsystem.entity.User;
import com.example.ratingsystem.mapper.ReviewMapper;
import com.example.ratingsystem.repository.MovieRepository;
import com.example.ratingsystem.repository.ReviewRepository;
import com.example.ratingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll()
            .stream()
            .map(reviewMapper::toDto)
            .toList();
    }

    public List<ReviewDTO> getReviewsByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId)
            .stream()
            .map(reviewMapper::toDto)
            .toList();
    }

    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        return reviewMapper.toDto(review);
    }

    @Transactional
    public ReviewDTO createReview(CreateReviewRequestDTO request) {
        Movie movie = movieRepository.findById(request.getMovieId())
            .orElseThrow(() -> new RuntimeException("Movie not found with id: " + request.getMovieId()));

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setMovie(movie);

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId()).orElse(null);
            review.setUser(user);
        }

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    @Transactional
    public ReviewDTO updateReview(Long id, UpdateReviewRequestDTO request) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toDto(updatedReview);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
}
