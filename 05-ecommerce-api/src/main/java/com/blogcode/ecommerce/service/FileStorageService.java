package com.blogcode.ecommerce.service;

import com.blogcode.ecommerce.dto.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Business-logic contract for storing uploaded product images on local
 * disk and exposing them under a public URL.
 *
 * <p><strong>Why this class exists:</strong> hiding the storage mechanism
 * (today: the local filesystem under {@code app.upload.dir}) behind an
 * interface means it could later be swapped for an S3-backed or Azure
 * Blob-backed implementation without any controller changes - a realistic
 * evolution path for a growing e-commerce system.
 */
public interface FileStorageService {

    /**
     * Stores the given multipart file under a generated, collision-free
     * name and returns metadata describing where it can be retrieved from.
     *
     * @param file the uploaded image file
     * @return metadata including the public URL clients should use
     * @throws com.blogcode.ecommerce.exception.FileStorageException if the file cannot be stored
     */
    ImageUploadResponse store(MultipartFile file);
}
