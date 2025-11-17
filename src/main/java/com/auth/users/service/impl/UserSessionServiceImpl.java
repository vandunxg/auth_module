package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.auth.common.enums.SessionStatus;
import com.auth.users.event.UserSessionEvent;
import com.auth.users.repository.UserSessionRepository;
import com.auth.users.repository.entity.UserSession;
import com.auth.users.service.UserSessionService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SESSION-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSessionServiceImpl implements UserSessionService {

    UserSessionRepository userSessionRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSessionOnLogin(UserSessionEvent event) {
        log.info("[createSessionOnLogin] event={}", event);

        UserSession userSession =
                UserSession.builder()
                        .userId(event.userId())
                        .ip(event.ipAddress())
                        .deviceId(event.deviceId())
                        .platform(event.platform())
                        .status(SessionStatus.ACTIVE)
                        .lastActiveAt(LocalDateTime.now())
                        .refreshTokenHash(event.refreshTokenHash())
                        .build();

        log.info("[createSessionOnLogin] save session to db");
        userSessionRepository.save(userSession);
    }
}
