package com.blogcode.microservices.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Entry point for the Eureka service discovery server.
 *
 * <p><strong>Why this class exists:</strong> in a microservices system,
 * services must find each other without hardcoding host names and ports
 * that change every time something restarts or scales. Eureka solves this
 * with a "phone book" pattern: every service registers itself here on
 * startup under a logical name (e.g. {@code student-service}), and any
 * other service (or the API gateway) can ask Eureka "where is
 * student-service right now?" instead of relying on static configuration.
 *
 * <p>{@code @EnableEurekaServer} is the single annotation that turns an
 * ordinary Spring Boot web application into a full Eureka registry, serving
 * both a human-readable dashboard and the REST API other services use to
 * register/query.
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryApplication {

    /**
     * Boots the Eureka server application context and embedded servlet
     * container.
     *
     * @param args standard command-line arguments, forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(ServiceRegistryApplication.class, args);
    }
}
