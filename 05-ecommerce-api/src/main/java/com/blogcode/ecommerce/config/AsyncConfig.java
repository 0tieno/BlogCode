package com.blogcode.ecommerce.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Enables and configures Spring's asynchronous method execution support.
 *
 * <p><strong>Why this class exists:</strong> {@code @EnableAsync} is what
 * makes {@code @Async} methods (see {@code EmailServiceImpl}) actually run
 * on a background thread instead of the caller's thread. This class also
 * defines a dedicated, bounded {@link ThreadPoolTaskExecutor} rather than
 * relying on Spring's default {@code SimpleAsyncTaskExecutor}, which spawns
 * an unbounded number of threads and is explicitly discouraged for
 * production use.
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Builds the thread pool used for every {@code @Async} method in the
     * application (there is currently only one: email sending).
     *
     * @return a bounded, named thread pool executor
     */
    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        // A recognizable thread name prefix makes it obvious in the logs
        // (see EmailServiceImpl) that email sending truly runs off the
        // request-handling thread.
        executor.setThreadNamePrefix("email-async-");
        executor.initialize();
        return executor;
    }
}
