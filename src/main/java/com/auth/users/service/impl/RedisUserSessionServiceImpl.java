package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.auth.users.repository.entity.UserSession;
import com.auth.users.service.RedisUserSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "REDIS-USER-SESSION-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisUserSessionServiceImpl implements RedisUserSessionService {

    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    static final String SESSION_KEY_PREFIX = "session:"; // session:<sessionId>
    static final String SESSION_TOKEN_PREFIX = "sessionToken:"; // sessionToken:<tokenHash>

    public void saveSession(UserSession session, Instant expiration) {
        log.info("[saveSession] session={}", session.getId());

        try {
            String sessionJson = objectMapper.writeValueAsString(session);
            long ttlSeconds = expiration.getEpochSecond() - Instant.now().getEpochSecond();

            if (ttlSeconds > 0) {
                redisTemplate
                        .opsForValue()
                        .set(
                                SESSION_KEY_PREFIX + session.getId(),
                                sessionJson,
                                ttlSeconds,
                                TimeUnit.SECONDS);

                redisTemplate
                        .opsForValue()
                        .set(
                                SESSION_TOKEN_PREFIX + session.getRefreshTokenHash(),
                                session.getId().toString(),
                                ttlSeconds,
                                TimeUnit.SECONDS);

                log.info(
                        "[saveSession] Cached session {} for {} seconds",
                        session.getId(),
                        ttlSeconds);
            }
        } catch (Exception e) {
            log.error("[saveSession] Cannot write session to redis", e);
        }
    }

    public UserSession getByTokenHash(String tokenHash) {
        log.info("[getByTokenHash] tokenHash={}", tokenHash.substring(0, 10));

        String key = SESSION_TOKEN_PREFIX + tokenHash;

        String sessionId = redisTemplate.opsForValue().get(key);
        if (sessionId == null) {
            return null;
        }

        return getBySessionId(UUID.fromString(sessionId));
    }

    public UserSession getBySessionId(UUID sessionId) {
        log.info("[getBySessionId] sessionId={}", sessionId);

        String json = redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + sessionId);
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, UserSession.class);
        } catch (Exception e) {
            log.error("[getBySessionId] Cannot parse session json", e);
            return null;
        }
    }

    public void deleteSession(UserSession session) {
        log.info("[deleteSession] session={}", session.getId());

        redisTemplate.delete(SESSION_KEY_PREFIX + session.getId());
        redisTemplate.delete(SESSION_TOKEN_PREFIX + session.getRefreshTokenHash());
        log.info("[deleteSession] session deleted from redis {}", session.getId());
    }
}
