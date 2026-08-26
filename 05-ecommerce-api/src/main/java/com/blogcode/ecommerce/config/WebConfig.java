package com.blogcode.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration that exposes uploaded product images as static,
 * browsable HTTP resources.
 *
 * <p><strong>Why this class exists:</strong> {@code FileStorageServiceImpl}
 * writes uploaded files to a directory on disk, but by default Spring MVC
 * has no idea that directory should be reachable over HTTP. This class
 * maps the public URL prefix ({@code app.upload.url-prefix}, e.g.
 * {@code /images/**}) to the physical upload directory
 * ({@code app.upload.dir}) so the URL returned by the upload endpoint
 * ({@code ImageUploadResponse.url()}) actually resolves to the stored file.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;
    private final String urlPrefix;

    /**
     * Creates the configuration, binding the same {@code app.upload.*}
     * properties used by {@code FileStorageServiceImpl} so both classes
     * always agree on where files live and how they are served.
     *
     * @param uploadDir the directory (relative or absolute) uploaded files are stored in
     * @param urlPrefix the public URL prefix files are served under, e.g. {@code /images}
     */
    public WebConfig(@Value("${app.upload.dir}") String uploadDir, @Value("${app.upload.url-prefix}") String urlPrefix) {
        this.uploadDir = uploadDir;
        this.urlPrefix = urlPrefix;
    }

    /**
     * Registers a resource handler that serves files from the local upload
     * directory under the configured public URL prefix.
     *
     * @param registry Spring MVC's registry of static resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "file:" + trailing slash tells Spring this is a filesystem path,
        // not a classpath resource, and must resolve relative to the
        // application's working directory - the same directory
        // FileStorageServiceImpl resolves uploadDir against.
        String location = "file:" + uploadDir + "/";
        registry.addResourceHandler(urlPrefix + "/**").addResourceLocations(location);
    }
}
