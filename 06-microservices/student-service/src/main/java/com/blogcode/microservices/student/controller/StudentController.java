package com.blogcode.microservices.student.controller;

import com.blogcode.microservices.student.dto.StudentDto;
import com.blogcode.microservices.student.dto.StudentRequest;
import com.blogcode.microservices.student.service.StudentService;
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
 * REST controller exposing CRUD endpoints for students.
 *
 * <p><strong>Why this class exists:</strong> the base path
 * {@code /api/students} is exactly what {@code api-gateway}'s
 * {@code Path=/api/students/**} route predicate matches on, so external
 * clients reach this controller indirectly through the gateway on port
 * 8080, never by talking to this service's own port (8081) directly in a
 * real deployment.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * Creates a new student.
     *
     * @param request validated student payload
     * @return {@code 201 Created} with the new student in the response body
     */
    @PostMapping
    public ResponseEntity<StudentDto> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(request));
    }

    /**
     * Lists every student, each enriched with live course details.
     *
     * @return {@code 200 OK} with the full student list
     */
    @GetMapping
    public ResponseEntity<List<StudentDto>> getAll() {
        return ResponseEntity.ok(studentService.getAll());
    }

    /**
     * Retrieves a single student by id.
     *
     * @param id the student id, taken from the URL path
     * @return {@code 200 OK} with the matching student
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    /**
     * Updates an existing student.
     *
     * @param id      the student id, taken from the URL path
     * @param request validated new student payload
     * @return {@code 200 OK} with the updated student
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.update(id, request));
    }

    /**
     * Deletes a student by id.
     *
     * @param id the student id, taken from the URL path
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
