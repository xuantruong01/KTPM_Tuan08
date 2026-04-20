package com.movie.userservice.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expirationMs
    ) {
        // Sử dụng trực tiếp secret key, không hash
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        
        logger.info("=== JWT Service Initialized ===");
        logger.info("Secret: {}", secret);
        logger.info("Expiration: {} ms", expirationMs);
        logger.info("===============================");
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        String token = Jwts
            .builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(secretKey)
            .compact();
        
        logger.debug("Token generated for subject: {}", subject);
        
        return token;
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
        try {
            logger.debug("Verifying token signature...");
            
            Claims claims = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            logger.debug("Token verified successfully");
            
            return claims;
        } catch (Exception e) {
            logger.error("Token verification failed: {}", e.getMessage());
            throw e;
        }
    }
}
