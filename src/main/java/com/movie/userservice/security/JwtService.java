package com.movie.userservice.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(toSecureKey(secret));
        this.expirationMs = expirationMs;
    }

    private byte[] toSecureKey(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts
            .builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(secretKey)
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        Claims claims = extractClaims(token);
        Object userId = claims.get("userId");
        return userId != null ? userId.toString() : null;
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        Claims claims = extractClaims(token);
        String username = claims.getSubject();
        Date expiration = claims.getExpiration();
        return username.equals(expectedUsername) && expiration.after(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts
            .parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
