package org.egov.filestore.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.UUID;

/**
 * Web configuration to handle multipart requests properly with TracerFilter.
 *
 * This filter runs BEFORE TracerFilter (order 0 vs order 1) to ensure multipart/form-data
 * requests have correlation IDs set in headers. This prevents TracerFilter from attempting
 * to parse file upload bodies as JSON, which causes UnsupportedMediaTypeException.
 *
 * The filter:
 * 1. Detects multipart requests
 * 2. Extracts or generates correlation ID
 * 3. Wraps request to provide correlation ID in header
 * 4. Allows TracerFilter to process normally without trying to parse body
 */
@Configuration
public class WebConfig {

    /**
     * Registers the multipart request filter with order 0 (before TracerFilter order 1)
     */
    @Bean
    public FilterRegistrationBean<MultipartCorrelationFilter> multipartCorrelationFilter() {
        FilterRegistrationBean<MultipartCorrelationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MultipartCorrelationFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("MultipartCorrelationFilter");
        registrationBean.setOrder(0); // Must run before TracerFilter (which has order 1)
        return registrationBean;
    }

    /**
     * Filter that ensures multipart requests have correlation IDs in headers.
     * This allows TracerFilter to extract correlation ID from headers instead of
     * trying to parse the multipart body as JSON.
     */
    public static class MultipartCorrelationFilter implements Filter {

        private static final String CORRELATION_ID_HEADER = "x-correlation-id";
        private static final String CORRELATION_ID_MDC = "CORRELATION_ID";
        private static final Logger log = LoggerFactory.getLogger(MultipartCorrelationFilter.class);

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String contentType = httpRequest.getContentType();

            // Only handle multipart requests
            if (contentType != null && contentType.startsWith("multipart/form-data")) {

                // Get or generate correlation ID
                String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
                if (correlationId == null || correlationId.trim().isEmpty()) {
                    correlationId = UUID.randomUUID().toString();
                    log.debug("Generated correlation ID for multipart request: {}", correlationId);
                }

                // Set in MDC for logging context
                MDC.put(CORRELATION_ID_MDC, correlationId);

                // Wrap request to ensure correlation ID is available in header
                HttpServletRequest wrappedRequest = new MultipartRequestWrapper(httpRequest, correlationId);

                try {
                    chain.doFilter(wrappedRequest, response);
                } finally {
                    MDC.remove(CORRELATION_ID_MDC);
                }
            } else {
                // Non-multipart requests pass through normally
                chain.doFilter(request, response);
            }
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            log.info("MultipartCorrelationFilter initialized - will handle correlation IDs for multipart requests");
        }

        @Override
        public void destroy() {
            log.info("MultipartCorrelationFilter destroyed");
        }
    }

    /**
     * Request wrapper that provides correlation ID in header for multipart requests.
     * This ensures TracerFilter can read the correlation ID from header instead of
     * attempting to parse it from the multipart body.
     */
    public static class MultipartRequestWrapper extends HttpServletRequestWrapper {

        private static final String CORRELATION_ID_HEADER = "x-correlation-id";
        private final String correlationId;

        public MultipartRequestWrapper(HttpServletRequest request, String correlationId) {
            super(request);
            this.correlationId = correlationId;
        }

        /**
         * Override getHeader to provide correlation ID for TracerFilter
         */
        @Override
        public String getHeader(String name) {
            if (CORRELATION_ID_HEADER.equalsIgnoreCase(name)) {
                return correlationId;
            }
            return super.getHeader(name);
        }
    }
}
