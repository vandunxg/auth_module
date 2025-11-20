package com.auth.files.api.adapter;

import org.springframework.web.multipart.MultipartFile;

import com.auth.users.api.response.FileResponse;

public interface FileServiceAdapter {

    FileResponse uploadFile(MultipartFile fileData);
}
