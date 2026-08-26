package com.blogcode.microservices.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the API gateway module.
 *
 * <p><strong>Why this class exists:</strong> the gateway is the single
 * public "front door" of this microservices system. External clients only
 * ever talk to the gateway; it is responsible for figuring out which
 * internal service should actually handle each request and forwarding it
 * there. This class itself stays minimal - all the interesting routing
 * configuration lives declaratively in {@code application.yml} (see
 * {@code spring.cloud.gateway.server.webmvc.routes}), which is the
 * recommended way to configure Spring Cloud Gateway Server Web MVC for
 * straightforward path-based routing.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Boots the gateway's application context and embedded servlet
     * container.
     *
     * @param args standard command-line arguments, forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
