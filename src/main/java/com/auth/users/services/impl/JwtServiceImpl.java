package com.auth.users.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth.common.enums.TokenType;
import com.auth.users.apis.response.TokenResponse;
import com.auth.users.configs.UserPrincipal;
import com.auth.users.repository.entity.User;
import com.auth.users.services.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "JWT-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtServiceImpl implements JwtService {

    @NonFinal
    @Value("${jwt.private-key.access-token}")
    String ACCESS_KEY;

    @NonFinal
    @Value("${jwt.private-key.refresh-token}")
    String REFRESH_KEY;

    @NonFinal
    @Value("${jwt.expiration.access-token}")
    Long ACCESS_EXPIRY;

    @NonFinal
    @Value("${jwt.expiration.refresh-token}")
    Long REFRESH_EXPIRY;

    @Override
    public String generateAccessToken(UserPrincipal principal) {

        User user = principal.getUser();
        //        List<String> roles = principal.getAuthorities()
        //                .stream()
        //                .map(GrantedAuthority::getAuthority)
        //                .toList();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        //        claims.put("roles", roles);
        claims.put("type", TokenType.ACCESS_TOKEN.name());

        return createToken(claims, user.getEmail(), TokenType.ACCESS_TOKEN);
    }

    @Override
    public String generateRefreshToken(UserPrincipal principal) {

        User user = principal.getUser();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("type", TokenType.REFRESH_TOKEN.name());

        return createToken(claims, user.getEmail(), TokenType.REFRESH_TOKEN);
    }

    public Boolean validateToken(String token, UserDetails userDetails, TokenType tokenType) {
        log.info("[validateToken]");

        final String email = extractEmail(token, tokenType);

        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token, tokenType));
    }

    @Override
    public TokenResponse issueToken(UserPrincipal user) {
        return new TokenResponse(generateAccessToken(user), generateRefreshToken(user));
    }

    Boolean isTokenExpired(String token, TokenType tokenType) {
        log.info("[isTokenExpired]");

        return extractExpiration(token, tokenType).before(new Date());
    }

    Date extractExpiration(String token, TokenType tokenType) {
        log.info("[extractExpiration]");

        return extractClaim(token, tokenType, Claims::getExpiration);
    }

    private SecretKey getKey(TokenType type) {
        return switch (type) {
            case ACCESS_TOKEN -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(ACCESS_KEY));
            case REFRESH_TOKEN -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(REFRESH_KEY));
        };
    }

    private Long getExpiry(TokenType type) {
        return switch (type) {
            case ACCESS_TOKEN -> ACCESS_EXPIRY;
            case REFRESH_TOKEN -> REFRESH_EXPIRY;
        };
    }

    private String createToken(Map<String, Object> claims, String subject, TokenType type) {

        SecretKey key = getKey(type);
        long expiry = getExpiry(type);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(key)
                .compact();
    }

    @Override
    public String extractEmail(String token, TokenType type) {
        return extractClaim(token, type, Claims::getSubject);
    }

    private Claims extractAllClaims(String token, TokenType type) {
        return Jwts.parser().verifyWith(getKey(type)).build().parseSignedClaims(token).getPayload();
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token, type));
    }
}
