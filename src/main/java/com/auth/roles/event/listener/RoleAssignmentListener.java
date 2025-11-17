package com.auth.roles.event.listener;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.auth.common.enums.RoleType;
import com.auth.roles.repository.RoleRepository;
import com.auth.roles.repository.entity.Role;
import com.auth.roles.service.UserRoleService;
import com.auth.users.event.UserCreatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "ROLE-ASSIGNMENT-LISTENER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleAssignmentListener {

    RoleRepository roleRepository;
    UserRoleService userRoleService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void onCreatedUserForRegister(UserCreatedEvent event) {
        log.info("onCreatedUserForRegister");

        UUID userId = event.userId();
        Role DEFAULT_USER_ROLE = roleRepository.findByName(RoleType.USER.name());

        userRoleService.assignRoleToUser(userId, DEFAULT_USER_ROLE.getId());
    }
}
