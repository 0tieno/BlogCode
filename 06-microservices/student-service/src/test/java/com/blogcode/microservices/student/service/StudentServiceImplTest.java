package com.blogcode.microservices.student.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.blogcode.microservices.student.client.CourseClient;
import com.blogcode.microservices.student.domain.Student;
import com.blogcode.microservices.student.dto.CourseDto;
import com.blogcode.microservices.student.dto.StudentDto;
import com.blogcode.microservices.student.dto.StudentRequest;
import com.blogcode.microservices.student.repository.StudentRepository;
import com.blogcode.microservices.student.service.impl.StudentServiceImpl;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Plain Mockito-based unit test for {@link StudentServiceImpl}, verifying
 * that student lookups correctly enrich the response with course details
 * obtained through the (mocked) {@link CourseClient} - without ever making
 * a real HTTP call or requiring Eureka/course-service to be running.
 */
class StudentServiceImplTest {

    private StudentRepository studentRepository;
    private CourseClient courseClient;
    private StudentService studentService;

    /**
     * Rebuilds fresh mocks and a fresh {@link StudentServiceImpl} before
     * every test method.
     */
    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        courseClient = mock(CourseClient.class);
        studentService = new StudentServiceImpl(studentRepository, courseClient);
    }

    /**
     * Verifies that fetching a student who is enrolled in a course calls
     * {@link CourseClient} and includes the resolved course details in the
     * response DTO.
     */
    @Test
    void getById_enrichesResponseWithCourseDetails_whenStudentIsEnrolled() {
        Student student = Student.builder()
                .id(1L)
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada@example.com")
                .enrolledCourseId(10L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseClient.getCourseById(10L))
                .thenReturn(new CourseDto(10L, "Algorithms", "Dr. Turing", 4, Instant.now()));

        StudentDto result = studentService.getById(1L);

        assertThat(result.firstName()).isEqualTo("Ada");
        assertThat(result.enrolledCourse()).isNotNull();
        assertThat(result.enrolledCourse().title()).isEqualTo("Algorithms");
        verify(courseClient).getCourseById(10L);
    }

    /**
     * Verifies that fetching a student who is not enrolled in any course
     * never calls {@link CourseClient} at all, avoiding an unnecessary
     * network round trip.
     */
    @Test
    void getById_skipsCourseLookup_whenStudentIsNotEnrolled() {
        Student student = Student.builder()
                .id(2L)
                .firstName("Grace")
                .lastName("Hopper")
                .email("grace@example.com")
                .enrolledCourseId(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student));

        StudentDto result = studentService.getById(2L);

        assertThat(result.enrolledCourse()).isNull();
        verifyNoInteractions(courseClient);
    }
}
