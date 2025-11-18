package com.auth.users.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.users.repository.entity.AuthKey;

public interface AuthKeyRepository extends JpaRepository<AuthKey, UUID> {
    boolean existsByUserId(UUID userId);

    Optional<AuthKey> findBySecretKeyHash(String secretKeyHash);
}
