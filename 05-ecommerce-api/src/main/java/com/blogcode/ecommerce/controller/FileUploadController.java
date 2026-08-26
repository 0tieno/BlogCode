package com.blogcode.ecommerce.controller;

import com.blogcode.ecommerce.dto.ImageUploadResponse;
import com.blogcode.ecommerce.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller exposing the product image upload endpoint.
 *
 * <p><strong>Why this class exists:</strong> file uploads use a different
 * HTTP content type ({@code multipart/form-data}) than the rest of this
 * JSON API, so they are isolated in their own controller. This also keeps
 * {@link ProductController} focused purely on product CRUD - the typical
 * client flow is "upload an image to get a URL, then create/update a
 * product with that URL", two separate concerns handled by two endpoints.
 */
@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    /**
     * Accepts a single image file and stores it under the configured
     * upload directory, returning a public URL that can later be attached
     * to a {@code ProductRequest.imageUrl()}.
     *
     * @param file the uploaded multipart file, expected form field name {@code file}
     * @return {@code 201 Created} with the stored file's metadata and public URL
     */
    @PostMapping(value = "/images", consumes = "multipart/form-data")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        ImageUploadResponse response = fileStorageService.store(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
