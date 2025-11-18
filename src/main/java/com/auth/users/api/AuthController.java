package com.auth.users.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auth.common.utils.MessageConstant;
import com.auth.common.utils.ResponseUtil;
import com.auth.users.api.request.LoginRequest;
import com.auth.users.api.request.RegisterRequest;
import com.auth.users.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.info("[POST] /auth/register]={}", registerRequest);

        return ResponseUtil.created(authService.register(registerRequest));
    }

    @PostMapping("/login")
    ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest request, HttpServletRequest httpRequest)
            throws AccessDeniedException {
        log.info("[POST] /auth/login]={}", request);

        return ResponseUtil.success(
                MessageConstant.LOGIN_SUCCESS, authService.login(request, httpRequest));
    }

    @PostMapping("/logout")
    ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("[POST] /auth/logout]");

        authService.logout(request);

        return ResponseUtil.success(MessageConstant.LOGOUT_SUCCESS);
    }

    @PostMapping("/refresh-token")
    ResponseEntity<?> refreshToken(HttpServletRequest request) {
        log.info("[POST] /auth/refresh-token");

        return ResponseUtil.success(authService.refreshToken(request));
    }

    @GetMapping("/login-history")
    ResponseEntity<?> loginHistory() {
        log.info("[GET] /auth/login-history");

        return ResponseUtil.success(MessageConstant.READ_SUCCESS, authService.loginHistory());
    }

    @GetMapping("/sessions")
    ResponseEntity<?> sessions() {
        log.info("[GET] /auth/sessions");

        return ResponseUtil.success(MessageConstant.READ_SUCCESS, authService.getSessions());
    }

    @PostMapping("/revoke-session/{session-id}")
    ResponseEntity<?> revokeSession(@PathVariable("session-id") String sessionId) {
        log.info("[POST] /auth/revoke-session/{}", sessionId);

        authService.revokeSession(sessionId);

        return ResponseUtil.success(MessageConstant.SESSION_REVOKED);
    }
}
