package com.example.ratingsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MovieRequestDTO {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 200, message = "Director must not exceed 200 characters")
    private String director;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Min(value = 1888, message = "Release year must be at least 1888")
    @Max(value = 2100, message = "Release year must be at most 2100")
    private Integer releaseYear;
}