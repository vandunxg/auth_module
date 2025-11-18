package com.auth.users.api.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionResponse(
        UUID sessionId,
        String ipAddress,
        String platform,
        String deviceId,
        LocalDateTime lastActive) {}
