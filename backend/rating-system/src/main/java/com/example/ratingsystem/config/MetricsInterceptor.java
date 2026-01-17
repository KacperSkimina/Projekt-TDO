package com.example.ratingsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import io.micrometer.core.instrument.Counter;


@Component
@RequiredArgsConstructor
public class MetricsInterceptor implements HandlerInterceptor {
    private final Counter requestCounter;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        requestCounter.increment();
        return true;
    }
}