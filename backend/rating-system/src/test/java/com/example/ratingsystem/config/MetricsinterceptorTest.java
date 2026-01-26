package com.example.ratingsystem.config;

import io.micrometer.core.instrument.Counter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsInterceptorTest {

    @Mock
    private Counter requestCounter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private MetricsInterceptor metricsInterceptor;

    @Test
    void shouldIncrementCounterOnPreHandle() {
        // When
        boolean result = metricsInterceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
        verify(requestCounter, times(1)).increment();
    }
}