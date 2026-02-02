package com.trainerworkload.infrastructure.security.service.impl;

import com.trainerworkload.infrastructure.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.List;

@Service
@Profile("security")
public class JwtServiceImpl implements JwtService {
    private final JwtParser parser;

    public JwtServiceImpl(@Value("${security.jwt.secret}") String secret) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            parser.parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<String> getRoles(String token) {
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims.get("roles", List.class);
    }
}
