package com.example.holidayplanner.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey secretKey =  Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateAccessToken(String accessToken) {
        return (isTokenValid(accessToken) && !isTokenExpired(accessToken));
    }

    public Boolean validateRefreshToken(String refeshToken) {
        return (isTokenValid(refeshToken) && !isTokenExpired(refeshToken));
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt( new Date(System.currentTimeMillis()) )
                .setExpiration( new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(60)) )
                .signWith(secretKey).compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token).getBody();
    }

    private String createToken(Map<String, Object> claims, UserDetails userDetails) {

        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt( new Date(System.currentTimeMillis()) )
                .setExpiration( new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60)) )
                .signWith(secretKey).compact();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException exception) {
            return false;
        }
    }
}
