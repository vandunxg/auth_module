package com.auth.roles.service;

import java.util.UUID;

public interface UserRoleService {

    void assignRoleToUser(UUID userId, UUID roleId);
}
