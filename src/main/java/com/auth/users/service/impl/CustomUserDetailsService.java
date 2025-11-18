package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth.common.configs.UserPrincipal;
import com.auth.roles.repository.RoleRepository;
import com.auth.roles.repository.entity.Role;
import com.auth.users.repository.UserRepository;
import com.auth.users.repository.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOM-USER-DETAIL-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository;
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("[loadUserByUsername] email={}", email);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException(email));

        List<Role> roles = roleRepository.findAllRolesByUserId(user.getId());

        List<SimpleGrantedAuthority> authorities =
                roles.stream().map(x -> new SimpleGrantedAuthority("ROLE_" + x.getName())).toList();

        return new UserPrincipal(user, authorities);
    }
}
