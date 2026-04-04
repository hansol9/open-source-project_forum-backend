package com.hansol.forum.presentation.controller;

import com.hansol.forum.application.dto.LoginRequest;
import com.hansol.forum.application.dto.RegisterRequest;
import com.hansol.forum.application.service.UserService;
import com.hansol.forum.domain.model.User;
import com.hansol.forum.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "User registered successfully",
                        "username", user.getUsername()
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "username", user.getUsername(),
                        "role", user.getRole().name()
                )
        );
    }
}
