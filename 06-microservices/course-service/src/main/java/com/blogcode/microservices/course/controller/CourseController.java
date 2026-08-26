package com.blogcode.microservices.course.controller;

import com.blogcode.microservices.course.dto.CourseDto;
import com.blogcode.microservices.course.dto.CourseRequest;
import com.blogcode.microservices.course.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing CRUD endpoints for courses.
 *
 * <p><strong>Why this class exists:</strong> this is the only HTTP surface
 * through which any other part of the system (including
 * {@code student-service}'s Feign client and the {@code api-gateway}) may
 * read or write course data. The base path {@code /api/courses} is exactly
 * what {@code api-gateway}'s {@code Path=/api/courses/**} route predicate
 * matches on.
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Creates a new course.
     *
     * @param request validated course payload
     * @return {@code 201 Created} with the new course in the response body
     */
    @PostMapping
    public ResponseEntity<CourseDto> create(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(request));
    }

    /**
     * Lists every course.
     *
     * @return {@code 200 OK} with the full course list
     */
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAll() {
        return ResponseEntity.ok(courseService.getAll());
    }

    /**
     * Retrieves a single course by id.
     *
     * @param id the course id, taken from the URL path
     * @return {@code 200 OK} with the matching course
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    /**
     * Updates an existing course.
     *
     * @param id      the course id, taken from the URL path
     * @param request validated new course payload
     * @return {@code 200 OK} with the updated course
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> update(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(courseService.update(id, request));
    }

    /**
     * Deletes a course by id.
     *
     * @param id the course id, taken from the URL path
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
