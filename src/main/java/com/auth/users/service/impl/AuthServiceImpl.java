package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
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

import com.auth.common.configs.UserPrincipal;
import com.auth.common.enums.LoginStatus;
import com.auth.common.enums.SessionStatus;
import com.auth.common.enums.TokenType;
import com.auth.common.utils.ErrorCode;
import com.auth.common.utils.TokenHasher;
import com.auth.users.api.request.LoginRequest;
import com.auth.users.api.request.RegisterRequest;
import com.auth.users.api.response.LoginHistoryResponse;
import com.auth.users.api.response.RegisterResponse;
import com.auth.users.api.response.SessionResponse;
import com.auth.users.api.response.TokenResponse;
import com.auth.users.event.UserLogonEvent;
import com.auth.users.event.UserLogoutEvent;
import com.auth.users.event.UserRevokeSessionEvent;
import com.auth.users.event.UserSessionEvent;
import com.auth.users.repository.LoginHistoryRepository;
import com.auth.users.repository.UserSessionRepository;
import com.auth.users.repository.entity.LoginHistory;
import com.auth.users.repository.entity.User;
import com.auth.users.repository.entity.UserSession;
import com.auth.users.service.AuthService;
import com.auth.users.service.JwtService;
import com.auth.users.service.UserService;

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
    UserSessionRepository userSessionRepository;
    LoginHistoryRepository loginHistoryRepository;

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

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {
        log.info("[logout]");

        String refreshToken = getRefreshTokenFromRequest(request);

        String tokenHash = tokenHasher.hash(refreshToken);

        log.info("[logout] publish UserLogonEvent when logout successfully");
        eventPublisher.publishEvent(new UserLogoutEvent(tokenHash));
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("[refreshToken] request={}]", request.getPathInfo());

        String refreshToken = getRefreshTokenFromRequest(request);
        UserPrincipal principal = getUserPrincipal();

        ensureRefreshTokenNotInvoke(refreshToken);
        ensureRefreshTokenNotExpiry(refreshToken);

        return new TokenResponse(jwtService.generateAccessToken(principal), refreshToken);
    }

    @Override
    public List<LoginHistoryResponse> loginHistory() {
        log.info("[loginHistory]");

        UserPrincipal principal = getUserPrincipal();

        UUID userId = principal.getUser().getId();

        List<LoginHistory> loginHistories = loginHistoryRepository.findALlByUserId(userId);

        return loginHistories.stream()
                .map(x -> new LoginHistoryResponse(x.getIp(), x.getPlatform(), x.getLoginAt()))
                .toList();
    }

    @Override
    @Transactional
    public void revokeSession(String sessionId) {
        log.info("[revokeSession] sessionId={}", sessionId);

        UserPrincipal principal = getUserPrincipal();
        User user = principal.getUser();
        UUID uuidOfSessionId = UUID.fromString(sessionId);

        eventPublisher.publishEvent(new UserRevokeSessionEvent(uuidOfSessionId, user.getId()));
    }

    @Override
    public List<SessionResponse> getSessions() {
        log.info("[getSessions]");

        UserPrincipal principal = getUserPrincipal();
        UUID userId = principal.getUser().getId();

        List<UserSession> sessions =
                userSessionRepository.findAllByUserIdAndStatus(userId, SessionStatus.ACTIVE);

        return sessions.stream()
                .map(
                        x ->
                                new SessionResponse(
                                        x.getId(),
                                        x.getIp(),
                                        x.getPlatform(),
                                        x.getDeviceId(),
                                        x.getLastActiveAt()))
                .toList();
    }

    UserPrincipal getUserPrincipal() {
        log.info("[getUserPrincipal]");

        return (UserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    void ensureRefreshTokenNotExpiry(String refreshToken) {
        log.info("[ensureRefreshTokenNotExpiry] refreshToken={}", refreshToken.substring(0, 10));

        Date expiryDate = jwtService.extractExpiration(refreshToken, TokenType.REFRESH_TOKEN);

        if (expiryDate.before(new Date())) {
            log.error(
                    "[ensureRefreshTokenNotExpiry] refreshToken={} expired",
                    refreshToken.substring(0, 10));

            throw new com.auth.common.error.AuthenticationException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    void ensureRefreshTokenNotInvoke(String refreshToken) {
        log.info("[ensureRefreshTokenNotInvoke] refreshToken={}", refreshToken.substring(0, 10));

        String tokenHash = tokenHasher.hash(refreshToken);

        UserSession userSession =
                userSessionRepository
                        .findByRefreshTokenHash(tokenHash)
                        .orElseThrow(
                                () ->
                                        new com.auth.common.error.AuthenticationException(
                                                ErrorCode.INVALID_TOKEN));

        if (!userSession.getStatus().equals(SessionStatus.ACTIVE)) {

            log.info(
                    "[ensureRefreshTokenNotInvoke] refreshToken={} invoked",
                    refreshToken.substring(0, 10));
            throw new com.auth.common.error.AuthenticationException(ErrorCode.TOKEN_REVOKED);
        }
    }

    String getRefreshTokenFromRequest(HttpServletRequest request) {
        log.info("[getRefreshTokenFromRequest] request={}", request.getPathInfo());

        String authHeader = request.getHeader("x-token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("[logout] Invalid token");

            throw new com.auth.common.error.AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }

        return authHeader.substring(7);
    }

    String getClientIp(HttpServletRequest request) {
        log.info("[getClientIp] request={}]", request.getPathInfo());

        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
