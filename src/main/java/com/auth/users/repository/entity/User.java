package com.auth.users.repository.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.auth.common.enums.UserStatus;
import com.auth.common.repository.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends BaseEntity {

    @Column(nullable = false)
    String fullName;

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    String password;

    @Column(unique = true)
    String phoneNumber;

    @Enumerated(EnumType.STRING)
    UserStatus status;

    String avatar;
}
