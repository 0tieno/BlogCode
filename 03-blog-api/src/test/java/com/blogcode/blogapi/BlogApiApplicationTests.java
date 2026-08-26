package com.blogcode.blogapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test verifying that the full Spring application context wires up correctly,
 * i.e. every bean (controllers, services, repositories, JPA configuration) can be
 * created without errors. This is deliberately the simplest possible test: if it fails,
 * something is fundamentally wrong with the application's configuration.
 */
@SpringBootTest
class BlogApiApplicationTests {

    /**
     * Fails the test only if the Spring context fails to start, thanks to
     * {@code @SpringBootTest} attempting a full context load before this method body
     * even runs.
     */
    @Test
    void contextLoads() {
    }
}
