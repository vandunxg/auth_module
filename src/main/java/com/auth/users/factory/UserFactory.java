package com.auth.users.factory;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.auth.common.enums.UserStatus;
import com.auth.users.apis.request.RegisterRequest;
import com.auth.users.repository.entity.User;

@Component
@Slf4j(topic = "USER-FACTORY")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserFactory {

    PasswordEncoder passwordEncoder;

    public User createFromRegister(RegisterRequest request) {
        log.info("[createFromRegister]={}", request);

        final UserStatus status = UserStatus.ACTIVE;

        return User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .status(status)
                .build();
    }
}
