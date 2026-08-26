package com.blogcode.microservices.course.repository;

import com.blogcode.microservices.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Course} entities.
 *
 * <p><strong>Why this class exists:</strong> extending {@link JpaRepository}
 * gives us full CRUD and pagination support with zero implementation code -
 * exactly the same pattern used throughout module 5, reinforced here so
 * students see it apply consistently across services.
 */
public interface CourseRepository extends JpaRepository<Course, Long> {
}
