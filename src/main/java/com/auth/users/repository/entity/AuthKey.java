package com.auth.users.repository.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.auth.common.repository.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "auth_keys")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AuthKey extends BaseEntity {

    @Column(nullable = false)
    UUID userId;

    @Column(nullable = false)
    String secretKeyHash;

    Instant expiresAt;

    Instant loginAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
