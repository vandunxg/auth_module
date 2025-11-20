package com.auth.users.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auth.common.utils.MessageConstant;
import com.auth.common.utils.ResponseUtil;
import com.auth.users.api.request.*;
import com.auth.users.api.response.RegisterResponse;
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

    @PostMapping("/login-with-key")
    ResponseEntity<?> loginWithKey(
            @RequestBody LoginWithKeyRequest request, HttpServletRequest httpRequest) {
        log.info("[POST] /auth/login-with-key]={}", request);

        return ResponseUtil.success(authService.loginWithKey(request, httpRequest));
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

    @PostMapping("/forget-password")
    ResponseEntity<?> forgetPassword(@RequestBody @Valid ForgetPasswordRequest request) {
        log.info("[POST] /auth/forget-password");

        return ResponseUtil.success(authService.forgetPassword(request));
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("code") String resetCode) {
        log.info("[GET] /auth/reset-password?code={}", resetCode);

        authService.verifyResetToken(resetCode);

        return ResponseUtil.success(MessageConstant.READ_SUCCESS);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordForgetRequest request) {
        log.info("[POST] /auth/reset-password={}", request);

        authService.resetPassword(request);

        return ResponseUtil.success(MessageConstant.UPDATE_SUCCESS);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {

        return ResponseUtil.success(new RegisterResponse(UUID.randomUUID()));
    }
}
