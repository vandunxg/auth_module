package com.auth.users.services;

import java.nio.file.AccessDeniedException;

import com.auth.users.apis.request.LoginRequest;
import com.auth.users.apis.request.RegisterRequest;
import com.auth.users.apis.response.RegisterResponse;
import com.auth.users.apis.response.TokenResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest registerRequest);

    TokenResponse login(LoginRequest request) throws AccessDeniedException;
}
