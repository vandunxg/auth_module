package com.auth.users.service;

import java.time.Instant;
import java.util.UUID;

import com.auth.users.repository.entity.UserSession;

public interface RedisUserSessionService {

    void saveSession(UserSession session, Instant expiration);

    UserSession getByTokenHash(String tokenHash);

    UserSession getBySessionId(UUID sessionId);

    void deleteSession(UserSession session);
}
