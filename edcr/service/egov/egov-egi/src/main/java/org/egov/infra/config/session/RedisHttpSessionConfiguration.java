package org.egov.infra.config.session;

import org.egov.infra.config.security.authentication.listener.UserSessionDestroyListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import static org.egov.infra.security.utils.SecurityConstants.SESSION_COOKIE_NAME;
import static org.egov.infra.security.utils.SecurityConstants.SESSION_COOKIE_PATH;

/**
 * Spring configuration for Redis-backed distributed HTTP session management.
 * <p>
 * Configures cookie serialization, domain scoping, secure flags, Spring Session registry,
 * and session destruction event listeners across clustered nodes.
 * </p>
 *
 * @author eGovernments Foundation
 */
@Configuration
@EnableRedisIndexedHttpSession
public class RedisHttpSessionConfiguration {

    @Value("${common.domain.name}")
    private String commonDomainName;

    @Value("${secure.cookie}")
    private boolean secureCookie;

    /**
     * Configures the cookie serializer for session tracking across subdomains.
     *
     * @return the configured {@link CookieSerializer}
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(SESSION_COOKIE_NAME);
        serializer.setCookiePath(SESSION_COOKIE_PATH);
        serializer.setUseSecureCookie(secureCookie);
        serializer.setDomainName(commonDomainName);
        return serializer;
    }

    /**
     * Resolves HTTP session IDs from cookies using the configured cookie serializer.
     *
     * @param cookieSerializer the serializer configuring cookie name and path
     * @return the {@link CookieHttpSessionIdResolver} instance
     */
    @Bean
    public CookieHttpSessionIdResolver CookieHttpSessionIdResolver(CookieSerializer cookieSerializer) {
        CookieHttpSessionIdResolver cookieHttpSession = new CookieHttpSessionIdResolver();
        cookieHttpSession.setCookieSerializer(cookieSerializer);
        return cookieHttpSession;
    }

    /**
     * Exposes a Spring Security session registry backed by the Redis session repository.
     *
     * @param sessionRepository the Redis-backed session repository
     * @return the {@link SpringSessionBackedSessionRegistry}
     */
    @Bean
    public SpringSessionBackedSessionRegistry springSessionBackedSessionRegistry(
            FindByIndexNameSessionRepository sessionRepository) {
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }

    /**
     * Exposes a session destroy listener to clean up user contextual resources when sessions expire.
     *
     * @return a new {@link UserSessionDestroyListener} instance
     */
    @Bean
    public UserSessionDestroyListener httpSessionEventPublisher() {
        return new UserSessionDestroyListener();
    }
}