package com.example.employeems.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
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

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path target = dir.resolve(safeNameSafe(storedName)).normalize();
        if (!target.startsWith(dir)) {
            throw new IOException("Invalid file path");
        }

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return storedName;
    }

    /**
     * Returns the backend URL the frontend should use to display the photo.
     */
    public String toPhotoUrl(String storedFilename) {
        if (storedFilename == null || storedFilename.isBlank()) return null;
        return "/api/employees/photos/" + storedFilename;
    }

    public byte[] readImage(String storedFilename) throws IOException {
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path file = dir.resolve(safeNameSafe(storedFilename)).normalize();

        if (!file.startsWith(dir) || !Files.exists(file) || Files.isDirectory(file)) {
            return null;
        }

        return Files.readAllBytes(file);
    }

    public MediaType detectMediaType(String filename) {
        String lower = filename == null ? "" : filename.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF;
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private String safeNameSafe(String name) {
        // Avoid path traversal
        return StringUtils.cleanPath(name);
    }
}
