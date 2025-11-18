package com.auth.common.configs;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.auth.common.enums.RoleType;
import com.auth.roles.repository.RoleRepository;
import com.auth.roles.repository.entity.Role;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "APPLICATION-STARTUP-RUNNER")
public class ApplicationStartupRunner implements CommandLineRunner {

    RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        log.info("Application startup started");

        createRoles();
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
