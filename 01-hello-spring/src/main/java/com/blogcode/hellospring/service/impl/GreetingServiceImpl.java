package com.blogcode.hellospring.service.impl;

import com.blogcode.hellospring.model.Greeting;
import com.blogcode.hellospring.service.GreetingService;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link GreetingService}.
 *
 * <p>Annotated with {@link Service}, a specialisation of Spring's generic
 * {@code @Component} stereotype. During component scanning (triggered by
 * {@code @SpringBootApplication} on {@code HelloSpringApplication}), Spring
 * discovers this class, instantiates exactly one instance of it (a
 * "singleton bean" by default) and registers it in the application context
 * under the {@link GreetingService} type. Any other bean - such as
 * {@code GreetingController} - that declares a dependency on
 * {@code GreetingService} will automatically receive this instance through
 * dependency injection.
 *
 * <p>{@link Slf4j} is a Lombok annotation that generates a
 * {@code private static final Logger log} field at compile time, saving us
 * from writing that boilerplate by hand in every class that needs logging.
 */
@Slf4j
@Service
public class GreetingServiceImpl implements GreetingService {

    /**
     * Thread-safe counter used to generate simple, ever-increasing ids for
     * greetings created during the lifetime of this application instance.
     *
     * <p>{@link AtomicLong} is used instead of a plain {@code long} field
     * because Spring beans are singletons shared across all HTTP request
     * threads by default; a plain counter increment ({@code count++}) is
     * not atomic and could produce duplicate ids under concurrent requests.
     * This is a small but important lesson about thread-safety in
     * singleton-scoped services.
     */
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link #createGreetingFor(String)} with the fixed
     * recipient {@code "World"}, matching the traditional "Hello, World!"
     * programming exercise every beginner starts with.
     */
    @Override
    public Greeting createDefaultGreeting() {
        log.debug("Creating default greeting");
        return createGreetingFor("World");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Greeting createGreetingFor(String name) {
        long nextId = idGenerator.incrementAndGet();
        String message = "Hello, %s!".formatted(name);
        log.debug("Generated greeting id={} for name='{}'", nextId, name);
        return new Greeting(nextId, message, Instant.now());
    }
}
