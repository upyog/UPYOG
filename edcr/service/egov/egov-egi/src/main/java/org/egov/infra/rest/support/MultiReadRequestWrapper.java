package org.egov.infra.rest.support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;

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
 * <h3>Multipart handling</h3>
 * <p>
 * For <strong>multipart/form-data</strong> requests the constructor deliberately
 * does <em>not</em> drain {@code getInputStream()}. Eagerly copying the raw
 * channel bytes would consume the same underlying source that Undertow's
 * {@code getParts()} implementation reads from. Once that channel is drained,
 * {@code getParts()} finds nothing, {@code StandardServletMultipartResolver}
 * resolves zero parts, and {@code @RequestPart("planFile")} fails with
 * "Required part not present".
 * </p>
 * <p>
 * Instead, for multipart requests:
 * <ul>
 *   <li>{@code getParts()} / {@code getPart(String)} delegate directly to the
 *       underlying Undertow request, whose raw channel bytes are still intact.</li>
 *   <li>{@code getInputStream()} also delegates to the underlying request
 *       (multipart body is not re-readable as a raw stream; callers must use
 *       {@code getParts()}).</li>
 * </ul>
 * </p>
 *
 * <p>
 * For non-multipart requests the existing caching behaviour (body eagerly read
 * into a byte array, re-served on every {@code getInputStream()} call) is
 * preserved exactly.
 * </p>
 *
 * <p>It also supports adding and retrieving custom request headers.</p>
 */
public class MultiReadRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger LOG = LogManager.getLogger(MultiReadRequestWrapper.class);

    /**
     * Cached body bytes — non-null only for non-multipart requests.
     * {@code null} for multipart requests (body must not be pre-consumed).
     */
    private final byte[] cachedBody;

    /** {@code true} when the wrapped request is multipart/form-data. */
    private final boolean multipart;

    private final Map<String, String> customHeaders;

    /**
     * Creates a request wrapper.
     *
     * <p>For non-multipart requests the body is eagerly cached so that
     * {@link #getInputStream()} can be replayed any number of times.</p>
     *
     * <p>For multipart requests the body is deliberately <em>not</em> cached:
     * the underlying Undertow channel is left untouched so that
     * {@code StandardServletMultipartResolver} can call {@code getParts()} on
     * the container later without finding an already-drained stream.</p>
     *
     * @param request the original HTTP request
     * @throws IOException if an error occurs while reading the body of a
     *                     non-multipart request
     */
    public MultiReadRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.customHeaders = new HashMap<>();

        String contentType = request.getContentType();
        this.multipart = (contentType != null
                && contentType.toLowerCase().startsWith("multipart/"));

        if (!this.multipart) {
            // Non-multipart: eagerly cache body so getInputStream() is replayable.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(request.getInputStream(), baos);
            this.cachedBody = baos.toByteArray();
            LOG.debug("Cached non-multipart request body: {} bytes", cachedBody.length);
        } else {
            // Multipart: do NOT drain the underlying channel.
            // Undertow's getParts() must be able to read the raw channel bytes.
            this.cachedBody = null;
            LOG.debug("Multipart request — body NOT cached; getParts() will delegate to container.");
        }
    }

    // -------------------------------------------------------------------------
    // InputStream / Reader overrides
    // -------------------------------------------------------------------------

    /**
     * For non-multipart requests: returns a fresh {@link ServletInputStream}
     * backed by the cached body bytes on every call (fully replayable).
     *
     * <p>For multipart requests: delegates to the underlying container request.
     * Callers on multipart requests should use {@link #getParts()} instead
     * of this method.</p>
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (!multipart) {
            return new CachedServletInputStream(this.cachedBody);
        }
        // Multipart: delegate to the underlying Undertow request stream.
        // StandardServletMultipartResolver uses getParts(), not getInputStream(),
        // so this path is not reached during normal multipart processing.
        return super.getInputStream();
    }

    /**
     * Returns a {@link BufferedReader} for reading the request body.
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    // -------------------------------------------------------------------------
    // Parts overrides
    // -------------------------------------------------------------------------

    /**
     * Delegates {@code getParts()} directly to the underlying servlet-container
     * request (Undertow) so that multipart bodies are parsed from the raw
     * channel, which this wrapper has not consumed.
     *
     * <p>This is the method called by {@code StandardServletMultipartResolver}
     * when resolving {@code @RequestPart} parameters.</p>
     *
     * @throws IOException      if an I/O error occurs during parsing
     * @throws ServletException if the request is not a multipart request
     */
    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        // Delegate to the underlying request so that Undertow can parse the
        // raw multipart channel (not consumed by our constructor for multipart).
        return ((HttpServletRequest) getRequest()).getParts();
    }

    /**
     * Delegates {@code getPart(String)} to the underlying servlet-container
     * request (Undertow).
     *
     * @param name the part name
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if the request is not a multipart request
     */
    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return ((HttpServletRequest) getRequest()).getPart(name);
    }

    // -------------------------------------------------------------------------
    // Inner class: CachedServletInputStream (non-multipart only)
    // -------------------------------------------------------------------------

    /**
     * {@link ServletInputStream} backed by a cached byte array.
     * Provides repeated access to the request body without consuming the
     * original request stream. Used only for non-multipart requests.
     */
    public class CachedServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream input;

        public CachedServletInputStream(byte[] cachedBody) {
            this.input = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public int read() {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            return input.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // Nothing — synchronous usage only.
        }
    }

    // -------------------------------------------------------------------------
    // Custom header support
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Accessor
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if the wrapped request is a multipart/form-data
     * request.
     */
    public boolean isMultipart() {
        return multipart;
    }
}