package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.common.configs.UserPrincipal;
import com.auth.common.enums.SessionStatus;
import com.auth.common.error.AccessMaximumResourceException;
import com.auth.common.error.UserValidationException;
import com.auth.common.utils.ErrorCode;
import com.auth.common.utils.TokenHasher;
import com.auth.users.api.request.RegisterRequest;
import com.auth.users.api.request.ResetPasswordRequest;
import com.auth.users.api.response.AuthKeyResponse;
import com.auth.users.api.response.LoginHistoryResponse;
import com.auth.users.api.response.SessionResponse;
import com.auth.users.api.response.UserResponse;
import com.auth.users.event.UserCreatedEvent;
import com.auth.users.event.UserRevokeSessionEvent;
import com.auth.users.factory.AuthKeyFactory;
import com.auth.users.factory.UserFactory;
import com.auth.users.repository.AuthKeyRepository;
import com.auth.users.repository.LoginHistoryRepository;
import com.auth.users.repository.UserRepository;
import com.auth.users.repository.UserSessionRepository;
import com.auth.users.repository.entity.AuthKey;
import com.auth.users.repository.entity.LoginHistory;
import com.auth.users.repository.entity.User;
import com.auth.users.repository.entity.UserSession;
import com.auth.users.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserFactory userFactory;
    AuthKeyFactory authKeyFactory;
    ApplicationEventPublisher eventPublisher;
    PasswordEncoder passwordEncoder;
    TokenHasher tokenHasher;
    AuthKeyRepository authKeyRepository;
    UserSessionRepository userSessionRepository;
    LoginHistoryRepository loginHistoryRepository;

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

    @Override
    public UserResponse getCurrentUser() {
        log.info("[getCurrentUser] request");

        UserPrincipal principal = getCurrentUserPrincipal();
        User user = principal.getUser();

        return new UserResponse(
                user.getId(), user.getFullName(), user.getEmail(), user.getPhoneNumber());
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("[getAllUsers] request");

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(
                        x ->
                                new UserResponse(
                                        x.getId(),
                                        x.getFullName(),
                                        x.getEmail(),
                                        x.getPhoneNumber()))
                .toList();
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        log.info("[resetPassword] request={}", request);

        UserPrincipal principal = getCurrentUserPrincipal();
        User user = principal.getUser();

        ensureOldPasswordMatches(request.oldPassword(), user);

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        log.info("[resetPassword] updated password to db");
        userRepository.save(user);
    }

    @Override
    public void resetPassword(String password, UUID userId) {
        log.info("[resetPassword]");

        User user = userRepository.findUserById(userId);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    @Override
    public AuthKeyResponse generateAuthKey() {
        log.info("[generateAuthKey]");

        UserPrincipal principal = getCurrentUserPrincipal();
        User user = principal.getUser();

        ensureUserJustOnlyOneKey(user.getId());

        String key = UUID.randomUUID().toString();

        AuthKey authKey = authKeyFactory.create(user.getId(), user.getEmail(), key);

        log.info("[generateAuthKey] save auth key to db");
        authKeyRepository.save(authKey);

        return new AuthKeyResponse(key);
    }

    @Override
    public List<LoginHistoryResponse> loginHistory() {
        log.info("[loginHistory]");

        UserPrincipal principal = getUserPrincipal();

        UUID userId = principal.getUser().getId();

        List<LoginHistory> loginHistories = loginHistoryRepository.findALlByUserId(userId);

        return loginHistories.stream()
                .map(x -> new LoginHistoryResponse(x.getIp(), x.getPlatform(), x.getLoginAt()))
                .toList();
    }

    @Override
    @Transactional
    public void revokeSession(String sessionId) {
        log.info("[revokeSession] sessionId={}", sessionId);

        UserPrincipal principal = getUserPrincipal();
        User user = principal.getUser();
        UUID uuidOfSessionId = UUID.fromString(sessionId);

        eventPublisher.publishEvent(new UserRevokeSessionEvent(uuidOfSessionId, user.getId()));
    }

    @Override
    public List<SessionResponse> getSessions() {
        log.info("[getSessions]");

        UserPrincipal principal = getUserPrincipal();
        UUID userId = principal.getUser().getId();

        List<UserSession> sessions =
                userSessionRepository.findAllByUserIdAndStatus(userId, SessionStatus.ACTIVE);

        return sessions.stream()
                .map(
                        x ->
                                new SessionResponse(
                                        x.getId(),
                                        x.getIp(),
                                        x.getPlatform(),
                                        x.getDeviceId(),
                                        x.getLastActiveAt()))
                .toList();
    }

    void ensureUserJustOnlyOneKey(UUID userId) {
        log.info("[ensureUserJustOnlyOneKey] request={}", userId);

        if (authKeyRepository.existsByUserId(userId)) {
            throw new AccessMaximumResourceException(ErrorCode.YOU_HAVE_KEY);
        }
    }

    void ensureOldPasswordMatches(String oldPassword, User user) {
        log.info("[ensureOldPasswordMatches]");

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserValidationException(ErrorCode.OLD_PASSWORD_NOT_MATCH);
        }
    }

    UserPrincipal getCurrentUserPrincipal() {
        log.info("[getCurrentUserPrincipal]");

        return (UserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    void ensureEmailNotExists(String email) {
        log.info("[ensureEmailNotExists]={}", email);

        if (userRepository.existsUserByEmail(email)) {
            log.error("[ensureEmailNotExists]={} already exists", email);

            throw new UserValidationException(ErrorCode.EMAIL_EXISTS);
        }
    }

    UserPrincipal getUserPrincipal() {
        log.info("[getUserPrincipal]");

        return (UserPrincipal)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
