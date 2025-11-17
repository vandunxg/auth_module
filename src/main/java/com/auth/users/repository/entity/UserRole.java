package com.auth.users.repository.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.auth.common.repository.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "user_roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserRole extends BaseEntity {

    UUID userId;
    UUID roleId;
}
