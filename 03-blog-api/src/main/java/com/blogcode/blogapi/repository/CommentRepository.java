package com.blogcode.blogapi.repository;

import com.blogcode.blogapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Comment} entities.
 *
 * <p>In addition to the standard CRUD methods inherited from {@link JpaRepository}, this
 * interface declares a <b>derived query method</b>: Spring Data parses the method name
 * {@code findByPostIdOrderByCreatedAtAsc} at startup and generates the equivalent JPQL
 * query automatically (roughly:
 * {@code select c from Comment c where c.post.id = :postId order by c.createdAt asc}).
 * This is the idiomatic Spring Data way to write simple, readable queries without writing
 * any SQL or JPQL by hand.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Finds every comment belonging to the given post, oldest first, so a post's comment
     * thread reads top-to-bottom in chronological order.
     *
     * @param postId the identifier of the parent post
     * @return the post's comments ordered by creation time ascending
     */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
