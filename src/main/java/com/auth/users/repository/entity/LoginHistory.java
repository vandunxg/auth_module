package com.auth.users.repository.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.auth.common.enums.LoginStatus;
import com.auth.common.repository.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "login_histories")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class LoginHistory extends BaseEntity {

    UUID userId;

    @Column(nullable = false)
    String email;

    String deviceId;
    String platform;
    String ip;

    @Enumerated(EnumType.STRING)
    LoginStatus status;

    LocalDateTime loginAt;
}
