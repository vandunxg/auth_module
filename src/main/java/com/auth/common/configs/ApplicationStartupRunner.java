package com.auth.common.configs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.auth.common.enums.RoleType;
import com.auth.common.enums.UserStatus;
import com.auth.roles.repository.RoleRepository;
import com.auth.roles.repository.UserRoleRepository;
import com.auth.roles.repository.entity.Role;
import com.auth.users.repository.UserRepository;
import com.auth.users.repository.entity.User;
import com.auth.users.repository.entity.UserRole;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "APPLICATION-STARTUP-RUNNER")
public class ApplicationStartupRunner implements CommandLineRunner {

    RoleRepository roleRepository;
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Application startup started");
        createRoles();
        createAdmin();
    }

    void createAdmin() {
        log.info("[createAdmin] Creating admin account]");

        if (userRepository.existsUserByEmail("admin@admin.com")) {
            log.info("[createAdmin] admin already exists");
            return;
        }

        Role adminRole = roleRepository.findByName(RoleType.ADMIN.name());
        User user =
                new User(
                        "admin",
                        "admin@admin.com",
                        passwordEncoder.encode("Admin@123"),
                        null,
                        UserStatus.ACTIVE);

        userRepository.save(user);
        log.warn("[createAdmin] Admin account created");

        UserRole userRole = new UserRole(user.getId(), adminRole.getId());
        userRoleRepository.save(userRole);
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
}
