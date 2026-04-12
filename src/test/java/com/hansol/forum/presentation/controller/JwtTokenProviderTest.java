package com.hansol.forum.presentation.controller;

import com.hansol.forum.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "myForumSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm2025",
                86400000L
        );
    }

    @Test
    void generateToken_Success() throws Exception {
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUsername_Success() throws Exception {
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        String username = jwtTokenProvider.getUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void getRole_Success() throws Exception {
        String token = jwtTokenProvider.generateToken("testuser", "ADMIN");

        String role = jwtTokenProvider.getRole(token);

        assertEquals("ADMIN", role);
    }

    @Test
    void validateToken_ValidToken() throws Exception {
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken() throws Exception {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void validateToken_NullToken() throws Exception {
        assertFalse(jwtTokenProvider.validateToken(null));
    }
}
