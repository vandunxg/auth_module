package com.auth.files.service;

import org.springframework.web.multipart.MultipartFile;

import com.auth.users.api.response.FileResponse;

public interface UpFileService {

    FileResponse uploadFile(MultipartFile fileData);
}
