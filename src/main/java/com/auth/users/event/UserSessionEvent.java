package com.auth.users.event;

import java.util.UUID;

public record UserSessionEvent(
        UUID userId, String deviceId, String platform, String ipAddress, String refreshTokenHash) {}
