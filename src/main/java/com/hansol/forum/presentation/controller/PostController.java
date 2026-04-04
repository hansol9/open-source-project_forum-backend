package com.hansol.forum.presentation.controller;

import com.hansol.forum.application.dto.PostRequest;
import com.hansol.forum.application.service.PostService;
import com.hansol.forum.application.service.UserService;
import com.hansol.forum.domain.model.Post;
import com.hansol.forum.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Post> create(@RequestBody PostRequest request,
                                       @AuthenticationPrincipal String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postService.create(request, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @GetMapping
    public ResponseEntity<List<Post>> findAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> findById(@PathVariable Long id) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id,
                                       @RequestBody PostRequest request,
                                       @AuthenticationPrincipal String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postService.update(id, request, user.getId());

        return ResponseEntity.ok(post);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        postService.delete(id, user.getId());

        return ResponseEntity.noContent().build();
    }
}
