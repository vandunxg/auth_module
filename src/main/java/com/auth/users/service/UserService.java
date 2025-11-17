package com.auth.users.service;

import java.util.UUID;

import com.auth.users.api.request.RegisterRequest;

public interface UserService {

    UUID createUserForRegister(RegisterRequest request);
}
