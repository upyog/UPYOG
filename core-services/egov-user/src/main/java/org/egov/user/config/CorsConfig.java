package org.egov.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * CORS configuration to support HttpOnly cookie authentication
 * Critical: allowCredentials must be true for cookies to work cross-origin
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed.origins:http://localhost:3000,http://localhost:3001}")
    private String[] allowedOrigins;

    @Value("${cors.allowed.methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String[] allowedMethods;

    @Value("${cors.allowed.headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.max.age:3600}")
    private Long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins (do NOT use "*" with credentials)
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));

        // Set allowed headers
        if (allowedHeaders.length == 1 && "*".equals(allowedHeaders[0])) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));
        }

        // CRITICAL: Must be true for cookies to be sent/received
        configuration.setAllowCredentials(true);

        // Expose headers that frontend might need
        configuration.setExposedHeaders(Arrays.asList(
            "x-correlation-id",
            "Authorization",
            "Content-Type",
            "Accept"
        ));

        // Max age for preflight cache
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
