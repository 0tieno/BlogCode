package com.blogcode.ecommerce.service.impl;

import com.blogcode.ecommerce.dto.ImageUploadResponse;
import com.blogcode.ecommerce.exception.FileStorageException;
import com.blogcode.ecommerce.service.FileStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Default {@link FileStorageService} implementation that stores uploaded
 * images on the local filesystem, inside the project directory.
 *
 * <p><strong>Why this class exists:</strong> a real e-commerce system needs
 * a place to keep product images. Storing them under a project-local
 * {@code uploads/images} directory (configured via {@code app.upload.dir},
 * see {@code application.yml}) - rather than the OS temp folder - means the
 * files survive application restarts and are easy for students to find and
 * inspect while learning. {@code WebConfig} exposes this same directory as
 * a static resource handler so the returned URL is immediately browsable.
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path uploadDirectory;
    private final String urlPrefix;

    /**
     * Creates the service and eagerly ensures the configured upload
     * directory exists on disk, failing fast at startup rather than on the
     * first upload request if the path is misconfigured or unwritable.
     *
     * @param uploadDir the directory (relative or absolute) uploaded files are stored in
     * @param urlPrefix the public URL prefix files are served under, e.g. {@code /images}
     */
    public FileStorageServiceImpl(
            @Value("${app.upload.dir}") String uploadDir, @Value("${app.upload.url-prefix}") String urlPrefix) {
        this.uploadDirectory = Path.of(uploadDir).toAbsolutePath().normalize();
        this.urlPrefix = urlPrefix;
        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory: " + this.uploadDirectory, e);
        }
        log.info("Product images will be stored under: {}", this.uploadDirectory);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The original file name is never trusted as-is: it is stripped down
     * to just its extension and combined with a random {@link UUID} to
     * build the stored file name. This prevents path traversal attacks
     * (e.g. a file named {@code ../../etc/passwd}) and avoids collisions
     * between two different uploads that happen to share a file name.
     */
    @Override
    public ImageUploadResponse store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Cannot store an empty file", null);
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload");
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }
        String storedFileName = UUID.randomUUID() + extension;

        Path targetPath = uploadDirectory.resolve(storedFileName).normalize();
        if (!targetPath.startsWith(uploadDirectory)) {
            // Defensive check: guarantees the resolved path never escapes
            // the configured upload directory.
            throw new FileStorageException("Invalid file path resolved for upload: " + storedFileName, null);
        }

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store uploaded file: " + originalName, e);
        }

        String publicUrl = urlPrefix + "/" + storedFileName;
        return new ImageUploadResponse(storedFileName, publicUrl, file.getSize());
    }
}
