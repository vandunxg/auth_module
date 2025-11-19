package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.auth.common.error.AuthenticationException;
import com.auth.common.utils.ErrorCode;
import com.auth.common.utils.TokenHasher;
import com.auth.users.repository.AuthKeyRepository;
import com.auth.users.repository.entity.AuthKey;
import com.auth.users.service.AuthKeyService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-KEY-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthKeyServiceImpl implements AuthKeyService {

    TokenHasher tokenHasher;
    AuthKeyRepository authKeyRepository;

    @Override
    public String extractEmail(String key) {
        log.info("[extractEmail]");

        AuthKey authKey = getAuthKeyByHashKey(tokenHasher.hash(key));

        return authKey.getEmail();
    }

    AuthKey getAuthKeyByHashKey(String hashKey) {
        log.info("[getAuthKeyByHashKey]");

        return authKeyRepository
                .findBySecretKeyHash(hashKey)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_KEY));
    }
}
