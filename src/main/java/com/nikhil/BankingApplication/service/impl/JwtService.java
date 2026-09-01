package com.nikhil.BankingApplication.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final String secret;

    public JwtService(@Value("${JWT_SECRET}")String secret){
        this.secret = secret;
    }

    public String generateToken(String email){
        logger.info("Generating JWT token for email {}", email);
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        logger.debug("Token generated successfully for email {}: {}", email, token);
        return token;
    }

    public String extractEmail(String token) {
        logger.info("Extracting email from JWT token");

        String email = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        logger.debug("Extracted email {} from token", email);
        return email;
    }

    public boolean validateToken(String token, String email) {
        logger.info("Validating JWT token for email {}", email);

        String extractedEmail = extractEmail(token);
        boolean isValid = (extractedEmail.equals(email) && !isTokenExpired(token));

        if (isValid) {
            logger.info("JWT token is valid for email {}", email);
        } else {
            logger.warn("JWT token validation failed for email {}. Extracted email: {}", email, extractedEmail);
        }

        return isValid;
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        boolean expired = expiration.before(new Date());
        if (expired) {
            logger.warn("JWT token has expired. Expiration time: {}", expiration);
        } else {
            logger.debug("JWT token is still valid. Expiration time: {}", expiration);
        }
        return expired;
    }
}
