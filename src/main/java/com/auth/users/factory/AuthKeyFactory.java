package com.auth.users.factory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth.common.utils.TokenHasher;
import com.auth.users.repository.entity.AuthKey;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthKeyFactory {

    TokenHasher tokenHasher;

    @NonFinal
    @Value("${spring.application.auth-key-expiry-minutes}")
    long AUTH_SECRET_EXPIRY_TIME;

    public AuthKey create(UUID userId, String email, String rawKey) {
        return AuthKey.builder()
                .userId(userId)
                .email(email)
                .secretKeyHash(tokenHasher.hash(rawKey))
                .expiresAt(Instant.now().plusMillis(AUTH_SECRET_EXPIRY_TIME))
                .build();
    }
}
