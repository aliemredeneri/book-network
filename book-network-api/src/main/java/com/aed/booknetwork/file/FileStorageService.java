package com.aed.booknetwork.file;

import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(@Nonnull MultipartFile sourceFile, @Nonnull Integer userId) {
        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(@Nonnull MultipartFile sourceFile, @Nonnull String fileUploadSubPath) {
        final String fileName = currentTimeMillis() + "_" + sourceFile.getOriginalFilename();
        final String fileUploadFullPath = fileUploadPath + separator + fileUploadSubPath + separator + fileName;
        try {
            final Path path = Paths.get(fileUploadFullPath);
            Files.createDirectories(path.getParent());
            Files.write(path, sourceFile.getBytes());
            log.info("File uploaded successfully to {}", fileUploadFullPath);
            return fileUploadFullPath;
        } catch (IOException e) {
            log.error("Error while uploading file", e);
            return null;
        }
    }


}
