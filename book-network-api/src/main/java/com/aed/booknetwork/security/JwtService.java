package com.aed.booknetwork.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    private String generateToken(Map<String, Objects> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Objects> claims, UserDetails userDetails, Long jwtExpiration) {
        var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities", authorities)
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUserName(String jwt) {
        return extractlaim(jwt, Claims::getSubject);
    }

    public <T> T  extractlaim(String jwt, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build().parseClaimsJws(jwt).getBody();

    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String jwt, UserDetails userDetails) {
        final String username = extractUserName(jwt);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));

    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractlaim(jwt, Claims::getExpiration);
    }
}
