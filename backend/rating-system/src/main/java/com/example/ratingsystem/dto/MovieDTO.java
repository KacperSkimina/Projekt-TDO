package com.example.ratingsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class MovieDTO {
    private Long id;
    private String title;
    private String director;
    private String description;
    private Integer releaseYear;
    private Double averageRating;
    private List<ReviewDTO> reviews;
}
