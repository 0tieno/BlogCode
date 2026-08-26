package com.blogcode.microservices.course.service.impl;

import com.blogcode.microservices.course.domain.Course;
import com.blogcode.microservices.course.dto.CourseDto;
import com.blogcode.microservices.course.dto.CourseRequest;
import com.blogcode.microservices.course.exception.ResourceNotFoundException;
import com.blogcode.microservices.course.repository.CourseRepository;
import com.blogcode.microservices.course.service.CourseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link CourseService} implementation backed by
 * {@link CourseRepository}.
 *
 * <p><strong>Why this class exists:</strong> keeps the actual business
 * rules (currently just "does this course exist") separate from the HTTP
 * layer and persistence layer, the same layered architecture used
 * throughout this curriculum. {@code @RequiredArgsConstructor} generates
 * the constructor Spring uses for constructor injection of
 * {@link CourseRepository}.
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CourseDto create(CourseRequest request) {
        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .instructor(request.instructor())
                .credits(request.credits())
                .build();
        return toDto(courseRepository.save(course));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public CourseDto getById(Long id) {
        return toDto(findEntityById(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<CourseDto> getAll() {
        return courseRepository.findAll().stream().map(this::toDto).toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CourseDto update(Long id, CourseRequest request) {
        Course course = findEntityById(id);
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setInstructor(request.instructor());
        course.setCredits(request.credits());
        // Dirty checking flushes this UPDATE automatically at commit time,
        // the same Hibernate pattern used throughout module 5.
        return toDto(course);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(Long id) {
        Course course = findEntityById(id);
        courseRepository.delete(course);
    }

    /**
     * Shared lookup helper that centralizes the "find or throw 404" pattern.
     *
     * @param id the course id to look up
     * @return the managed {@link Course} entity
     * @throws ResourceNotFoundException if no course has this id
     */
    private Course findEntityById(Long id) {
        return courseRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    /**
     * Converts a {@link Course} entity into its read-model {@link CourseDto}.
     *
     * @param course the entity to convert
     * @return an equivalent, detached {@link CourseDto}
     */
    private CourseDto toDto(Course course) {
        return new CourseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getInstructor(),
                course.getCredits(),
                course.getCreatedAt(),
                course.getUpdatedAt());
    }
}
