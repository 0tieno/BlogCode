package com.blogcode.blogapi.service.impl;

import com.blogcode.blogapi.dto.CommentRequest;
import com.blogcode.blogapi.dto.CommentResponse;
import com.blogcode.blogapi.entity.Comment;
import com.blogcode.blogapi.entity.Post;
import com.blogcode.blogapi.exception.ResourceNotFoundException;
import com.blogcode.blogapi.repository.CommentRepository;
import com.blogcode.blogapi.repository.PostRepository;
import com.blogcode.blogapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link CommentService}.
 *
 * <p>Depends on both {@link PostRepository} and {@link CommentRepository} because adding
 * or removing a comment always requires first confirming its parent post exists - a good
 * illustration for beginners that a service can, and often should, coordinate more than
 * one repository to enforce a business rule (here: "comments cannot exist without a post").
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        ensurePostExists(postId);
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(CommentResponse::fromEntity)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Uses {@link Post#addComment(Comment)} rather than saving the comment directly
     * through {@link CommentRepository}, so both sides of the bidirectional association
     * stay in sync in memory, and the cascading defined on {@code Post.comments} persists
     * the new comment automatically when the post is saved.
     */
    @Override
    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Comment comment = Comment.builder()
                .author(request.author())
                .content(request.content())
                .build();

        post.addComment(comment);
        postRepository.save(post);

        return CommentResponse.fromEntity(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        ensurePostExists(postId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getPost().getId().equals(postId)) {
            throw new ResourceNotFoundException(
                    "Comment with id " + commentId + " does not belong to post with id " + postId);
        }

        commentRepository.delete(comment);
    }

    /**
     * Confirms a post with the given id exists, without loading its full entity graph,
     * used by read-only comment operations that only need to validate the parent exists.
     *
     * @param postId the post identifier to check
     * @throws ResourceNotFoundException if no post with the given id exists
     */
    private void ensurePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
    }
}
