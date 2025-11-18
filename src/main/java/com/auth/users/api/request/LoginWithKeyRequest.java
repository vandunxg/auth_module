package com.auth.users.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginWithKeyRequest(
        @NotBlank(message = "Key must not be blank")
                @Pattern(
                        regexp =
                                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                        message = "Invalid key format")
                String key,
        @NotBlank(message = "Platform must not be blank")
                @Pattern(
                        regexp = "^(WEB|IOS|ANDROID)$",
                        message = "Platform must be WEB, IOS, or ANDROID")
                String platform,
        @NotBlank(message = "Device id must not be blank")
                @Size(max = 255, message = "Device id must be at most 255 characters")
                String deviceId) {}
