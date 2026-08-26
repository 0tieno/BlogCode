package com.blogcode.blogapi.repository;

import com.blogcode.blogapi.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Post} entities.
 *
 * <p>Extending {@link JpaRepository} gives this interface a full set of CRUD and pagination
 * operations (e.g. {@code findAll(Pageable)}, {@code save}, {@code deleteById}) without a
 * single line of implementation code - Spring Data generates a concrete implementation at
 * runtime by inspecting the interface's generic type parameters ({@code Post}, {@code Long}).
 *
 * <p>No custom query methods are needed yet because the "list all posts, paged and sorted"
 * requirement is satisfied entirely by the inherited {@code findAll(Pageable)} method.
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
