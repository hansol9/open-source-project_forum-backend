package com.hansol.forum.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansol.forum.application.dto.CommentRequest;
import com.hansol.forum.domain.model.Comment;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

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
    void createComment_Success() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setContent("Test Comment");

        mockMvc.perform(post("/api/posts/" + testPost.getId() + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test Comment"));
    }

    @Test
    void getCommentsByPostId_Success() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Existing Comment");
        comment.setUserId(testUser.getId());
        comment.setPostId(testPost.getId());
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.saveAndFlush(comment);

        mockMvc.perform(get("/api/posts/" + testPost.getId() + "/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Existing Comment"));
    }

    @Test
    void updateComment_Success() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Old Comment");
        comment.setUserId(testUser.getId());
        comment.setPostId(testPost.getId());
        comment.setCreatedAt(LocalDateTime.now());
        comment = commentRepository.saveAndFlush(comment);

        CommentRequest request = new CommentRequest();
        request.setContent("Updated Comment");

        mockMvc.perform(put("/api/posts/" + testPost.getId() + "/comments/" + comment.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated Comment"));
    }

    @Test
    void deleteComment_Success() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Delete Me");
        comment.setUserId(testUser.getId());
        comment.setPostId(testPost.getId());
        comment.setCreatedAt(LocalDateTime.now());
        comment = commentRepository.saveAndFlush(comment);

        mockMvc.perform(delete("/api/posts/" + testPost.getId() + "/comments/" + comment.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
