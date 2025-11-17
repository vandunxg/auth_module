package com.auth.users.event.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.auth.users.event.UserLogonEvent;
import com.auth.users.service.LoginHistoryService;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "LOGIN-HISTORY-LISTENER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginHistoryListener {

    LoginHistoryService loginHistoryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onUserLogon(UserLogonEvent event) {
        log.info("[onUserLogon] event={}", event);

        loginHistoryService.createLoginHistory(event);
    }
}
