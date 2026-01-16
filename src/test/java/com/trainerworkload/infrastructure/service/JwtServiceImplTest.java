package com.trainerworkload.infrastructure.service;

import com.trainerworkload.infrastructure.security.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl("test-secret-key-test-secret-key-test-secret-key");
    }

    @Test
    void validatesTokenSuccessfully() {
        String token = Jwts.builder()
                .setSubject("service")
                .signWith(Keys.hmacShaKeyFor("test-secret-key-test-secret-key-test-secret-key".getBytes()))
                .compact();

        boolean result = jwtService.isValidToken(token);
        assertTrue(result);
    }

    @Test
    void invalidatesTokenWithInvalidSignature() {
        String token = Jwts.builder()
                .setSubject("user")
                .signWith(Keys.hmacShaKeyFor("different-secret-key-different-secret-key".getBytes()))
                .compact();

        boolean result = jwtService.isValidToken(token);
        assertFalse(result);
    }

    @Test
    void invalidatesMalformedToken() {
        String token = "malformed.token";

        boolean result = jwtService.isValidToken(token);
        assertFalse(result);
    }

    @Test
    void retrievesRolesFromValidToken() {
        String token = Jwts.builder()
                .claim("roles", List.of("ROLE_SERVICE"))
                .signWith(Keys.hmacShaKeyFor("test-secret-key-test-secret-key-test-secret-key".getBytes()))
                .compact();

        List<String> roles = jwtService.getRoles(token);
        assertEquals(List.of("ROLE_SERVICE"), roles);
    }
}

