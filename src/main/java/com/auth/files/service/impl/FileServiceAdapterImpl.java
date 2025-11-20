package com.auth.files.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.auth.files.api.adapter.FileServiceAdapter;
import com.auth.users.api.response.FileResponse;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "UP-FILE-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileServiceAdapterImpl implements FileServiceAdapter {

    UpFileServiceImpl upFileService;

    @Override
    public FileResponse uploadFile(MultipartFile fileData) {
        log.info("[uploadFile] fileName={}", fileData.getOriginalFilename());

        return upFileService.uploadFile(fileData);
    }
}
