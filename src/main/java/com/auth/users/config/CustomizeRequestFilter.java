package com.auth.users.config;

import static com.auth.common.enums.TokenType.ACCESS_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.common.response.ErrorResponse;
import com.auth.common.utils.ErrorCode;
import com.auth.common.utils.ResponseUtil;
import com.auth.users.service.JwtService;
import com.auth.users.service.impl.CustomUserDetailsService;
import com.google.gson.Gson;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomizeRequestFilter extends OncePerRequestFilter {

    JwtService jwtService;
    CustomUserDetailsService customUserDetailsService;
    Gson gson;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        String authHeader = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasLength(authHeader) && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(7);
            log.info("Token {}", token.substring(0, 10));
            String email;

            try {
                email = jwtService.extractEmail(token, ACCESS_TOKEN);
                log.info("email: {}", email);
            } catch (Exception e) {
                log.info(e.getMessage());

                ResponseEntity<ErrorResponse> errorResponse =
                        ResponseUtil.error(ErrorCode.TOKEN_EXPIRED);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(gson.toJson(errorResponse.getBody()));
                return;
            }

            UserDetails user = customUserDetailsService.loadUserByUsername(email);

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetails(request));
            securityContext.setAuthentication(authToken);
            SecurityContextHolder.setContext(securityContext);

            filterChain.doFilter(request, response);
        } else {
            log.warn("Request not contain token");

            filterChain.doFilter(request, response);
        }
    }
}
