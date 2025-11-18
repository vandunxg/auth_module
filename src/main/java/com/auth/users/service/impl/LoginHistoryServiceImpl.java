package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.auth.users.event.UserLogonEvent;
import com.auth.users.repository.LoginHistoryRepository;
import com.auth.users.repository.entity.LoginHistory;
import com.auth.users.service.LoginHistoryService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "LOGIN-HISTORY-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginHistoryServiceImpl implements LoginHistoryService {

    LoginHistoryRepository loginHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createLoginHistory(UserLogonEvent event) {
        log.info("[createLoginHistory] event={}", event);

        LoginHistory loginHistory =
                LoginHistory.builder()
                        .userId(Objects.isNull(event.userId()) ? null : event.userId())
                        .identifier(event.identifier())
                        .deviceId(event.deviceId())
                        .platform(event.platform())
                        .ip(event.ipAddress())
                        .loginAt(LocalDateTime.now())
                        .status(event.status())
                        .build();

        log.info("[createLoginHistory] save to db");
        loginHistoryRepository.save(loginHistory);
    }
}
