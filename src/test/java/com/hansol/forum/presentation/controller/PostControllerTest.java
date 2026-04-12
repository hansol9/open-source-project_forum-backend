package com.hansol.forum.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansol.forum.application.dto.PostRequest;
import com.hansol.forum.domain.model.Post;
import com.hansol.forum.domain.model.Role;
import com.hansol.forum.domain.model.User;
import com.hansol.forum.domain.repository.PostRepository;
import com.hansol.forum.domain.repository.UserRepository;
import com.hansol.forum.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;
    private User testUser;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole(Role.USER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser = userRepository.save(testUser);

        token = jwtTokenProvider.generateToken("testuser", "USER");
    }

    @Test
    void createPost_Success() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Test Post");
        request.setContent("Test Content");

        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Post"));
    }

    @Test
    void getAllPosts_Success() throws Exception {
        Post post = new Post();
        post.setTitle("Existing Post");
        post.setContent("Content");
        post.setUserId(testUser.getId());
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        mockMvc.perform(get("/api/posts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Existing Post"));
    }

    @Test
    void getPostById_Success() throws Exception {
        Post post = new Post();
        post.setTitle("Find Me");
        post.setContent("Content");
        post.setUserId(testUser.getId());
        post.setCreatedAt(LocalDateTime.now());
        post = postRepository.save(post);

        mockMvc.perform(get("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Find Me"));
    }

    @Test
    void getPostById_NotFound() throws Exception {
        mockMvc.perform(get("/api/posts/999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePost_Success() throws Exception {
        Post post = new Post();
        post.setTitle("Old Title");
        post.setContent("Old Content");
        post.setUserId(testUser.getId());
        post.setCreatedAt(LocalDateTime.now());
        post = postRepository.save(post);

        PostRequest request = new PostRequest();
        request.setContent("New Title");
        request.setContent("New Content");

        mockMvc.perform(put("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void deletePost_Success() throws Exception {
        Post post = new Post();
        post.setTitle("Delete Me");
        post.setContent("Content");
        post.setUserId(testUser.getId());
        post.setCreatedAt(LocalDateTime.now());
        post = postRepository.save(post);

        mockMvc.perform(delete("/api/posts/" + post.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void createPost_Unauthorized() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("Test");
        request.setContent("Content");

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
