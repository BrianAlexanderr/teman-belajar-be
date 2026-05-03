package com.project.teman_belajar.module.auth.service;

import com.project.teman_belajar.module.auth.entities.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    private static final long JWT_EXPIRATION = 900000;

    private static final long REFRESH_EXPIRATION = 604800000;

    public String extractUserEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Long getUserVersion(UserDetails user) {
        return Optional.ofNullable(user)
                .filter(Users.class::isInstance)
                .map(u -> ((Users) u).getVersion())
                .orElse(0L); // Default to 0 if anything goes wrong
    }

    public String generateAccessToken(UserDetails user){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("token_type", "ACCESS");
        extraClaims.put("version",  getUserVersion(user));
        return buildToken(extraClaims, user, JWT_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails user){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("token_type", "REFRESH");
        extraClaims.put("version",  getUserVersion(user));
        return buildToken(extraClaims, user, REFRESH_EXPIRATION);
    }

    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ){
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isAccessToken(String token){
        String type = extractClaim(token, claims -> claims.get("token_type").toString());
        return "ACCESS".equals(type);
    }

    private boolean isValidVersion(Long userVersion, Long claimVersion){
        return userVersion.equals(claimVersion);
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String email = extractUserEmail(token);
        final Long userVersion = getUserVersion(userDetails);
        final Long claimVersion = extractClaim(
                token,
                claims -> claims.get("version", Long.class)
        );
        return (email.equals(userDetails.getUsername())) &&
                !isTokenExpired(token) &&
                isValidVersion(userVersion, claimVersion);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims getAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignInKey() {
        byte[] keys = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keys);
    }
}
