package com.auth.users.api.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        UUID userId, String fullName, String email, String phoneNumber, String avatar) {}
