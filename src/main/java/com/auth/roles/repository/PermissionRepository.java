package com.auth.roles.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auth.roles.repository.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {}
