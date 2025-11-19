package com.auth.users.service;

import java.util.Date;
import java.util.UUID;

import com.auth.common.configs.UserPrincipal;
import com.auth.common.enums.TokenType;
import com.auth.users.api.response.TokenResponse;

public interface JwtService {

    String refreshToken(String refreshToken);

    String generateAccessToken(UserPrincipal userPrincipal);

    String generateAccessToken(UserPrincipal userPrincipal, UUID sessionId);

    String generateRefreshToken(UserPrincipal userPrincipal);

    String extractEmail(String token, TokenType tokenType);

    String extractSessionId(String token, TokenType tokenType);

    Date extractExpiration(String token, TokenType tokenType);

    TokenResponse issueToken(UserPrincipal user);
}
