package org.egov.infra.rest.support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.web.savedrequest.Enumerator;
/**
 * HttpServletRequest wrapper that allows the request body to be read
 * multiple times by caching the request payload in memory.
 *
 * <p>
 * The standard HttpServletRequest input stream can be consumed only once.
 * This wrapper reads and stores the request body during construction and
 * provides fresh input streams/readers backed by the cached content for
 * subsequent reads.
 * </p>
 *
 * <p>
 * It also supports adding and retrieving custom request headers.
 * </p>
 */
public class MultiReadRequestWrapper extends HttpServletRequestWrapper {
    
    private static final Logger LOG = LogManager.getLogger(MultiReadRequestWrapper.class);
    
    private final byte[] cachedBody; // Change to byte[] and make final
    private final Map<String, String> customHeaders;

    /**
     * Creates a request wrapper and caches the request body in memory.
     * @param request the original HTTP request
     * @throws IOException if an error occurs while reading the request body
     */
    public MultiReadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.customHeaders = new HashMap<>();
        // EAGERLY cache body in constructor before anything else reads it
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(request.getInputStream(), baos);
        this.cachedBody = baos.toByteArray();
        LOG.debug("Cached request body size: {} bytes", cachedBody.length);
    }
    /**
     * Returns a new ServletInputStream backed by the cached request body.
     * @return cached request body input stream
     */
    @Override
    public ServletInputStream getInputStream() {
        // Always returns fresh stream from cached body
        return new CachedServletInputStream(this.cachedBody);
    }
    /**
     * Returns a BufferedReader for reading the cached request body.
     * @return reader for the cached request content
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    // Update CachedServletInputStream to take byte[] parameter
    public class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream input;
        /**
         * ServletInputStream implementation backed by a cached byte array.
         * Provides repeated access to the request body without consuming
         * the original request stream.
         */
        public CachedServletInputStream(byte[] cachedBody) {
            this.input = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public int read() {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            return input.available() == 0; // Fix this too - was always returning false!
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // Nothing
        }
    }

    public void putHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = customHeaders.get(name);
        if (headerValue != null) {
            return headerValue;
        }
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<>(customHeaders.keySet());
        Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }
        return Collections.enumeration(set);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String headerValue = customHeaders.get(name);
        if (headerValue != null) {
            return new Enumerator<>(Arrays.asList(headerValue));
        }
        return super.getHeaders(name);
    }
}