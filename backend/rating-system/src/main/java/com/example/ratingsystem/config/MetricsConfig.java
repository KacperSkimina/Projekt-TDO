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
}
