package com.blogcode.studentcrudapi.service;

import com.blogcode.studentcrudapi.dto.StudentRequest;
import com.blogcode.studentcrudapi.dto.StudentResponse;
import java.util.List;

/**
 * Contract describing every business operation this API supports for
 * managing students.
 *
 * <p>As in the 01-hello-spring project, the controller layer
 * ({@code StudentController}) depends only on this interface, never on
 * {@code StudentServiceImpl} directly. This keeps the controller testable in
 * isolation (by mocking {@code StudentService}), documents the full set of
 * operations the "business layer" promises to support, and allows the
 * implementation to change (e.g. adding caching, or switching persistence
 * technology) without any change to the web layer.
 *
 * <p>Every method here works exclusively with DTOs
 * ({@link StudentRequest}/{@link StudentResponse}), never with the
 * {@code Student} JPA entity directly - reinforcing that persistence details
 * are an implementation concern hidden behind this contract.
 */
public interface StudentService {

    /**
     * Retrieves every student currently stored in the database.
     *
     * @return a list of {@link StudentResponse} DTOs, one per stored
     *         student; an empty list if none exist.
     */
    List<StudentResponse> getAllStudents();

    /**
     * Retrieves a single student by their unique identifier.
     *
     * @param id the database-generated identifier of the student to fetch.
     * @return the matching {@link StudentResponse}.
     * @throws com.blogcode.studentcrudapi.exception.ResourceNotFoundException
     *         if no student exists with the given {@code id}.
     */
    StudentResponse getStudentById(Long id);

    /**
     * Searches for students whose first or last name contains the given
     * keyword (case-insensitive).
     *
     * @param keyword the substring to search for within student names.
     * @return every matching {@link StudentResponse}; an empty list if none
     *         match.
     */
    List<StudentResponse> searchStudents(String keyword);

    /**
     * Retrieves every student enrolled in the given course.
     *
     * @param course the exact course/program name to filter by.
     * @return every matching {@link StudentResponse}; an empty list if none
     *         match.
     */
    List<StudentResponse> getStudentsByCourse(String course);

    /**
     * Creates and persists a new student record.
     *
     * @param request the validated request DTO describing the new student.
     * @return a {@link StudentResponse} representing the newly created
     *         student, including its database-generated {@code id}.
     */
    StudentResponse createStudent(StudentRequest request);

    /**
     * Updates every field of an existing student record.
     *
     * @param id      the identifier of the student to update.
     * @param request the validated request DTO containing the new values.
     * @return a {@link StudentResponse} representing the student after the
     *         update has been applied.
     * @throws com.blogcode.studentcrudapi.exception.ResourceNotFoundException
     *         if no student exists with the given {@code id}.
     */
    StudentResponse updateStudent(Long id, StudentRequest request);

    /**
     * Deletes the student with the given identifier.
     *
     * @param id the identifier of the student to delete.
     * @throws com.blogcode.studentcrudapi.exception.ResourceNotFoundException
     *         if no student exists with the given {@code id}.
     */
    void deleteStudent(Long id);
}
