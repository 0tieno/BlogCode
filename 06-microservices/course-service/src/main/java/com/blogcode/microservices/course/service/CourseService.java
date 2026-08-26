package com.blogcode.microservices.course.service;

import com.blogcode.microservices.course.dto.CourseDto;
import com.blogcode.microservices.course.dto.CourseRequest;
import java.util.List;

/**
 * Business-logic contract for managing courses.
 *
 * <p><strong>Why this class exists:</strong> as in module 5, controllers
 * depend on this interface rather than a concrete implementation class,
 * keeping the available business operations easy to read in one place and
 * the implementation swappable/mockable.
 */
public interface CourseService {

    /**
     * Creates a new course.
     *
     * @param request validated course data supplied by the client
     * @return the created course as a read-model DTO
     */
    CourseDto create(CourseRequest request);

    /**
     * Retrieves a single course by id.
     *
     * @param id the course id
     * @return the matching course as a read-model DTO
     * @throws com.blogcode.microservices.course.exception.ResourceNotFoundException if no course has this id
     */
    CourseDto getById(Long id);

    /**
     * Retrieves every course.
     *
     * @return all courses as read-model DTOs
     */
    List<CourseDto> getAll();

    /**
     * Updates an existing course.
     *
     * @param id      the course id to update
     * @param request validated new course data
     * @return the updated course as a read-model DTO
     * @throws com.blogcode.microservices.course.exception.ResourceNotFoundException if no course has this id
     */
    CourseDto update(Long id, CourseRequest request);

    /**
     * Deletes a course by id.
     *
     * @param id the course id to delete
     * @throws com.blogcode.microservices.course.exception.ResourceNotFoundException if no course has this id
     */
    void delete(Long id);
}
