package com.example.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Spring Boot 3.x Gateway Configuration for Multipart File Uploads
 *
 * This configuration ensures proper handling of multipart/form-data requests
 * through the API Gateway without corruption or truncation.
 *
 * Key configurations:
 * - Increases max in-memory size to 100MB (from default 256KB)
 * - Configures multipart reader with appropriate limits
 * - Prevents logging of file content (memory optimization)
 *
 * Required for Spring Boot 3.x compatibility with large file uploads.
 */
@Configuration
public class MultipartGatewayConfig implements WebFluxConfigurer {

    // 100MB in bytes
    private static final int MAX_IN_MEMORY_SIZE = 104857600;

    // 50MB in bytes - max size per part when written to disk
    private static final int MAX_DISK_USAGE_PER_PART = 52428800;

    // Maximum number of parts in multipart request (files + form fields)
    private static final int MAX_PARTS = 10;

    /**
     * Configure HTTP message codecs to handle large multipart requests
     *
     * Spring Boot 3.x has stricter memory limits by default.
     * We increase them here to support file uploads up to 100MB.
     *
     * @param configurer the ServerCodecConfigurer to configure
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        // Set overall codec max in-memory size
        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE);

        // Configure part reader with custom settings
        DefaultPartHttpMessageReader partReader = new DefaultPartHttpMessageReader();
        partReader.setMaxInMemorySize(MAX_IN_MEMORY_SIZE);
        partReader.setMaxParts(MAX_PARTS);
        partReader.setMaxDiskUsagePerPart(MAX_DISK_USAGE_PER_PART);

        // Disable logging of request details to prevent memory issues with large files
        partReader.setEnableLoggingRequestDetails(false);

        // Create multipart reader with the configured part reader
        MultipartHttpMessageReader multipartReader = new MultipartHttpMessageReader(partReader);
        multipartReader.setMaxInMemorySize(MAX_IN_MEMORY_SIZE);

        // Register the multipart reader
        configurer.defaultCodecs().register(multipartReader);
    }
}
