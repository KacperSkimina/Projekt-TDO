package com.example.ratingsystem.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter requestCounter(MeterRegistry registry) {
        return Counter.builder("http_requests_total")
                .description("Total HTTP requests")
                .tag("application", "movie-rating-system")
                .register(registry);
    }

    @Bean
    public Counter authSuccessCounter(MeterRegistry registry) {
        return Counter.builder("auth_success_total")
                .description("Total successful authentications")
                .register(registry);
    }

    @Bean
    public Counter authFailureCounter(MeterRegistry registry) {
        return Counter.builder("auth_failure_total")
                .description("Total failed authentications")
                .register(registry);
    }

    @Bean
    public Counter reviewCreationCounter(MeterRegistry registry) {
        return Counter.builder("reviews_created_total")
                .description("Total reviews created")
                .register(registry);
    }
}