package com.blogcode.microservices.student.service;

import com.blogcode.microservices.student.dto.StudentDto;
import com.blogcode.microservices.student.dto.StudentRequest;
import java.util.List;

/**
 * Business-logic contract for managing students, including resolving each
 * student's enrolled course details from course-service.
 */
public interface StudentService {

    /**
     * Creates a new student.
     *
     * @param request validated student data supplied by the client
     * @return the created student as a read-model DTO, including resolved course details
     */
    StudentDto create(StudentRequest request);

    /**
     * Retrieves a single student by id, including their enrolled course
     * details fetched live from course-service.
     *
     * @param id the student id
     * @return the matching student as a read-model DTO
     * @throws com.blogcode.microservices.student.exception.ResourceNotFoundException if no student has this id
     */
    StudentDto getById(Long id);

    /**
     * Retrieves every student, each enriched with their enrolled course
     * details.
     *
     * @return all students as read-model DTOs
     */
    List<StudentDto> getAll();

    /**
     * Updates an existing student.
     *
     * @param id      the student id to update
     * @param request validated new student data
     * @return the updated student as a read-model DTO
     * @throws com.blogcode.microservices.student.exception.ResourceNotFoundException if no student has this id
     */
    StudentDto update(Long id, StudentRequest request);

    /**
     * Deletes a student by id.
     *
     * @param id the student id to delete
     * @throws com.blogcode.microservices.student.exception.ResourceNotFoundException if no student has this id
     */
    void delete(Long id);
}
