package org.egov.edcr.security.oauth2.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.edcr.security.oauth2.entity.ClientDetail;
import org.egov.edcr.security.oauth2.entity.SecuredClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Spring Security 6 authorization-server configuration.
 *
 * <p>The old {@code AuthorizationServerConfigurerAdapter} API was provided by
 * the end-of-life {@code spring-security-oauth2} project and is not compatible
 * with Spring Security 6. Client definitions remain in the existing JSON file
 * so deployed client ids, secrets, scopes and token lifetimes are unchanged.
 */
@Configuration
public class AuthorizationServerConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(AuthorizationServerConfiguration.class);
    private static final String CLIENTS_CONFIG = "config/restapi-secured-clients-config.json";
    private static final String CLIENTS_CONFIG_OVERRIDE = "config/restapi-secured-clients-config-override.json";
    private static final String SCOPE_WRITE = "write";
    private static final String SCOPE_READ = "read";
    private static final String DEFAULT_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/egov";

    private final ObjectMapper objectMapper;

    public AuthorizationServerConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Registers every client from the legacy JSON configuration with Spring
     * Authorization Server. Password grant is deliberately retained through
     * the custom converter/provider in this package for backwards compatibility.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        List<RegisteredClient> registeredClients = new ArrayList<>();
        for (ClientDetail client : getSecuredClientFromResource().getClients()) {
            registeredClients.add(toRegisteredClient(client));
        }
        return new InMemoryRegisteredClientRepository(registeredClients);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        // This matches the previous in-process configuration for development.
        // Production must replace this bean with a durable JDBC/Redis-backed
        // OAuth2AuthorizationService before a rolling upgrade.
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
        // Retain opaque access tokens. Existing resource-server clients should
        // not be switched to JWT until ResourceServerConfiguration is migrated.
        return new DelegatingOAuth2TokenGenerator(
                new OAuth2AccessTokenGenerator(), new OAuth2RefreshTokenGenerator());
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager,
            OAuth2AuthorizationService authorizationService,
            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                        .accessTokenRequestConverters(converters ->
                                converters.add(0, new OAuth2ResourceOwnerPasswordAuthenticationConverter()))
                        .authenticationProvider(new OAuth2ResourceOwnerPasswordAuthenticationProvider(
                                authenticationManager, authorizationService, tokenGenerator)));

        return http.build();
    }

    private RegisteredClient toRegisteredClient(ClientDetail client) {
        Duration accessTokenTtl = Duration.ofMinutes(client.getAccessTokenValidity());
        Duration refreshTokenTtl = Duration.ofMinutes(client.getRefreshTokenValidity());

        // Spring Authorization Server 1.x strictly requires at least one non-empty redirectUri
        String redirectUri = DEFAULT_REDIRECT_URI;

        return RegisteredClient.withId(UUID.nameUUIDFromBytes(
                        client.getClientId().getBytes(StandardCharsets.UTF_8)).toString())
                .clientId(client.getClientId())
                .clientSecret(withPasswordEncoderId(client.getClientSecret()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(OAuth2ResourceOwnerPasswordAuthenticationToken.PASSWORD)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri(redirectUri) // Satisfies mandatory validation in Spring Security 6
                .scope(SCOPE_READ)
                .scope(SCOPE_WRITE)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(accessTokenTtl)
                        .refreshTokenTimeToLive(refreshTokenTtl)
                        .build())
                .build();
    }

    private String withPasswordEncoderId(String secret) {
        // Legacy client JSON contains plaintext secrets. Keep them usable during
        // migration; replace these values with {bcrypt} hashes after clients move.
        return secret.startsWith("{") ? secret : "{noop}" + secret;
    }

    private SecuredClient getSecuredClientFromResource() {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        InputStream inputStream = null;
        try {
            inputStream = getClientsConfig().getInputStream();
            SecuredClient securedClient = objectMapper.readValue(inputStream, SecuredClient.class);
            if (securedClient == null || securedClient.getClients() == null || securedClient.getClients().isEmpty()) {
                throw new IllegalStateException("No OAuth2 clients are configured");
            }
            return securedClient;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read OAuth2 client configuration", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private Resource getClientsConfig() {
        Resource resource = new ClassPathResource(CLIENTS_CONFIG_OVERRIDE);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Overridden config present: " + resource.exists());
        }
        return resource.exists() ? resource : new ClassPathResource(CLIENTS_CONFIG);
    }
}