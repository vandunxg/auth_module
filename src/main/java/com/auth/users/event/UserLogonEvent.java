package com.auth.users.event;

import java.util.UUID;

import com.auth.common.enums.LoginStatus;

public record UserLogonEvent(
        UUID userId,
        String identifier,
        String platform,
        String deviceId,
        String ipAddress,
        LoginStatus status) {}
