package com.auth.users.service;

public interface AuthKeyService {

    String extractEmail(String key);

    void deleteAuthKey(String key);

    void isKeyActive(String key);
}
