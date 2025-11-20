package com.auth.files.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auth.common.configs.R2Config;
import com.auth.files.service.UpFileService;
import com.auth.users.api.response.FileResponse;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "UP-FILE-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpFileServiceImpl implements UpFileService {

    S3Client s3Client;
    R2Config r2Config;

    @Override
    public FileResponse uploadFile(MultipartFile file) {
        log.info("[uploadFile] file={}", file.getOriginalFilename());

        String key = String.format("%s-%s", UUID.randomUUID(), file.getOriginalFilename());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(r2Config.getBucket())
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            log.info("[uploadFile] file upload failed, key={}", key);

            throw new RuntimeException("Upload failed", e);
        }

        log.info("[uploadFile] file uploaded successfully, key={}", key);
        return buildFileResponse(key);
    }

    FileResponse buildFileResponse(String key) {

        return new FileResponse(key, String.format("%s/%s", r2Config.getPublicUrl(), key));
    }
}
