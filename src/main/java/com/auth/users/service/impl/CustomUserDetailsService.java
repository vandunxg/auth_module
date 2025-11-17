package com.auth.users.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth.users.config.UserPrincipal;
import com.auth.users.repository.UserRepository;
import com.auth.users.repository.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOM-USER-DETAIL-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException(email));

        //        List<String> roles = userRoleRepo.findRoleNamesByUserId(user.getId());
        //
        //        List<SimpleGrantedAuthority> authorities =
        //                roles.stream()
        //                        .map(SimpleGrantedAuthority::new)
        //                        .toList();

        return new UserPrincipal(user, null);
    }
}
