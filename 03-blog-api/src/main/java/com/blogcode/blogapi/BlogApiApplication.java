package com.blogcode.blogapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Blog API Spring Boot application.
 *
 * <p>{@code @SpringBootApplication} is a convenience meta-annotation combining
 * {@code @Configuration}, {@code @EnableAutoConfiguration} and {@code @ComponentScan}:
 * it tells Spring Boot to auto-configure beans based on the dependencies on the classpath
 * (here: Spring MVC, Spring Data JPA, Bean Validation) and to scan this package and its
 * sub-packages for components (controllers, services, repositories).
 */
@SpringBootApplication
public class BlogApiApplication {

    /**
     * Boots the Spring application context and starts the embedded web server.
     *
     * @param args standard command-line arguments, forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(BlogApiApplication.class, args);
    }
}
