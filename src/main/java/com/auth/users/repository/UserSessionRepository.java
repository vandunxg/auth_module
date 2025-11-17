package com.auth.users.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.users.repository.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {}
