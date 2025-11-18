package com.auth.users.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "oldPassword must not be blank")
                @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
                @Pattern(
                        regexp =
                                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                        message =
                                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and"
                                        + " one special character")
                String oldPassword,
        @NotBlank(message = "newPassword must not be blank")
                @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
                @Pattern(
                        regexp =
                                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                        message =
                                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and"
                                        + " one special character")
                String newPassword) {}
