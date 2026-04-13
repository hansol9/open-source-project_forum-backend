package com.hansol.forum.presentation.controller;

import com.hansol.forum.application.service.LikeService;
import com.hansol.forum.application.service.UserService;
import com.hansol.forum.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    public ResponseEntity<?> like(@PathVariable Long postId) {
        User user = userService.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        likeService.likePost(postId, user.getId());
        return ResponseEntity.ok(Map.of("message", "Post liked successfully"));
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(@PathVariable Long postId) {
        User user = userService.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        likeService.unlikePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<?> count(@PathVariable Long postId) {
        long count = likeService.countByPostId(postId);
        return ResponseEntity.ok(Map.of("postId", postId, "likeCount", count));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(@PathVariable Long postId) {
        User user = userService.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        boolean liked = likeService.isLikedByUser(postId, user.getId());
        return ResponseEntity.ok(Map.of("postId", postId, "liked", liked));
    }
}
