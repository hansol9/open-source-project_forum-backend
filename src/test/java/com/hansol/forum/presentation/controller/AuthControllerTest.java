package com.hansol.forum.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansol.forum.application.dto.LoginRequest;
import com.hansol.forum.application.dto.RegisterRequest;
import com.hansol.forum.domain.model.Role;
import com.hansol.forum.domain.model.User;
import com.hansol.forum.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void register_DuplicateUsername_ReturnsBadRequest() throws Exception {
        User existing = new User();
        existing.setUsername("existing");
        existing.setEmail("existing@example.com");
        existing.setPassword(passwordEncoder.encode("password"));
        existing.setRole(Role.USER);
        existing.setCreatedAt(LocalDateTime.now());
        userRepository.save(existing);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_Success() throws Exception {
        User user = new User();
        user.setUsername("loginuser");
        user.setEmail("login@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("loginuser");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("loginuser"))
                .andExpect(jsonPath("$.role").value("USER"));

    }

    @Test
    void login_WrongPassword_ReturnsBadRequest() throws Exception {
        User user = new User();
        user.setUsername("loginuser2");
        user.setEmail("login2@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("loginuser2");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_UserNotFound_ReturnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
