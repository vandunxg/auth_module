package com.auth.users.service;

import java.util.List;
import java.util.UUID;

import com.auth.users.api.request.RegisterRequest;
import com.auth.users.api.request.ResetPasswordRequest;
import com.auth.users.api.response.AuthKeyResponse;
import com.auth.users.api.response.LoginHistoryResponse;
import com.auth.users.api.response.SessionResponse;
import com.auth.users.api.response.UserResponse;

public interface UserService {

    UUID createUserForRegister(RegisterRequest request);

    UserResponse getCurrentUser();

    List<UserResponse> getAllUsers();

    void resetPassword(ResetPasswordRequest request);

    void resetPassword(String password, UUID userId);

    AuthKeyResponse generateAuthKey();

    List<LoginHistoryResponse> loginHistory();

    void revokeSession(String sessionId);

    List<SessionResponse> getSessions();
}
