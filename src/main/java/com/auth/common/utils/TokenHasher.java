package com.auth.common.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "TOKEN-HASHER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenHasher {

    @NonFinal
    @Value("${spring.application.hash-secret-key}")
    String SECRET;

    public String hash(String token) {
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
            sha256.init(key);
            return Base64.getEncoder().encodeToString(sha256.doFinal(token.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error hashing refresh token", e);
        }
    }
}
