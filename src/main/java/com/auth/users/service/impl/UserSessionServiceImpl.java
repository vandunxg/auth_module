package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.auth.common.enums.SessionStatus;
import com.auth.common.error.AuthenticationException;
import com.auth.common.utils.ErrorCode;
import com.auth.users.event.UserLogoutEvent;
import com.auth.users.event.UserRevokeSessionEvent;
import com.auth.users.event.UserSessionEvent;
import com.auth.users.repository.UserSessionRepository;
import com.auth.users.repository.entity.UserSession;
import com.auth.users.service.RedisUserSessionService;
import com.auth.users.service.UserSessionService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SESSION-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSessionServiceImpl implements UserSessionService {

    long SESSION_EXPIRE_DAYS = 30;

    UserSessionRepository userSessionRepository;
    RedisUserSessionService redisUserSessionService;

    @Override
    @Transactional
    public UserSession createSessionOnLogin(UserSessionEvent event) {
        log.info("[createSessionOnLogin] event={}", event);

        UserSession userSession =
                UserSession.builder()
                        .userId(event.userId())
                        .ip(event.ipAddress())
                        .deviceId(event.deviceId())
                        .platform(event.platform())
                        .status(SessionStatus.ACTIVE)
                        .lastActiveAt(LocalDateTime.now())
                        .expiresAt(Instant.now().plus(SESSION_EXPIRE_DAYS, ChronoUnit.DAYS))
                        .refreshTokenHash(event.refreshTokenHash())
                        .build();

        log.info("[createSessionOnLogin] save session to db");
        userSessionRepository.save(userSession);

        redisUserSessionService.saveSession(userSession, userSession.getExpiresAt());

        return userSession;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logoutSession(UserLogoutEvent event) {
        log.info("[logoutSession] event={}", event);

        UserSession session = redisUserSessionService.getByTokenHash(event.tokenHash());

        if (session == null) {
            session =
                    userSessionRepository
                            .findByRefreshTokenHash(event.tokenHash())
                            .orElseThrow(
                                    () -> new AuthenticationException(ErrorCode.INVALID_TOKEN));
        }

        // delete session from redis
        redisUserSessionService.deleteSession(session);

        changeSessionStatus(session, SessionStatus.LOGGED_OUT);

        log.info("[logoutSession] session terminated");
        userSessionRepository.save(session);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeSession(UserRevokeSessionEvent event) {
        log.info("[revokeSession] event={}", event);

        UserSession session = redisUserSessionService.getBySessionId(event.sessionId());
        if (session == null) {
            session = getUserSessionBySessionIdAndUserId(event.sessionId(), event.userId());
        }

        ensureSessionIsActive(session);

        changeSessionStatus(session, SessionStatus.REVOKED_BY_USER);

        log.info("[revokeSession] session revoked");
        userSessionRepository.save(session);
    }

    void ensureSessionIsActive(UserSession userSession) {
        log.info("[ensureSessionIsActive] session={}", userSession.getId());

        if (!userSession.getStatus().equals(SessionStatus.ACTIVE)) {
            log.error("[ensureSessionIsActive] session={} is not active", userSession.getId());

            throw new AuthenticationException(ErrorCode.SESSION_REVOKED);
        }
    }

    void changeSessionStatus(UserSession userSession, SessionStatus sessionStatus) {
        log.info("[changeSessionStatus] sessionStatus={}", sessionStatus);

        userSession.setStatus(sessionStatus);
    }

    @Transactional
    UserSession getUserSessionBySessionIdAndUserId(UUID sessionId, UUID userId) {
        log.info("[getUserSessionBySessionId] sessionId={}", sessionId);

        return userSessionRepository
                .findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.SESSION_NOT_FOUND));
    }
}
