package com.superbox.study.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${superbox.app.jwtSecret}")
    private String jwtSecret;
    @Value("${superbox.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        // 주요 정보
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.ES512, jwtSecret)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature : {} ", e.getLocalizedMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token : {} ", e.getLocalizedMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired : {} ", e.getLocalizedMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported : {} ", e.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty : {} ", e.getLocalizedMessage());
        }
        return false;
    }

}
