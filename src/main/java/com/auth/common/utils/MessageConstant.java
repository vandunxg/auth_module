package com.auth.common.utils;

public final class MessageConstant {

    private MessageConstant() {}

    // ===== AUTH MESSAGES =====
    public static final String LOGIN_SUCCESS = "Login successfully";
    public static final String LOGIN_FAILED = "Login failed";
    public static final String LOGOUT_SUCCESS = "Logout successfully";

    // ===== USER MESSAGES =====
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_CREATED = "User created successfully";
    public static final String USER_UPDATED = "User updated successfully";

    // ===== PASSWORD MESSAGES =====
    public static final String PASSWORD_CHANGED = "Password changed successfully";
    public static final String PASSWORD_INVALID = "Invalid password";
    public static final String PASSWORD_RESET_SENT = "Reset password link has been sent";

    // ===== TOKEN MESSAGES =====
    public static final String TOKEN_INVALID = "Invalid token";
    public static final String TOKEN_EXPIRED = "Token expired";
    public static final String TOKEN_REVOKED = "Token revoked";
    public static final String TOKEN_BLACKLIST = "Token blacklisted";

    // ===== API KEY MESSAGES =====
    public static final String API_KEY_INVALID = "Invalid API Key";
    public static final String API_KEY_EXPIRED = "API Key expired";
    public static final String API_KEY_REVOKED = "API Key revoked";

    // ===== SESSION MESSAGES =====
    public static final String SESSION_INVALID = "Invalid session";
    public static final String SESSION_REVOKED = "Session revoked";
    public static final String SESSION_EXPIRED = "Session expired";

    // ===== SYSTEM / GENERAL =====
    public static final String OPERATION_SUCCESS = "Operation completed successfully";
    public static final String OPERATION_FAILED = "Operation failed";
    public static final String INTERNAL_ERROR = "Internal server error";
    public static final String ACCESS_DENIED = "Access denied";

    // ========== CREATE ==========
    public static final String CREATE_SUCCESS = "Created successfully";
    public static final String CREATE_FAILED = "Failed to create resource";

    // ========== READ ==========
    public static final String READ_SUCCESS = "Fetched successfully";
    public static final String READ_FAILED = "Failed to fetch resource";
    public static final String NOT_FOUND = "Resource not found";

    // ========== UPDATE ==========
    public static final String UPDATE_SUCCESS = "Updated successfully";
    public static final String UPDATE_FAILED = "Failed to update resource";

    // ========== DELETE ==========
    public static final String DELETE_SUCCESS = "Deleted successfully";
    public static final String DELETE_FAILED = "Failed to delete resource";

    // ========== LIST / PAGINATION ==========
    public static final String LIST_SUCCESS = "List retrieved successfully";
    public static final String LIST_EMPTY = "List is empty";

    // ========== VALIDATION ==========
    public static final String VALIDATION_ERROR = "Validation error";
    public static final String INVALID_REQUEST = "Invalid request data";
}
