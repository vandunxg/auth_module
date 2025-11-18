package com.auth.roles.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.auth.roles.repository.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    boolean existsByName(String name);

    Role findByName(String name);

    @Query(
            value =
                    """
            SELECT r.*
            FROM roles r
            JOIN user_roles ur ON ur.role_id = r.id
            WHERE ur.user_id = :userId
        """,
            nativeQuery = true)
    List<Role> findAllRolesByUserId(UUID userId);
}
