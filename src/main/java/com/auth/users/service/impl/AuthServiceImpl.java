package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.common.enums.LoginStatus;
import com.auth.common.utils.ErrorCode;
import com.auth.users.api.request.LoginRequest;
import com.auth.users.api.request.RegisterRequest;
import com.auth.users.api.response.RegisterResponse;
import com.auth.users.api.response.TokenResponse;
import com.auth.common.configs.UserPrincipal;
import com.auth.users.event.UserLogonEvent;
import com.auth.users.event.UserSessionEvent;
import com.auth.users.repository.entity.User;
import com.auth.users.service.AuthService;
import com.auth.users.service.JwtService;
import com.auth.users.service.UserService;
import com.auth.users.util.TokenHasher;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserService userService;
    JwtService jwtService;
    AuthenticationManager authenticationManager;
    ApplicationEventPublisher eventPublisher;
    TokenHasher tokenHasher;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.info("[register] request={}", request);

        UUID userId = userService.createUserForRegister(request);

        return new RegisterResponse(userId);
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("[login] request={}", request);

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.email(), request.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            User user = principal.getUser();

            TokenResponse tokenResponse = jwtService.issueToken(principal);
            String refreshTokenHash = tokenHasher.hash(tokenResponse.refreshToken());

            log.info("[login] publish UserLogonEvent when login successfully");
            eventPublisher.publishEvent(
                    new UserLogonEvent(
                            user.getId(),
                            user.getEmail(),
                            request.platform(),
                            request.deviceId(),
                            getClientIp(httpRequest),
                            LoginStatus.SUCCESS));

            log.info("[login] publish UserSessionEvent when login successfully");
            eventPublisher.publishEvent(
                    new UserSessionEvent(
                            user.getId(),
                            request.deviceId(),
                            request.platform(),
                            getClientIp(httpRequest),
                            refreshTokenHash));

            return tokenResponse;
        } catch (AuthenticationException ex) {
            log.warn("[login] Failed email={}, reason={}", request.email(), ex.getMessage());

            log.info("[login] publish UserLogonEvent when login fail");
            eventPublisher.publishEvent(
                    new UserLogonEvent(
                            null,
                            request.email(),
                            request.platform(),
                            request.deviceId(),
                            getClientIp(httpRequest),
                            LoginStatus.FAILED));

            throw new com.auth.common.error.AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
