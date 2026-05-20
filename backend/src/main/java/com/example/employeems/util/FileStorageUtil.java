package com.example.employeems.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Beginner-friendly file storage utility.
 *
 * - Saves uploaded images into a local folder (app.upload.dir)
 * - Returns a relative photo URL that frontend can show.
 *
 * In production, you may use S3/GCS instead.
 */
@Component
public class FileStorageUtil {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Saves file and returns stored filename.
     */
    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String original = file.getOriginalFilename();
        String ext = "";

        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }

        // Sanitize name + generate unique key
        String safeExt = ext.isBlank() ? "" : ext.toLowerCase();
        String storedName = UUID.randomUUID() + safeExt;

        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path target = dir.resolve(safeNameSafe(storedName));
        Files.copy(file.getInputStream(), target);

        return storedName;
    }

    /**
     * Returns the backend URL the frontend should use to display the photo.
     */
    public String toPhotoUrl(String storedFilename) {
        if (storedFilename == null || storedFilename.isBlank()) return null;
        // This will be served by a static endpoint we will add later.
        return "/api/employees/photos/" + storedFilename;
    }

    private String safeNameSafe(String name) {
        // Avoid path traversal
        return StringUtils.cleanPath(name);
    }
}
