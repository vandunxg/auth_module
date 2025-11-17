package com.auth.users.service;

import com.auth.users.event.UserSessionEvent;

public interface UserSessionService {

    void createSessionOnLogin(UserSessionEvent event);
}
