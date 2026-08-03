package com.blogcode.studentcrudapi.repository;

import com.blogcode.studentcrudapi.entity.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Student} entities.
 *
 * <p>Extending {@link JpaRepository} gives us a full set of CRUD methods
 * (save, findById, findAll, deleteById, etc.) for free - Spring Data
 * generates the implementation of this interface at runtime via a dynamic
 * proxy, so no hand-written implementation class exists (or is needed).
 * This is the "repository pattern": it abstracts away persistence details
 * behind a simple, collection-like interface so the service layer never has
 * to write SQL or JDBC code directly for standard operations.
 *
 * <p>{@link Repository} is technically redundant here (Spring Data detects
 * repository interfaces automatically because they extend
 * {@code JpaRepository}), but it is added explicitly to make the
 * architectural role of this interface unambiguous to readers.
 *
 * <p>The two custom query methods below demonstrate the two main ways
 * Spring Data lets you add query logic beyond the built-in CRUD methods:
 * <b>derived query methods</b> (method name parsed into a query) and
 * <b>{@code @Query} with JPQL</b> (an explicit query string).
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Finds every student whose course matches exactly (case-sensitive).
     *
     * <p>This is a "derived query method": Spring Data parses the method
     * name at startup ({@code findBy} + {@code Course}) and generates the
     * equivalent JPQL query automatically - no implementation or annotation
     * required. It is the idiomatic Spring Data way to express simple,
     * single-condition lookups.
     *
     * @param course the exact course name to filter by, e.g.
     *               {@code "Computer Science"}.
     * @return every {@link Student} enrolled in {@code course}; an empty
     *         list if none match.
     */
    List<Student> findByCourse(String course);

    /**
     * Searches for students whose first or last name contains the given
     * keyword, ignoring case.
     *
     * <p>Unlike {@link #findByCourse(String)}, this query is expressed
     * explicitly with {@link Query} and JPQL because it needs to match
     * against two different columns with a case-insensitive "contains"
     * comparison - a derived method name for this would become unwieldy
     * ({@code findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase}).
     * Writing the JPQL out explicitly keeps the query readable and gives
     * full control over how the {@code keyword} parameter is used.
     *
     * @param keyword the case-insensitive substring to search for within
     *                either the first name or last name.
     * @return every {@link Student} whose first or last name contains
     *         {@code keyword}; an empty list if none match.
     */
    @Query("""
            SELECT s FROM Student s
            WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Student> searchByKeyword(@Param("keyword") String keyword);
}
