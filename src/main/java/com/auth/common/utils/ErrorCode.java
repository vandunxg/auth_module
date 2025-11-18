package com.auth.common.utils;

import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ===== AUTH / LOGIN =====
    FAIL_LOGIN("Fail to login", "FAIL_LOGIN", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(
            "Invalid username or password", "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("Account locked", "ACCOUNT_LOCKED", HttpStatus.FORBIDDEN),
    TOKEN_EXPIRED("Token has expired", "TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED("Token has been revoked", "TOKEN_REVOKED", HttpStatus.UNAUTHORIZED),
    TOKEN_BLACKLIST("Token blacklisted", "TOKEN_BLACKLIST", HttpStatus.UNAUTHORIZED),
    FAIL_LOGOUT("Fail to logout", "FAIL_LOGOUT", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("Invalid token", "INVALID_TOKEN", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("Unauthorized", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED),
    INVALID_KEY("Invalid key", "INVALID_KEY", HttpStatus.UNAUTHORIZED),
    INVALID_RESET_PASSWORD(
            "Invalid reset password", "INVALID_RESET_PASSWORD", HttpStatus.UNAUTHORIZED),
    RESET_TOKEN_EXPIRY("Token has expired", "RESET_TOKEN_EXPIRY", HttpStatus.UNAUTHORIZED),
    RESET_TOKEN_ALREADY_USED(
            "Token already used", "RESET_TOKEN_ALREADY_USED", HttpStatus.UNAUTHORIZED),
    CONFIRM_PASSWORD_NOT_MATCH(
            "Password does not match", "CONFIRM_PASSWORD_NOT_MATCH", HttpStatus.UNAUTHORIZED),
    // ===== USER =====
    USER_NOT_FOUND("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND),
    EMAIL_EXISTS("Email already exists", "EMAIL_EXISTS", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTS("Username already exists", "USERNAME_EXISTS", HttpStatus.BAD_REQUEST),
    USER_NOT_ACTIVE("User not active", "USER_NOT_ACTIVE", HttpStatus.UNAUTHORIZED),
    OLD_PASSWORD_NOT_MATCH(
            "Old password not match", "OLD_PASSWORD_NOT_MATCH", HttpStatus.BAD_REQUEST),
    YOU_HAVE_KEY("Key has already been created", "YOU_HAVE_KEY", HttpStatus.UNAUTHORIZED),
    // ===== SESSION =====
    SESSION_NOT_FOUND("Session not found", "SESSION_NOT_FOUND", HttpStatus.NOT_FOUND),
    SESSION_EXPIRED("Session expired", "SESSION_EXPIRED", HttpStatus.UNAUTHORIZED),
    SESSION_REVOKED("Session revoked", "SESSION_REVOKED", HttpStatus.UNAUTHORIZED),
    SESSION_SUSPICIOUS("Suspicious session activity", "SESSION_SUSPICIOUS", HttpStatus.FORBIDDEN),

    // ===== API KEY =====
    API_KEY_INVALID("Invalid API Key", "API_KEY_INVALID", HttpStatus.UNAUTHORIZED),
    API_KEY_EXPIRED("API Key expired", "API_KEY_EXPIRED", HttpStatus.UNAUTHORIZED),
    API_KEY_REVOKED("API Key revoked", "API_KEY_REVOKED", HttpStatus.FORBIDDEN),

    // ===== GENERAL =====
    VALIDATION_ERROR("Validation error", "VALIDATION_ERROR", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("Internal server error", "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED("Access denied", "ACCESS_DENIED", HttpStatus.FORBIDDEN);

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String message, String code, HttpStatus status) {
        this.message = message;
        this.code = code;
        this.httpStatus = status;
    }
}
