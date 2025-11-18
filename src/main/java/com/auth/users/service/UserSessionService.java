package com.auth.users.service;

import com.auth.users.event.UserLogoutEvent;
import com.auth.users.event.UserRevokeSessionEvent;
import com.auth.users.event.UserSessionEvent;

public interface UserSessionService {

    void createSessionOnLogin(UserSessionEvent event);

    void logoutSession(UserLogoutEvent event);

    void revokeSession(UserRevokeSessionEvent event);
}
