package com.blogcode.studentcrudapi.dto;

/**
 * Data Transfer Object representing the shape of a student returned to
 * clients from every read/write endpoint in {@code StudentController}.
 *
 * <p>Implemented as a Java {@code record} for the same reasons as
 * {@link StudentRequest}: it is an immutable snapshot of data flowing out of
 * the application, safe to serialize to JSON without risk of later
 * mutation. Keeping {@code StudentResponse} distinct from the
 * {@link com.blogcode.studentcrudapi.entity.Student} entity means the
 * database schema (entity) and the public API contract (response DTO) can
 * evolve independently - a core benefit of the DTO pattern reinforced
 * throughout this curriculum.
 *
 * @param id        the database-generated unique identifier of the student.
 * @param firstName the student's first name.
 * @param lastName  the student's last name.
 * @param email     the student's email address.
 * @param course    the course/program the student is enrolled in.
 * @param age       the student's age in years.
 */
public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String course,
        Integer age
) {
}
