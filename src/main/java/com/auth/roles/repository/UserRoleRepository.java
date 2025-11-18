package com.auth.roles.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.users.repository.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {}
