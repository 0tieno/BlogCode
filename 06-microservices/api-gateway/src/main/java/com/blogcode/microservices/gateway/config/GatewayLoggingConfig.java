package com.blogcode.microservices.gateway.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers a simple request-logging servlet {@link Filter} that runs in
 * front of every route the gateway forwards.
 *
 * <p><strong>Why this class exists:</strong> an API gateway is the perfect
 * place to demonstrate "cross-cutting concerns" - behavior that should
 * apply uniformly to every request regardless of which downstream service
 * ultimately handles it. Logging is the simplest possible example; a real
 * gateway would add authentication, rate limiting, or request/response
 * transformation in exactly this same layer, before the request is
 * forwarded on to {@code student-service} or {@code course-service}.
 */
@Slf4j
@Configuration
public class GatewayLoggingConfig {

    /**
     * Wraps {@link #requestLoggingFilter()} in a {@link FilterRegistrationBean}
     * so it applies to every URL pattern the embedded servlet container
     * dispatches, including the routes configured in
     * {@code application.yml}.
     *
     * @return the registration bean Spring Boot uses to install the filter
     */
    @Bean
    public FilterRegistrationBean<Filter> requestLoggingFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>(requestLoggingFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    /**
     * Builds a minimal servlet {@link Filter} that logs the HTTP method and
     * URI of every request the gateway receives before it is routed.
     *
     * @return the logging filter instance
     */
    public Filter requestLoggingFilter() {
        return (ServletRequest request, ServletResponse response, FilterChain chain) -> {
            if (request instanceof HttpServletRequest httpRequest) {
                log.info("Gateway received {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            }
            chain.doFilter(request, response);
        };
    }
}
