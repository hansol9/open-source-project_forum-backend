package com.hansol.forum.presentation.controller;

import com.hansol.forum.domain.model.Like;
import com.hansol.forum.domain.model.Post;
import com.hansol.forum.domain.model.Role;
import com.hansol.forum.domain.model.User;
import com.hansol.forum.domain.repository.CommentRepository;
import com.hansol.forum.domain.repository.LikeRepository;
import com.hansol.forum.domain.repository.PostRepository;
import com.hansol.forum.domain.repository.UserRepository;
import com.hansol.forum.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String token;
    private User testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole(Role.USER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser = userRepository.saveAndFlush(testUser);

        testPost = new Post();
        testPost.setTitle("Test Post");
        testPost.setContent("Content");
        testPost.setUserId(testUser.getId());
        testPost.setCreatedAt(LocalDateTime.now());
        testPost = postRepository.saveAndFlush(testPost);

        token = jwtTokenProvider.generateToken("testuser", "USER");
    }

    @AfterEach
    void tearDown() {
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void likePost_Success() throws Exception {
        mockMvc.perform(post("/api/posts/" + testPost.getId() + "/likes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post liked successfully"));
    }

    @Test
    void unlikePost_Success() throws Exception {
        Like like = new Like();
        like.setUserId(testUser.getId());
        like.setPostId(testPost.getId());
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.saveAndFlush(like);

        mockMvc.perform(delete("/api/posts/" + testPost.getId() + "/likes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void getLikeCount_Success() throws Exception {
        mockMvc.perform(get("/api/posts/" + testPost.getId() + "/likes/count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeCount").value(0));
    }

    @Test
    void getLikeStatus_Success() throws Exception {
        mockMvc.perform(get("/api/posts/" + testPost.getId() + "/likes/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false));
    }
}
