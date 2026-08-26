package com.blogcode.ecommerce.repository;

import com.blogcode.ecommerce.domain.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Category} entities.
 *
 * <p><strong>Why this class exists:</strong> extending {@link JpaRepository}
 * gives us full CRUD (find, save, delete) and pagination support with zero
 * implementation code - Spring generates a proxy implementation at runtime.
 * This is one of the first "magic" moments beginners encounter in Spring
 * Data, so this interface intentionally stays minimal to keep that lesson
 * clear.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Looks up a category by its unique, case-sensitive name.
     *
     * <p>Spring Data derives the SQL for this method entirely from its
     * name ("derived query methods"): {@code findBy} + {@code Name} is
     * translated to {@code WHERE name = ?}. No {@code @Query} annotation or
     * hand-written SQL is required.
     *
     * @param name the category name to search for
     * @return an {@link Optional} containing the match, or empty if none exists
     */
    Optional<Category> findByName(String name);

    /**
     * Checks whether a category with the given name already exists, used
     * by the service layer to reject duplicate category names before
     * hitting the database's unique constraint.
     *
     * @param name the category name to check
     * @return true if a category with this name already exists
     */
    boolean existsByName(String name);
}
