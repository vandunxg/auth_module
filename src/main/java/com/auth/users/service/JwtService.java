package com.auth.users.service;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

import com.auth.common.configs.UserPrincipal;
import com.auth.common.enums.TokenType;
import com.auth.users.api.response.TokenResponse;

public interface JwtService {

    String generateAccessToken(UserPrincipal userPrincipal);

    String generateRefreshToken(UserPrincipal userPrincipal);

    Boolean validateToken(String token, UserDetails userDetails, TokenType tokenType);

    String extractEmail(String token, TokenType tokenType);

    Date extractExpiration(String token, TokenType tokenType);

    TokenResponse issueToken(UserPrincipal user);
}
