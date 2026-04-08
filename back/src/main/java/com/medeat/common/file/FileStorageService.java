package com.medeat.common.file;

import com.medeat.common.config.FileStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    public String store(MultipartFile file, String subDirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Path baseDir = Paths.get(fileStorageProperties.getUploadDir()).normalize();
        Path targetDir = (subDirectory == null || subDirectory.isBlank())
                ? baseDir
                : baseDir.resolve(subDirectory).normalize();

        Files.createDirectories(targetDir);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = System.currentTimeMillis() + "_" + originalName;
        Path savedFile = targetDir.resolve(fileName).normalize();

        if (!savedFile.startsWith(targetDir)) {
            throw new IOException("Invalid upload path");
        }

        file.transferTo(savedFile);
        return fileName;
    }
}
