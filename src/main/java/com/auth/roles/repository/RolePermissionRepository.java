package com.auth.roles.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auth.roles.repository.entity.RolePermission;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {}
