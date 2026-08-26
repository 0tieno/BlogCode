package com.blogcode.blogapi.service;

import com.blogcode.blogapi.dto.CommentRequest;
import com.blogcode.blogapi.dto.CommentResponse;

import java.util.List;

/**
 * Service-layer contract for managing comments attached to blog posts.
 *
 * <p>Kept as a separate service from {@link PostService} (rather than folding comment
 * operations into the post service) because comments have their own lifecycle rules
 * (they always belong to a post, but are added/removed independently), which is a useful
 * "single responsibility" boundary for students to see modelled explicitly.
 */
public interface CommentService {

    /**
     * Retrieves every comment belonging to a post, oldest first.
     *
     * @param postId the identifier of the parent post
     * @return the post's comments as {@link CommentResponse} DTOs
     * @throws com.blogcode.blogapi.exception.ResourceNotFoundException if no post with
     *         the given id exists
     */
    List<CommentResponse> getCommentsByPostId(Long postId);

    /**
     * Adds a new comment to an existing post.
     *
     * @param postId  the identifier of the post being commented on
     * @param request validated request payload describing the new comment
     * @return the newly created comment as a {@link CommentResponse}
     * @throws com.blogcode.blogapi.exception.ResourceNotFoundException if no post with
     *         the given id exists
     */
    CommentResponse addComment(Long postId, CommentRequest request);

    /**
     * Deletes a single comment from a post.
     *
     * @param postId    the identifier of the post the comment should belong to
     * @param commentId the identifier of the comment to delete
     * @throws com.blogcode.blogapi.exception.ResourceNotFoundException if the post does
     *         not exist, the comment does not exist, or the comment does not belong to
     *         the given post
     */
    void deleteComment(Long postId, Long commentId);
}
