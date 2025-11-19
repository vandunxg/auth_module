package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;
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
import com.auth.common.enums.*;
import com.auth.common.error.UserValidationException;
import com.auth.common.utils.ErrorCode;
import com.auth.common.utils.TokenHasher;
import com.auth.users.api.request.*;
import com.auth.users.api.response.*;
import com.auth.users.event.UserLogonEvent;
import com.auth.users.event.UserLogoutEvent;
import com.auth.users.event.UserSessionEvent;
import com.auth.users.repository.*;
import com.auth.users.repository.entity.*;
import com.auth.users.service.AuthService;
import com.auth.users.service.JwtService;
import com.auth.users.service.UserService;
import com.auth.users.service.UserSessionService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    final long RESET_TOKEN_EXPIRY_MS = 15 * 60 * 1000;

    UserService userService;
    JwtService jwtService;
    AuthenticationManager authenticationManager;
    ApplicationEventPublisher eventPublisher;
    TokenHasher tokenHasher;
    UserSessionRepository userSessionRepository;
    AuthKeyRepository authKeyRepository;
    UserRepository userRepository;
    CustomUserDetailsService customUserDetailsService;
    PasswordResetTokenRepository passwordResetTokenRepository;
    UserSessionService userSessionService;

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

        boolean isSuccess = true;
        UUID userId = null;

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.email(), request.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            User user = principal.getUser();
            userId = user.getId();

            ensureUserActive(user);

            String refreshToken = jwtService.generateRefreshToken(principal);
            UserSession userSession =
                    userSessionService.createSessionOnLogin(
                            new UserSessionEvent(
                                    user.getId(),
                                    request.deviceId(),
                                    request.platform(),
                                    getClientIp(httpRequest),
                                    tokenHasher.hash(refreshToken)));
            String accessToken = jwtService.generateAccessToken(principal, userSession.getId());

            //            log.info("[login] publish UserSessionEvent when login successfully");
            //            eventPublisher.publishEvent(
            //                    new UserSessionEvent(
            //                            user.getId(),
            //                            request.deviceId(),
            //                            request.platform(),
            //                            getClientIp(httpRequest),
            //                            refreshTokenHash));

            return new TokenResponse(accessToken, refreshToken);
        } catch (AuthenticationException ex) {
            log.warn("[login] Failed email={}, reason={}", request.email(), ex.getMessage());

            isSuccess = false;

            throw new com.auth.common.error.AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        } finally {
            log.info("[login] publish UserLogonEvent when after login");
            eventPublisher.publishEvent(
                    new UserLogonEvent(
                            userId,
                            request.email(),
                            request.platform(),
                            request.deviceId(),
                            getClientIp(httpRequest),
                            isSuccess ? LoginStatus.SUCCESS : LoginStatus.FAILED));
        }
    }

    @Override
    public TokenResponse loginWithKey(LoginWithKeyRequest request, HttpServletRequest httpRequest) {
        log.info("[loginWithKey]={}", request);

        boolean isSuccess = true;
        UUID userId = null;

        try {
            String key = request.key();
            AuthKey authKey = getAuthKeyByHashKey(tokenHasher.hash(key));
            User user = getUserById(authKey.getUserId());
            UserPrincipal principal =
                    (UserPrincipal) customUserDetailsService.loadUserByUsername(user.getEmail());
            userId = user.getId();

            String refreshToken = jwtService.generateRefreshToken(principal);
            UserSession userSession =
                    userSessionService.createSessionOnLogin(
                            new UserSessionEvent(
                                    user.getId(),
                                    request.deviceId(),
                                    request.platform(),
                                    getClientIp(httpRequest),
                                    tokenHasher.hash(refreshToken)));
            String accessToken = jwtService.generateAccessToken(principal, userSession.getId());

            //            log.info("[login] publish UserSessionEvent when login successfully");
            //            eventPublisher.publishEvent(
            //                    new UserSessionEvent(
            //                            user.getId(),
            //                            request.deviceId(),
            //                            request.platform(),
            //                            getClientIp(httpRequest),
            //                            refreshTokenHash));

            return new TokenResponse(accessToken, refreshToken);
        } catch (AuthenticationException ex) {
            log.info("[loginWithKey]={}", ex.getMessage());

            isSuccess = false;

            throw new com.auth.common.error.AuthenticationException(ErrorCode.INVALID_KEY);
        } finally {
            log.info("[loginWithKey] publish UserLogonEvent after login with key");

            eventPublisher.publishEvent(
                    new UserLogonEvent(
                            userId,
                            request.key(),
                            request.platform(),
                            request.deviceId(),
                            getClientIp(httpRequest),
                            isSuccess ? LoginStatus.SUCCESS : LoginStatus.FAILED));
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

        ensureRefreshTokenNotInvoke(refreshToken);
        ensureRefreshTokenNotExpiry(refreshToken);

        return new TokenResponse(jwtService.refreshToken(refreshToken), refreshToken);
    }

    @Override
    @Transactional
    public void verifyResetToken(String token) {
        log.info("[verifyResetToken] token={}", token);

        PasswordResetToken passwordResetToken = getPasswordResetTokenByTokenHash(token);

        ensureResetTokenNotExpired(passwordResetToken);

        if (passwordResetToken.getStatus().equals(PasswordResetTokenStatus.USED)) {
            throw new com.auth.common.error.AuthenticationException(
                    ErrorCode.RESET_TOKEN_ALREADY_USED);
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordForgetRequest request) {
        log.info("[resetPassword] request={}]", request);

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new UserValidationException(ErrorCode.CONFIRM_PASSWORD_NOT_MATCH);
        }

        String hashToken = request.code();

        PasswordResetToken passwordResetToken = getPasswordResetTokenByTokenHash(hashToken);

        if (passwordResetToken.getStatus().equals(PasswordResetTokenStatus.USED)) {
            throw new com.auth.common.error.AuthenticationException(
                    ErrorCode.RESET_TOKEN_ALREADY_USED);
        }

        if (passwordResetToken.getStatus().equals(PasswordResetTokenStatus.EXPIRED)) {
            throw new com.auth.common.error.AuthenticationException(ErrorCode.RESET_TOKEN_EXPIRY);
        }

        userService.resetPassword(request.newPassword(), passwordResetToken.getUserId());

        passwordResetToken.setStatus(PasswordResetTokenStatus.USED);

        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Transactional
    void ensureResetTokenNotExpired(PasswordResetToken passwordResetToken) {
        log.info("[ensureResetTokenNotExpired] token={}", passwordResetToken.getId());

        if (passwordResetToken.getExpiresAt().isBefore(Instant.now())) {
            passwordResetToken.setStatus(PasswordResetTokenStatus.EXPIRED);
            passwordResetTokenRepository.save(passwordResetToken);
            throw new com.auth.common.error.AuthenticationException(ErrorCode.RESET_TOKEN_EXPIRY);
        }
    }

    @Override
    @Transactional
    public PasswordResetResponse forgetPassword(ForgetPasswordRequest request) {
        log.info("[forgetPassword] request={}", request);

        User user =
                userRepository
                        .findByEmail(request.email())
                        .orElseThrow(
                                () ->
                                        new com.auth.common.error.AuthenticationException(
                                                ErrorCode.USER_NOT_FOUND));

        passwordResetTokenRepository.deleteAllByUserId(user.getId());

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = tokenHasher.hash(rawToken);

        PasswordResetToken passwordResetToken =
                PasswordResetToken.builder()
                        .userId(user.getId())
                        .tokenHash(tokenHash)
                        .expiresAt(Instant.now().plusMillis(RESET_TOKEN_EXPIRY_MS))
                        .status(PasswordResetTokenStatus.ACTIVE)
                        .build();

        passwordResetTokenRepository.save(passwordResetToken);

        // todo: publish event to send mail

        return new PasswordResetResponse(tokenHash);
    }

    PasswordResetToken getPasswordResetTokenByTokenHash(String tokenHash) {

        return passwordResetTokenRepository
                .getPasswordResetTokenByTokenHash(tokenHash)
                .orElseThrow(
                        () ->
                                new com.auth.common.error.AuthenticationException(
                                        ErrorCode.INVALID_RESET_PASSWORD));
    }

    User getUserById(UUID userId) {
        log.info("[getUserById] userId={}", userId);

        return userRepository
                .findById(userId)
                .orElseThrow(
                        () ->
                                new com.auth.common.error.AuthenticationException(
                                        ErrorCode.USER_NOT_FOUND));
    }

    AuthKey getAuthKeyByHashKey(String hashKey) {
        log.info("[getAuthKeyByHashKey]");

        return authKeyRepository
                .findBySecretKeyHash(hashKey)
                .orElseThrow(
                        () ->
                                new com.auth.common.error.AuthenticationException(
                                        ErrorCode.INVALID_KEY));
    }

    void ensureUserActive(User user) {
        log.info("[ensureUserActive] user={}", user.getId());

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new com.auth.common.error.AuthenticationException(ErrorCode.USER_NOT_ACTIVE);
        }
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
