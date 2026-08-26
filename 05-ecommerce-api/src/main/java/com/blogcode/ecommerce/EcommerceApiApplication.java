package com.blogcode.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the 05-ecommerce-api curriculum module.
 *
 * <p><strong>Why this class exists:</strong> {@code @SpringBootApplication}
 * is a convenience meta-annotation combining {@code @Configuration}
 * (this class can declare beans), {@code @EnableAutoConfiguration}
 * (Spring Boot inspects the classpath and wires up sensible defaults for
 * every starter we added in {@code pom.xml}), and {@code @ComponentScan}
 * (every {@code @Component}/{@code @Service}/{@code @Repository}/
 * {@code @RestController} under the {@code com.blogcode.ecommerce} package
 * is automatically discovered and registered).
 */
@SpringBootApplication
public class EcommerceApiApplication {

    /**
     * Boots the embedded servlet container and the whole Spring
     * application context.
     *
     * @param args standard command-line arguments, forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApiApplication.class, args);
    }
}
