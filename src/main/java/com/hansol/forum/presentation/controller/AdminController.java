package com.hansol.forum.presentation.controller;

import com.hansol.forum.application.service.CommentService;
import com.hansol.forum.application.service.PostService;
import com.hansol.forum.application.service.UserService;
import com.hansol.forum.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/users")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        long totalUsers = userService.countUsers();
        long totalPosts = postService.countPosts();
        long totalComments = commentService.countComments();

        return ResponseEntity.ok(
                Map.of(
                        "totalUsers", totalUsers,
                        "totalPosts", totalPosts,
                        "totalComments", totalComments
                )
        );
    }
}
