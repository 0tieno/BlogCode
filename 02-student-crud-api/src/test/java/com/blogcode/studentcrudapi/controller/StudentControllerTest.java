package com.blogcode.studentcrudapi.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogcode.studentcrudapi.dto.StudentRequest;
import com.blogcode.studentcrudapi.dto.StudentResponse;
import com.blogcode.studentcrudapi.exception.ResourceNotFoundException;
import com.blogcode.studentcrudapi.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webmvc.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web-layer test for {@link StudentController}.
 *
 * <p>As in {@code GreetingControllerTest} from the 01-hello-spring project,
 * {@link WebMvcTest} boots only the Spring MVC infrastructure required to
 * exercise this single controller, and {@link StudentService} is replaced
 * with a Mockito mock via {@link MockitoBean}. This keeps the test focused
 * purely on the HTTP contract - request mapping, status codes, JSON
 * (de)serialization and validation - without touching a real database.
 */
@WebMvcTest(StudentController.class)
class StudentControllerTest {

    /** Drives simulated HTTP requests against the controller under test. */
    @Autowired
    private MockMvc mockMvc;

    /** Used to serialize {@link StudentRequest} bodies for POST/PUT tests. */
    @Autowired
    private ObjectMapper objectMapper;

    /** Mockito mock standing in for the real service layer. */
    @MockitoBean
    private StudentService studentService;

    /**
     * Verifies that {@code GET /api/v1/students} returns the list produced
     * by the service layer with a {@code 200 OK} status.
     */
    @Test
    void getAllStudents_returnsListFromService() throws Exception {
        StudentResponse student = new StudentResponse(1L, "John", "Doe", "john.doe@example.com", "Computer Science", 21);
        when(studentService.getAllStudents()).thenReturn(List.of(student));

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    /**
     * Verifies that {@code GET /api/v1/students/{id}} returns
     * {@code 404 Not Found} (via {@code GlobalExceptionHandler}) when the
     * service layer reports the student does not exist.
     */
    @Test
    void getStudentById_returnsNotFound_whenMissing() throws Exception {
        when(studentService.getStudentById(99L))
                .thenThrow(new ResourceNotFoundException("Student not found with id: 99"));

        mockMvc.perform(get("/api/v1/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    /**
     * Verifies that {@code POST /api/v1/students} rejects an invalid
     * request body (blank first name) with {@code 400 Bad Request} before
     * the service layer is ever invoked.
     */
    @Test
    void createStudent_returnsBadRequest_whenInvalid() throws Exception {
        StudentRequest invalidRequest = new StudentRequest("", "Doe", "not-an-email", "", null);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifies that {@code POST /api/v1/students} accepts a valid request
     * body, delegates to the service layer, and returns
     * {@code 201 Created}.
     */
    @Test
    void createStudent_returnsCreated_whenValid() throws Exception {
        StudentRequest validRequest = new StudentRequest("Ada", "Lovelace", "ada@example.com", "Mathematics", 28);
        StudentResponse created = new StudentResponse(10L, "Ada", "Lovelace", "ada@example.com", "Mathematics", 28);
        when(studentService.createStudent(any(StudentRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)));
    }

    /**
     * Verifies that {@code DELETE /api/v1/students/{id}} returns
     * {@code 204 No Content} and that the service layer's delete method was
     * invoked with the correct id.
     */
    @Test
    void deleteStudent_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/students/5"))
                .andExpect(status().isNoContent());

        verify(studentService).deleteStudent(5L);
    }
}
