package com.blogcode.blogapi.controller;

import com.blogcode.blogapi.dto.CommentRequest;
import com.blogcode.blogapi.dto.CommentResponse;
import com.blogcode.blogapi.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing endpoints for comments nested under a specific post, i.e.
 * {@code /api/v1/posts/{postId}/comments}.
 *
 * <p>Nesting this controller's mapping under the parent post's id is a deliberate REST
 * design choice: it makes the URL itself communicate the "a comment always belongs to a
 * post" relationship, and it means {@link CommentService} never has to guess which post
 * a comment is being added to or deleted from.
 */
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Lists every comment on a given post, oldest first.
     *
     * @param postId the identifier of the parent post, taken from the URL path
     * @return {@code 200 OK} with the post's comments as a list of {@link CommentResponse}
     */
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    /**
     * Adds a new comment to a post.
     *
     * @param postId  the identifier of the post being commented on, taken from the URL path
     * @param request the validated request body describing the new comment
     * @return {@code 201 Created} with the newly created {@link CommentResponse}
     */
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long postId,
                                                       @Valid @RequestBody CommentRequest request) {
        CommentResponse created = commentService.addComment(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Deletes a single comment from a post.
     *
     * @param postId    the identifier of the parent post, taken from the URL path
     * @param commentId the identifier of the comment to delete, taken from the URL path
     * @return {@code 204 No Content} to signal successful deletion with no response body
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
