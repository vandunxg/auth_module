package com.auth.users.apis;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.AccessDeniedException;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.common.utils.MessageConstant;
import com.auth.common.utils.ResponseUtil;
import com.auth.users.apis.request.LoginRequest;
import com.auth.users.apis.request.RegisterRequest;
import com.auth.users.services.AuthService;

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
    ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) throws AccessDeniedException {
        log.info("[POST] /auth/login]={}", request);

        return ResponseUtil.success(MessageConstant.LOGIN_SUCCESS, authService.login(request));
    }
}
