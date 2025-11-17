package com.auth.users.services;

import java.util.UUID;

import com.auth.users.apis.request.RegisterRequest;

public interface UserService {

    UUID createUserForRegister(RegisterRequest request);
}
