package com.blogcode.microservices.student.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Customizes the default Resilience4j circuit breaker settings used to
 * protect calls made through {@link com.blogcode.microservices.student.client.CourseClient}.
 *
 * <p><strong>Why this class exists:</strong> Spring Cloud CircuitBreaker
 * ships with sensible defaults, but explicitly configuring the failure
 * thresholds here makes the resilience behaviour visible and teachable
 * instead of implicit. With this configuration, if more than 50% of the
 * last 5 calls to course-service fail, the circuit "opens" for 5 seconds -
 * during which every call is instantly routed to
 * {@link com.blogcode.microservices.student.client.CourseClientFallback}
 * without even attempting a network call - before "half-opening" to test
 * whether course-service has recovered.
 */
@Configuration
public class ResilienceConfig {

    /**
     * Registers the default circuit breaker + time limiter configuration
     * applied to every {@code @FeignClient} protected by Spring Cloud
     * CircuitBreaker in this service.
     *
     * @return a customizer Spring Cloud CircuitBreaker applies to every circuit breaker instance
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCircuitBreakerCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .slidingWindowSize(5)
                        .waitDurationInOpenState(Duration.ofSeconds(5))
                        .permittedNumberOfCallsInHalfOpenState(2)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .build());
    }
}
