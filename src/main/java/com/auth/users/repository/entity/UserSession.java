package com.auth.users.repository.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.auth.common.enums.SessionStatus;
import com.auth.common.repository.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "user_sessions")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserSession extends BaseEntity {

    @Column(nullable = false)
    UUID userId;

    @Column(nullable = false)
    UUID refreshTokenId;

    String deviceId;

    String platform;

    String ip;

    LocalDateTime lastActiveAt;

    @Enumerated(EnumType.STRING)
    SessionStatus status;
}
