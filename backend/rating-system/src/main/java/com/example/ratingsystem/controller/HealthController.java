package com.example.ratingsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/")
    public String root() {
        return """
            {
                "status": "running",
                "service": "Movie Rating System API",
                "endpoints": {
                    "health": "/actuator/health",
                    "metrics": "/actuator/prometheus",
                    "movies": "/api/movies",
                    "reviews": "/api/reviews",
                    "auth": "/api/auth/login"
                }
            }
            """;
    }
}