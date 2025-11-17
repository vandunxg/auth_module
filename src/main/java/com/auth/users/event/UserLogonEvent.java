package com.auth.users.event;

import java.util.UUID;

public record UserLogonEvent(UUID userId) {
}
