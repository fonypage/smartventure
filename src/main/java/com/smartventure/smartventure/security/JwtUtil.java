package com.smartventure.smartventure.security;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Ваш секрет в виде UTF-8, не Base64
    private static final String SECRET = "very_secret_key_123_very_secret_key_123";

    private SecretKey key;

    @PostConstruct
    public void init() {
        // Генерируем HMAC-SHA256-ключ из байтов UTF-8
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /** Генерирует токен с subject=username, срок жизни 24 часа */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 3600_000))
                .signWith(key) // подпись будет HS256 по умолчанию
                .compact();
    }

    /** Извлекает username из валидного токена */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /** Проверяет подпись и срок жизни */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
