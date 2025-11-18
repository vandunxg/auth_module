package com.auth.users.api.response;

import java.time.LocalDateTime;

public record LoginHistoryResponse(String ipAddress, String platform, LocalDateTime loginAt) {}
