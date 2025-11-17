package com.auth.users.service;

import java.nio.file.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;

import com.auth.users.api.request.LoginRequest;
import com.auth.users.api.request.RegisterRequest;
import com.auth.users.api.response.RegisterResponse;
import com.auth.users.api.response.TokenResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest registerRequest);

    TokenResponse login(LoginRequest request, HttpServletRequest httpRequest)
            throws AccessDeniedException;
}
