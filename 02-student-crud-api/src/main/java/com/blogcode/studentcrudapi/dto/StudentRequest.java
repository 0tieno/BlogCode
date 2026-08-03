package com.blogcode.studentcrudapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object representing the payload clients send when creating
 * or updating a student, for both {@code POST /api/v1/students} and
 * {@code PUT /api/v1/students/{id}}.
 *
 * <p>Implemented as a Java {@code record} because a request DTO is, by
 * nature, an immutable snapshot of the data a client sent - once Jackson has
 * deserialized the incoming JSON into a {@code StudentRequest}, nothing
 * should mutate it. Declaring it separately from the {@link
 * com.blogcode.studentcrudapi.entity.Student} JPA entity is a deliberate
 * layering decision (the DTO pattern): it prevents leaking persistence
 * details (JPA annotations, lazy-loaded fields, the database-generated
 * {@code id}) into the public API contract, and it lets validation rules be
 * expressed independently of how the data is stored.
 *
 * <p>Each component is annotated with {@code jakarta.validation} constraints.
 * These are enforced by Spring MVC only when a handler method parameter is
 * additionally annotated with {@code @Valid} (see
 * {@code StudentController}); if any constraint is violated, Spring throws a
 * {@code MethodArgumentNotValidException}, which
 * {@code GlobalExceptionHandler} converts into a clean {@code 400 Bad
 * Request} JSON response instead of a raw stack trace.
 *
 * @param firstName the student's first name; must not be blank.
 * @param lastName  the student's last name; must not be blank.
 * @param email     the student's email address; must not be blank and must
 *                  be a syntactically valid email, per {@link Email}.
 * @param course    the course/program the student is enrolled in; must not
 *                  be blank.
 * @param age       the student's age in years; must be present and within a
 *                  realistic range for a student (5-120) to catch obvious
 *                  data-entry mistakes.
 */
public record StudentRequest(

        @NotBlank(message = "First name must not be blank")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        String lastName,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Course must not be blank")
        String course,

        @NotNull(message = "Age must be provided")
        @Min(value = 5, message = "Age must be at least 5")
        @Max(value = 120, message = "Age must be at most 120")
        Integer age
) {
}
