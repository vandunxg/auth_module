package com.auth.users.event;

import java.util.UUID;

public record UserRevokeSessionEvent(UUID sessionId, UUID userId) {}
