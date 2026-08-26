package com.blogcode.ecommerce.dto;

/**
 * Response DTO returned by the image upload endpoint.
 *
 * <p><strong>Why this class exists:</strong> after a client uploads a
 * product image, it needs a stable, publicly reachable URL to store on a
 * {@code Product} (see {@link ProductRequest#imageUrl()}). Returning a tiny,
 * dedicated DTO instead of a raw string keeps the response self-describing
 * and leaves room to add more metadata later (e.g. file size, content type)
 * without breaking existing clients.
 *
 * @param fileName   the generated, collision-free file name stored on disk
 * @param url        the public URL clients can use to fetch the image
 * @param sizeBytes  size of the stored file in bytes
 */
public record ImageUploadResponse(String fileName, String url, long sizeBytes) {
}
