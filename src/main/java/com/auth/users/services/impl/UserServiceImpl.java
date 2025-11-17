package com.auth.users.services.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.common.error.UserValidationException;
import com.auth.common.utils.ErrorCode;
import com.auth.users.apis.request.RegisterRequest;
import com.auth.users.event.UserCreatedEvent;
import com.auth.users.factory.UserFactory;
import com.auth.users.repository.UserRepository;
import com.auth.users.repository.entity.User;
import com.auth.users.services.UserService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    UserFactory userFactory;

    ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UUID createUserForRegister(RegisterRequest request) {
        log.info("[createUser] request={}", request);

        ensureEmailNotExists(request.email());

        User user = userFactory.createFromRegister(request);

        log.info("[createUser] saved user to db");
        userRepository.save(user);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId()));

        return user.getId();
    }

    void ensureEmailNotExists(String email) {
        log.info("[ensureEmailNotExists]={}", email);

        if (userRepository.existsUserByEmail(email)) {
            log.error("[ensureEmailNotExists]={} already exists", email);

            throw new UserValidationException(ErrorCode.EMAIL_EXISTS);
        }
    }
}
