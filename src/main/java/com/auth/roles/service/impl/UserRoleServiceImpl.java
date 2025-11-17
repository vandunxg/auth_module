package com.auth.roles.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.auth.roles.repository.entity.UserRoleRepository;
import com.auth.roles.service.UserRoleService;
import com.auth.users.repository.entity.UserRole;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-ROLE-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRoleServiceImpl implements UserRoleService {

    UserRoleRepository userRoleRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void assignRoleToUser(UUID userId, UUID roleId) {
        log.info("[assignRoleToUser] Assigning role {} to user {}", roleId, userId);

        UserRole userRole = new UserRole(userId, roleId);

        userRoleRepository.save(userRole);
    }
}
