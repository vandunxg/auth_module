package com.auth.users.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordForgetRequest(
        @NotBlank(message = "Reset token must not be blank") String code,
        @NotBlank(message = "newPassword must not be blank")
                @Size(
                        min = 8,
                        max = 128,
                        message = "newPassword must be between 8 and 128 characters")
                @Pattern(
                        regexp =
                                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                        message =
                                "newPassword must contain at least one uppercase letter, one lowercase letter, one digit, and"
                                        + " one special character")
                String newPassword,
        @NotBlank(message = "confirmPassword must not be blank")
                @Size(
                        min = 8,
                        max = 128,
                        message = "confirmPassword must be between 8 and 128 characters")
                @Pattern(
                        regexp =
                                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                        message =
                                "confirmPassword must contain at least one uppercase letter, one lowercase letter, one digit, and"
                                        + " one special character")
                String confirmPassword) {}
