package com.blogcode.microservices.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.blogcode.microservices.course.domain.Course;
import com.blogcode.microservices.course.dto.CourseDto;
import com.blogcode.microservices.course.dto.CourseRequest;
import com.blogcode.microservices.course.exception.ResourceNotFoundException;
import com.blogcode.microservices.course.repository.CourseRepository;
import com.blogcode.microservices.course.service.impl.CourseServiceImpl;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Plain Mockito-based unit test for {@link CourseServiceImpl}, following
 * the same "no Spring context required" testing pattern used in module 5's
 * {@code OrderServiceImplTest}.
 */
class CourseServiceImplTest {

    private CourseRepository courseRepository;
    private CourseService courseService;

    /**
     * Rebuilds a fresh mock repository and service before each test.
     */
    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseService = new CourseServiceImpl(courseRepository);
    }

    /**
     * Verifies that creating a course persists it and maps the saved
     * entity back into a {@link CourseDto}.
     */
    @Test
    void create_savesAndReturnsCourse() {
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course course = invocation.getArgument(0);
            course.setId(1L);
            course.setCreatedAt(Instant.now());
            course.setUpdatedAt(Instant.now());
            return course;
        });

        CourseRequest request = new CourseRequest("Intro to Databases", "Relational modeling", "Dr. Smith", 3);
        CourseDto result = courseService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Intro to Databases");
        assertThat(result.credits()).isEqualTo(3);
    }

    /**
     * Verifies that requesting a non-existent course fails with
     * {@link ResourceNotFoundException} instead of a raw {@code null}.
     */
    @Test
    void getById_throwsResourceNotFoundException_whenCourseDoesNotExist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
