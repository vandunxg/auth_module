package com.auth.users.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgetPasswordRequest(
        @NotBlank(message = "Email must not be blank")
                @Email(message = "Invalid email format")
                @Size(max = 255, message = "Email must be at most 255 characters")
                String email) {}
