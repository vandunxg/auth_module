package com.auth.common.configs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.auth.common.enums.PermissionType;
import com.auth.common.enums.RoleType;
import com.auth.roles.repository.PermissionRepository;
import com.auth.roles.repository.RolePermissionRepository;
import com.auth.roles.repository.RoleRepository;
import com.auth.roles.repository.entity.Permission;
import com.auth.roles.repository.entity.Role;
import com.auth.roles.repository.entity.RolePermission;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "APPLICATION-STARTUP-RUNNER")
public class ApplicationStartupRunner implements CommandLineRunner {

    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;

    @Override
    public void run(String... args) {
        createRoles();
        createPermissions();
        assignPermissionsToRoles();
    }

    void createRoles() {
        log.info("[createRoles]");

        List<Role> roles = roleRepository.findAll();

        if (!roles.isEmpty()) {
            log.info("[createRoles] roles already created, skip");
            return;
        }

        Role adminRole = new Role(RoleType.ADMIN.name(), "Administrator");
        Role userRole = new Role(RoleType.USER.name(), "Normal user");

        roleRepository.saveAll(List.of(adminRole, userRole));

        log.info("[createRoles] created {} roles", 2);
    }

    void createPermissions() {
        log.info("[createPermissions]");

        List<Permission> permissions = permissionRepository.findAll();

        if (!permissions.isEmpty()) {
            log.info("[createPermissions] permissions already created, skip");
            return;
        }

        Permission p1 = new Permission(PermissionType.RESOURCE_CREATE.name(), "Create resource");
        Permission p2 = new Permission(PermissionType.RESOURCE_READ.name(), "Read resource");
        Permission p3 = new Permission(PermissionType.ADMIN_PANEL_ACCESS.name(), "Admin access");

        permissionRepository.saveAll(List.of(p1, p2, p3));

        log.info("[createPermissions] created {} permissions", 3);
    }

    void assignPermissionsToRoles() {
        log.info("[assignPermissionsToRoles]");

        List<RolePermission> allRolePermissions = rolePermissionRepository.findAll();

        if (!allRolePermissions.isEmpty()) {
            log.info("[assignPermissionsToRoles] role_permissions already created, skip");
            return;
        }

        List<Role> roles = roleRepository.findAll();
        List<Permission> permissions = permissionRepository.findAll();

        List<RolePermission> rolePermissions = new ArrayList<>();

        roles.forEach(
                role -> {
                    if (role.getName().equals(RoleType.ADMIN.name())) {
                        permissions.forEach(
                                p ->
                                        rolePermissions.add(
                                                new RolePermission(role.getId(), p.getId())));
                    }

                    if (role.getName().equals(RoleType.USER.name())) {
                        permissions.stream()
                                .filter(
                                        p ->
                                                p.getPermission()
                                                        .equals(
                                                                PermissionType.RESOURCE_READ
                                                                        .name()))
                                .forEach(
                                        p ->
                                                rolePermissions.add(
                                                        new RolePermission(
                                                                role.getId(), p.getId())));
                    }
                });

        rolePermissionRepository.saveAll(rolePermissions);
        log.info("[assignPermissionsToRoles] created {} role_permissions", rolePermissions.size());
    }
}
