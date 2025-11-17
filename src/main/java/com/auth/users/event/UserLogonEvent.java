package com.auth.users.event;

import java.util.UUID;

import com.auth.common.enums.LoginStatus;

public record UserLogonEvent(
        UUID userId,
        String email,
        String platform,
        String deviceId,
        String ipAddress,
        LoginStatus status) {}
