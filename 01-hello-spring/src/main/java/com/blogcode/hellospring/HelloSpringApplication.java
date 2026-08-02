package com.blogcode.hellospring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the "Hello Spring" curriculum application.
 *
 * <p>This is the very first class a beginner should read. Everything in Spring
 * Boot starts from a {@code main} method just like a plain Java program - the
 * only special part is the call to {@link SpringApplication#run(Class, String...)},
 * which:
 * <ol>
 *   <li>Creates the Spring {@code ApplicationContext} (the container that
 *       creates and wires together all of our beans, e.g. the controller and
 *       the service).</li>
 *   <li>Triggers Spring Boot's auto-configuration, which inspects the
 *       classpath (here: {@code spring-boot-starter-webmvc}) and automatically
 *       configures an embedded Tomcat server plus Spring MVC.</li>
 *   <li>Starts the embedded web server so the application can accept HTTP
 *       requests immediately, with no external application server required.</li>
 * </ol>
 *
 * <p>The {@link SpringBootApplication} annotation is itself a convenience
 * "meta-annotation" that combines three separate annotations:
 * <ul>
 *   <li>{@code @Configuration} - marks this class as a source of bean
 *       definitions.</li>
 *   <li>{@code @EnableAutoConfiguration} - turns on Spring Boot's
 *       classpath-driven auto-configuration.</li>
 *   <li>{@code @ComponentScan} - tells Spring to scan this class's package
 *       (and all sub-packages) for classes annotated with stereotypes such as
 *       {@code @Service}, {@code @RestController}, etc.</li>
 * </ul>
 * This is why {@code HelloSpringApplication} lives at the root package
 * ({@code com.blogcode.hellospring}) - every other class in this project sits
 * in a sub-package so component scanning picks them up automatically.
 */
@SpringBootApplication
public class HelloSpringApplication {

    /**
     * Boots the Spring application context and starts the embedded HTTP
     * server.
     *
     * <p>We keep this method intentionally tiny - all real application logic
     * belongs in dedicated layers (controller/service), never in
     * {@code main}. This keeps the entry point easy to read and testable in
     * isolation.
     *
     * @param args command-line arguments forwarded by the JVM; Spring Boot
     *             also allows overriding {@code application.properties}
     *             values via arguments such as {@code --server.port=9090}.
     */
    public static void main(String[] args) {
        SpringApplication.run(HelloSpringApplication.class, args);
    }
}
