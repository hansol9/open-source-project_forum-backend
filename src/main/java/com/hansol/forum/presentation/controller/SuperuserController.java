package com.hansol.forum.presentation.controller;

import com.hansol.forum.application.dto.PostRequest;
import com.hansol.forum.domain.model.Post;
import com.hansol.forum.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/superuser/posts")
@PreAuthorize("hasRole('SUPERUSER')")
@RequiredArgsConstructor
public class SuperuserController {

    private final PostRepository postRepository;

    @PutMapping("/{id}")
    public ResponseEntity<Post> editPost(@PathVariable Long id,
                                         @RequestBody PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(postRepository.save(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        postRepository.delete(post);
        return ResponseEntity.noContent().build();
    }
}
