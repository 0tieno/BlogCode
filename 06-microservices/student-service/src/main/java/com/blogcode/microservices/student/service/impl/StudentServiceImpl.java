package com.blogcode.microservices.student.service.impl;

import com.blogcode.microservices.student.client.CourseClient;
import com.blogcode.microservices.student.domain.Student;
import com.blogcode.microservices.student.dto.CourseDto;
import com.blogcode.microservices.student.dto.StudentDto;
import com.blogcode.microservices.student.dto.StudentRequest;
import com.blogcode.microservices.student.exception.ResourceNotFoundException;
import com.blogcode.microservices.student.repository.StudentRepository;
import com.blogcode.microservices.student.service.StudentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link StudentService} implementation backed by
 * {@link StudentRepository} and {@link CourseClient}.
 *
 * <p><strong>Why this class exists:</strong> this is where the
 * inter-service communication pattern of this curriculum module comes
 * together. Persisting a {@link Student} is a purely local database
 * operation, but rendering a complete {@link StudentDto} also requires
 * asking a <em>different</em> service (course-service) for course details
 * over the network - a fundamentally different cost/failure profile than a
 * local JPA relationship (module 5's {@code Order}/{@code Product}). This
 * class isolates that complexity so {@code StudentController} stays a thin
 * HTTP adapter.
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseClient courseClient;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public StudentDto create(StudentRequest request) {
        Student student = Student.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .enrolledCourseId(request.enrolledCourseId())
                .build();
        return toDto(studentRepository.save(student));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public StudentDto getById(Long id) {
        return toDto(findEntityById(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getAll() {
        return studentRepository.findAll().stream().map(this::toDto).toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public StudentDto update(Long id, StudentRequest request) {
        Student student = findEntityById(id);
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email());
        student.setEnrolledCourseId(request.enrolledCourseId());
        // Dirty checking flushes this UPDATE automatically at commit time.
        return toDto(student);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(Long id) {
        Student student = findEntityById(id);
        studentRepository.delete(student);
    }

    /**
     * Shared lookup helper that centralizes the "find or throw 404" pattern.
     *
     * @param id the student id to look up
     * @return the managed {@link Student} entity
     * @throws ResourceNotFoundException if no student has this id
     */
    private Student findEntityById(Long id) {
        return studentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    /**
     * Converts a {@link Student} entity into its read-model
     * {@link StudentDto}, resolving the enrolled course's details via
     * {@link CourseClient} when the student has enrolled in one.
     *
     * <p>The Feign call is wrapped by course-service's own not-found
     * behaviour and {@code CourseClientFallback}: if the course id is
     * invalid or course-service is unreachable, a degraded-but-non-fatal
     * {@code CourseDto} placeholder is returned instead of failing the
     * whole student lookup.
     *
     * @param student the entity to convert
     * @return an equivalent, detached {@link StudentDto}
     */
    private StudentDto toDto(Student student) {
        CourseDto enrolledCourse = null;
        if (student.getEnrolledCourseId() != null) {
            enrolledCourse = courseClient.getCourseById(student.getEnrolledCourseId());
        }
        return new StudentDto(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getEnrolledCourseId(),
                enrolledCourse,
                student.getCreatedAt(),
                student.getUpdatedAt());
    }
}
