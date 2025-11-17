package com.auth.users.service;

import com.auth.users.event.UserLogonEvent;

public interface LoginHistoryService {

    void createLoginHistory(UserLogonEvent event);
}
