package com.blogcode.microservices.student.repository;

import com.blogcode.microservices.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Student} entities.
 *
 * <p>See course-service's {@code CourseRepository} for the rationale
 * behind extending {@link JpaRepository} for effortless CRUD support.
 */
public interface StudentRepository extends JpaRepository<Student, Long> {
}
