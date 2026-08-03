package com.blogcode.studentcrudapi.mapper;

import com.blogcode.studentcrudapi.dto.StudentRequest;
import com.blogcode.studentcrudapi.dto.StudentResponse;
import com.blogcode.studentcrudapi.entity.Student;

/**
 * Utility class that converts between the {@link Student} JPA entity and the
 * {@link StudentRequest}/{@link StudentResponse} DTOs.
 *
 * <p>Centralising this translation logic in one place (rather than scattering
 * {@code new Student(...)} or {@code new StudentResponse(...)} calls across
 * the service layer) keeps the mapping rules easy to find, easy to unit test
 * in isolation, and easy to change if the entity/DTO shapes ever diverge
 * further (e.g. if {@code StudentResponse} later added a computed field that
 * has no direct entity counterpart).
 *
 * <p>Declared as a {@code final} class with a private constructor and only
 * {@code static} methods because it is a pure, stateless collection of
 * conversion functions - there is no reason to ever instantiate it, and
 * marking it {@code final}/non-instantiable communicates that clearly to
 * other developers.
 */
public final class StudentMapper {

    /**
     * Prevents instantiation; this class only exposes static utility
     * methods and holds no state of its own.
     */
    private StudentMapper() {
    }

    /**
     * Converts an incoming {@link StudentRequest} DTO into a new,
     * transient (not-yet-persisted) {@link Student} entity.
     *
     * <p>The entity's {@code id} is intentionally left {@code null} here -
     * it is populated by the database (via {@code GenerationType.IDENTITY})
     * only after the entity is saved through {@code StudentRepository}.
     *
     * @param request the validated request DTO submitted by a client.
     * @return a new {@link Student} entity populated from {@code request},
     *         ready to be passed to {@code StudentRepository.save(..)}.
     */
    public static Student toEntity(StudentRequest request) {
        return Student.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .course(request.course())
                .age(request.age())
                .build();
    }

    /**
     * Copies the fields of a {@link StudentRequest} onto an existing,
     * already-persisted {@link Student} entity, used when handling update
     * ({@code PUT}) requests.
     *
     * <p>Mutating the managed entity in place (rather than building a brand
     * new one) means Hibernate's dirty-checking mechanism will detect the
     * changed fields and issue an {@code UPDATE} statement automatically
     * when the surrounding transaction commits - no explicit {@code save}
     * call is even required, though the service layer calls it anyway for
     * clarity.
     *
     * @param existing the managed entity loaded from the database, whose
     *                 fields will be overwritten.
     * @param request  the validated request DTO containing the new values.
     */
    public static void updateEntity(Student existing, StudentRequest request) {
        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setEmail(request.email());
        existing.setCourse(request.course());
        existing.setAge(request.age());
    }

    /**
     * Converts a persisted {@link Student} entity into the
     * {@link StudentResponse} DTO returned to API clients.
     *
     * @param student the entity loaded from (or just saved to) the
     *                database.
     * @return a {@link StudentResponse} exposing only the fields relevant to
     *         API consumers.
     */
    public static StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getCourse(),
                student.getAge()
        );
    }
}
