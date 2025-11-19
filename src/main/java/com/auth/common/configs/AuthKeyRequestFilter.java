package com.auth.common.configs;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.common.response.ErrorResponse;
import com.auth.common.utils.ErrorCode;
import com.auth.common.utils.ResponseUtil;
import com.auth.users.service.AuthKeyService;
import com.auth.users.service.impl.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-KEY-REQUEST-FILTER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthKeyRequestFilter extends OncePerRequestFilter {

    AuthKeyService authKeyService;
    ObjectMapper objectMapper;
    CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.info("[AUTH-KEY-FILTER] {} {}", request.getMethod(), request.getRequestURI());

        if (isRequestAuthenticated()) {
            log.info("[REQUEST ALREADY AUTHENTICATED BY KEY]");

            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("x-api-key");
        if (apiKey == null || apiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = authKeyService.extractEmail(apiKey);

            UserDetails user = customUserDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            auth.setDetails(new WebAuthenticationDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            log.info("[AUTH-KEY-FILTER] AUTHENTICATED BY API-KEY: {}", email);

        } catch (Exception ex) {
            log.error("[AUTH-KEY-FILTER] INVALID KEY: {}", ex.getMessage());
            ResponseEntity<ErrorResponse> error = ResponseUtil.error(ErrorCode.INVALID_KEY);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(objectMapper.writeValueAsString(error));
            return;
        }

        filterChain.doFilter(request, response);
    }

    Boolean isRequestAuthenticated() {
        log.info("[isRequestAuthenticated] Checking if request is authenticated");

        return SecurityContextHolder.getContext().getAuthentication() != null;
    }
}
