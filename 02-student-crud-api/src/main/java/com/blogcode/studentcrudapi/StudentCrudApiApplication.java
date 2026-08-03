package com.blogcode.studentcrudapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the "Student CRUD API" curriculum application.
 *
 * <p>As in {@code HelloSpringApplication} from the 01-hello-spring project,
 * the {@link SpringBootApplication} annotation triggers component scanning
 * (discovering the {@code @RestController}, {@code @Service} and
 * {@code @Repository} beans in sub-packages of
 * {@code com.blogcode.studentcrudapi}) and Spring Boot's auto-configuration.
 * In this project, auto-configuration does considerably more work than in
 * the first: because {@code spring-boot-starter-data-jpa} and the H2 driver
 * are both on the classpath, Spring Boot automatically configures a
 * {@code DataSource}, an {@code EntityManagerFactory}, a
 * {@code PlatformTransactionManager}, and proxies for every
 * {@code JpaRepository} interface it finds - all without a single line of
 * manual configuration code in this project.
 *
 * <p>On startup, Spring Boot also executes
 * {@code src/main/resources/data.sql} against the H2 database (see
 * {@code spring.sql.init.mode=always} in {@code application.properties}),
 * pre-loading a handful of sample students so the API is immediately
 * useful without any manual data entry.
 */
@SpringBootApplication
public class StudentCrudApiApplication {

    /**
     * Boots the Spring application context, initialises the embedded H2
     * database and JPA infrastructure, loads the sample data from
     * {@code data.sql}, and starts the embedded HTTP server.
     *
     * @param args command-line arguments forwarded by the JVM.
     */
    public static void main(String[] args) {
        SpringApplication.run(StudentCrudApiApplication.class, args);
    }
}
