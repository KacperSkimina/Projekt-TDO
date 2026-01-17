package com.example.ratingsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {

    private Long id;
    private Integer rating;
    private String comment;
    private Long movieId;
    private String movieTitle;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;

}

