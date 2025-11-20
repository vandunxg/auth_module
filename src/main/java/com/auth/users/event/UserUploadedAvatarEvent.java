package com.auth.users.event;

import org.springframework.web.multipart.MultipartFile;

public record UserUploadedAvatarEvent(MultipartFile file) {}
