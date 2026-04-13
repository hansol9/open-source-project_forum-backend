package com.hansol.forum.presentation.controller;

import com.hansol.forum.application.dto.CommentRequest;
import com.hansol.forum.application.service.CommentService;
import com.hansol.forum.application.service.UserService;
import com.hansol.forum.domain.model.Comment;
import com.hansol.forum.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<Comment> create(@PathVariable Long postId,
                                          @RequestBody CommentRequest request) {
        User user = userService.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = commentService.create(request, postId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> findByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.findByPostId(postId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> update(@PathVariable Long postId,
                                          @PathVariable Long commentId,
                                          @RequestBody CommentRequest request) {
        User user = userService.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = commentService.update(commentId, request, user.getId());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId,
                                       @PathVariable Long commentId) {
        User user = userService.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        commentService.delete(commentId, user.getId());
        return ResponseEntity.noContent().build();
    }
}