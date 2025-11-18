package com.auth.users.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.users.repository.entity.LoginHistory;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {
    List<LoginHistory> findALlByUserId(UUID userId);
}
