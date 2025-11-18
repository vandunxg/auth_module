package com.auth.users.event.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.auth.users.event.UserLogoutEvent;
import com.auth.users.event.UserRevokeSessionEvent;
import com.auth.users.event.UserSessionEvent;
import com.auth.users.service.UserSessionService;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "USER-SESSION-LISTENER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSessionListener {

    UserSessionService userSessionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserLogon(UserSessionEvent event) {
        log.info("[onUserLogon] event={}", event);

        userSessionService.createSessionOnLogin(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserLogout(UserLogoutEvent event) {
        log.info("[onUserLogout] event={}", event);

        userSessionService.logoutSession(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onUserRevokedSession(UserRevokeSessionEvent event) {
        log.info("[onUserRevokedSession] event={}", event);

        userSessionService.revokeSession(event);
    }
}
