package com.auth.users.services.impl;

import com.auth.common.utils.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.auth.users.apis.request.LoginRequest;
import com.auth.users.apis.request.RegisterRequest;
import com.auth.users.apis.response.RegisterResponse;
import com.auth.users.apis.response.TokenResponse;
import com.auth.users.configs.UserPrincipal;
import com.auth.users.services.AuthService;
import com.auth.users.services.JwtService;
import com.auth.users.services.UserService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserService userService;
    JwtService jwtService;
    AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.info("[register] request={}", request);

        UUID userId = userService.createUserForRegister(request);

        return new RegisterResponse(userId);
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        log.info("[login] request={}", request);

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.email(), request.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            return jwtService.issueToken(principal);

        } catch (AuthenticationException ex) {
            log.warn("[login] Failed email={}, reason={}", request.email(), ex.getMessage());

            throw new com.auth.common.error.AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}
