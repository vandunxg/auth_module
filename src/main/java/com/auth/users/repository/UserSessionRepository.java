package com.auth.users.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.common.enums.SessionStatus;
import com.auth.users.repository.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);

    List<UserSession> findAllByUserIdAndStatus(UUID userId, SessionStatus status);

    Optional<UserSession> findByIdAndUserId(UUID id, UUID userId);
}
