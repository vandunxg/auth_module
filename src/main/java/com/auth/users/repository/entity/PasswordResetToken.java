package com.auth.users.repository.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.auth.common.enums.PasswordResetTokenStatus;
import com.auth.common.repository.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "password_reset_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PasswordResetToken extends BaseEntity {

    @Column(nullable = false)
    UUID userId;

    @Column(nullable = false)
    String tokenHash;

    Instant expiresAt;

    @Enumerated(value = EnumType.STRING)
    PasswordResetTokenStatus status;
}
