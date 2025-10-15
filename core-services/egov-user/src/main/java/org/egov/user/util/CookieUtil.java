package org.egov.user.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Utility class for managing HttpOnly authentication cookies
 * Provides secure cookie creation and management for token-based authentication
 */
@Component
@Slf4j
public class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Value("${cookie.secure.enabled:true}")
    private boolean secureCookieEnabled;

    @Value("${cookie.same.site:Lax}")
    private String sameSitePolicy;

    @Value("${cookie.domain:}")
    private String cookieDomain;

    @Value("${cookie.max.age:1800}") // 30 minutes default
    private int accessTokenMaxAge;

    @Value("${cookie.refresh.max.age:86400}") // 24 hours default
    private int refreshTokenMaxAge;

    /**
     * Creates and sets access token cookie in HTTP response
     *
     * @param response HttpServletResponse object
     * @param token Access token value
     */
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = createSecureCookie(ACCESS_TOKEN_COOKIE_NAME, token, accessTokenMaxAge, "/");
        response.addCookie(cookie);
        log.debug("Access token cookie set successfully");
    }

    /**
     * Creates and sets refresh token cookie in HTTP response
     *
     * @param response HttpServletResponse object
     * @param token Refresh token value
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = createSecureCookie(REFRESH_TOKEN_COOKIE_NAME, token, refreshTokenMaxAge, "/user/oauth");
        response.addCookie(cookie);
        log.debug("Refresh token cookie set successfully");
    }

    /**
     * Creates a secure HttpOnly cookie with all security flags
     *
     * @param name Cookie name
     * @param value Cookie value
     * @param maxAge Maximum age in seconds
     * @param path Cookie path
     * @return Configured Cookie object
     */
    private Cookie createSecureCookie(String name, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // Prevent JavaScript access (XSS protection)
        cookie.setSecure(secureCookieEnabled); // HTTPS only
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);

        // Set domain if configured (for subdomain sharing)
        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            cookie.setDomain(cookieDomain);
        }

        // Note: SameSite attribute requires Servlet 6.0+ or manual header manipulation
        // For now, we'll add it via response header in the filter

        return cookie;
    }

    /**
     * Extracts access token from cookies in the request
     *
     * @param request HttpServletRequest object
     * @return Access token or null if not found
     */
    public String getAccessTokenFromCookie(HttpServletRequest request) {
        return getCookieValue(request, ACCESS_TOKEN_COOKIE_NAME);
    }

    /**
     * Extracts refresh token from cookies in the request
     *
     * @param request HttpServletRequest object
     * @return Refresh token or null if not found
     */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        return getCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    /**
     * Gets cookie value by name from request
     *
     * @param request HttpServletRequest object
     * @param cookieName Name of the cookie
     * @return Cookie value or null if not found
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    log.debug("Found cookie: {}", cookieName);
                    return cookie.getValue();
                }
            }
        }
        log.debug("Cookie not found: {}", cookieName);
        return null;
    }

    /**
     * Clears authentication cookies by setting maxAge to 0
     *
     * @param response HttpServletResponse object
     */
    public void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, ACCESS_TOKEN_COOKIE_NAME, "/");
        clearCookie(response, REFRESH_TOKEN_COOKIE_NAME, "/user/oauth");
        log.debug("Authentication cookies cleared");
    }

    /**
     * Clears a specific cookie
     *
     * @param response HttpServletResponse object
     * @param cookieName Name of cookie to clear
     * @param path Cookie path
     */
    private void clearCookie(HttpServletResponse response, String cookieName, String path) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookieEnabled);
        cookie.setPath(path);
        cookie.setMaxAge(0); // Delete cookie

        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            cookie.setDomain(cookieDomain);
        }

        response.addCookie(cookie);
    }

    /**
     * Adds SameSite attribute to Set-Cookie header
     * This is a workaround for Servlet versions < 6.0
     *
     * @param response HttpServletResponse object
     */
    public void addSameSiteAttribute(HttpServletResponse response) {
        if (sameSitePolicy != null && !sameSitePolicy.isEmpty()) {
            String setCookieHeader = response.getHeader("Set-Cookie");
            if (setCookieHeader != null && !setCookieHeader.contains("SameSite")) {
                response.setHeader("Set-Cookie", setCookieHeader + "; SameSite=" + sameSitePolicy);
            }
        }
    }
}
