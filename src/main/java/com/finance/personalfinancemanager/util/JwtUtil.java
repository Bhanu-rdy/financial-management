package com.finance.personalfinancemanager.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    public static void main(String[] args) {
        System.out.println(new Date());
        System.out.println("1 second: " + new Date(System.currentTimeMillis() + 1000));
        System.out.println(new Date().before(new Date(System.currentTimeMillis() + 1000)));
    }

    //Secret key for signing JWT
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //Token validity is 24 hours
    private final long JWT_EXPIRATION = 24 * 60 * 60 * 1000; //24 hours in milliseconds

    //Generate JWT token
    public String generateToken(Long userId, String userName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    //Extract all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)// verifies signature
                .getBody();    // returns payload (Claims)
    }

    //Extract userId from token
    public Long extractUserId(String token) {
        Claims  claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    //Extract userName from token
    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }


    //Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    //Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }


}
