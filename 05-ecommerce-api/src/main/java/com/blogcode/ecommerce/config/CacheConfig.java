package com.blogcode.ecommerce.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's cache abstraction application-wide.
 *
 * <p><strong>Why this class exists:</strong> {@code @EnableCaching} is what
 * activates the interceptors that make {@code @Cacheable}/{@code @CacheEvict}
 * annotations (used in {@code ProductServiceImpl}) actually take effect.
 * The concrete cache technology (Redis) is selected declaratively via the
 * {@code spring.cache.type=redis} property in {@code application.yml};
 * Spring Boot auto-configures the {@code RedisCacheManager} for us, so this
 * class only needs to flip the feature on.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
