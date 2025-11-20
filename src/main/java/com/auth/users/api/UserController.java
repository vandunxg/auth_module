package com.auth.users.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.auth.common.utils.MessageConstant;
import com.auth.common.utils.ResponseUtil;
import com.auth.users.api.request.ResetPasswordRequest;
import com.auth.users.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        log.info("[GET /users/me] Get current user]");

        return ResponseUtil.success(userService.getCurrentUser());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/list-users")
    public ResponseEntity<?> listUsers() {
        log.info("[GET /users/list-users] List all users");

        return ResponseUtil.success(userService.getAllUsers());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        log.info("[POST /users/reset-password]={}", request);

        userService.resetPassword(request);

        return ResponseUtil.success(MessageConstant.PASSWORD_CHANGED);
    }

    @GetMapping("/generate-key")
    public ResponseEntity<?> generateKey() {
        log.info("[GET /users/generate-key]");

        return ResponseUtil.success(userService.generateAuthKey());
    }

    @GetMapping("/login-history")
    ResponseEntity<?> loginHistory() {
        log.info("[GET] /auth/login-history");

        return ResponseUtil.success(MessageConstant.READ_SUCCESS, userService.loginHistory());
    }

    @GetMapping("/sessions")
    ResponseEntity<?> sessions() {
        log.info("[GET] /auth/sessions");

        return ResponseUtil.success(MessageConstant.READ_SUCCESS, userService.getSessions());
    }

    @PostMapping("/revoke-session/{session-id}")
    ResponseEntity<?> revokeSession(@PathVariable("session-id") String sessionId) {
        log.info("[POST] /auth/revoke-session/{}", sessionId);

        userService.revokeSession(sessionId);

        return ResponseUtil.success(MessageConstant.SESSION_REVOKED);
    }

    @PostMapping("/delete-key/{key}")
    ResponseEntity<?> deleteKey(@PathVariable("key") String key) {
        log.info("[POST] /auth/delete-key/{}", key);

        userService.deleteAuthKey(key);

        return ResponseUtil.success(MessageConstant.DELETE_SUCCESS);
    }

    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {

        return ResponseUtil.success(
                MessageConstant.OPERATION_SUCCESS, userService.uploadAvatar(file));
    }
}
