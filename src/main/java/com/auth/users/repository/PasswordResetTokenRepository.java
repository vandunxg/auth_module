package com.auth.users.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.users.repository.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    void deleteAllByUserId(UUID userId);

    Optional<PasswordResetToken> getPasswordResetTokenByTokenHash(String tokenHash);
}
