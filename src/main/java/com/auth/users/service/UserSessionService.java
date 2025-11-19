package com.auth.users.service;

import com.auth.users.event.UserLogoutEvent;
import com.auth.users.event.UserRevokeSessionEvent;
import com.auth.users.event.UserSessionEvent;
import com.auth.users.repository.entity.UserSession;

public interface UserSessionService {

    UserSession createSessionOnLogin(UserSessionEvent event);

    void logoutSession(UserLogoutEvent event);

    void revokeSession(UserRevokeSessionEvent event);
}
