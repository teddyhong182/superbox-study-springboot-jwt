package com.superbox.study.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtUtils {

    @Value("${superbox.app.jwtSecret}")
    private String jwtSecret;
    @Value("${superbox.app.jwtExpirationSeconds}")
    private int jwtExpirationSeconds;

    public String generateJwtToken(Authentication authentication) {
        // 주요 정보
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateToken(userPrincipal);
    }

    public String generateToken(UserDetailsImpl userPrincipal) {
        String username = userPrincipal.getUsername();
        return generateTokenFromUsername(username);
    }

    public String generateTokenFromUsername(String username) {
        LocalDateTime now = LocalDateTime.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Timestamp.valueOf(now))
                .setExpiration(Timestamp.valueOf(now.plusSeconds(jwtExpirationSeconds)))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature : {} ", e.getLocalizedMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token : {} ", e.getLocalizedMessage());
        } catch (ExpiredJwtException e) {
            // 토큰 만료 시
            log.error("JWT token is expired : {} ", e.getLocalizedMessage());
        } catch (UnsupportedJwtException e) {
            // 이건 에러
            log.error("JWT token is unsupported : {} ", e.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty : {} ", e.getLocalizedMessage());
        }
        return false;
    }

    private Claims extractAllClaims(String token) {
        Claims claims = null;
        if (StringUtils.hasText(token)) {
            try {
                claims = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
            } catch (JwtException e) {
                log.warn(e.getLocalizedMessage());
            }
            return claims;
        }
        return claims;
    }

    private Key getSignKey() {
        String secretKeyEncodeBase64 = Encoders.BASE64.encode(jwtSecret.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyEncodeBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
