package com.auth.users.service;

import java.nio.file.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;

import com.auth.users.api.request.*;
import com.auth.users.api.response.*;

public interface AuthService {

    RegisterResponse register(RegisterRequest registerRequest);

    TokenResponse login(LoginRequest request, HttpServletRequest httpRequest)
            throws AccessDeniedException;

    TokenResponse loginWithKey(LoginWithKeyRequest request, HttpServletRequest httpRequest);

    void logout(HttpServletRequest request);

    TokenResponse refreshToken(HttpServletRequest request);

    void verifyResetToken(String token);

    void resetPassword(ResetPasswordForgetRequest resetPasswordRequest);

    PasswordResetResponse forgetPassword(ForgetPasswordRequest request);
}
