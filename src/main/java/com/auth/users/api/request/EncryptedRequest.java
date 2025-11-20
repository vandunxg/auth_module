package com.auth.users.api.request;

import jakarta.validation.constraints.NotNull;

public record EncryptedRequest(
        @NotNull(message = "Data must be not blank")
        String data
) {}
