package org.egov.edcr.security.oauth2.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.edcr.security.oauth2.entity.ResourceDetail;
import org.egov.edcr.security.oauth2.entity.SecuredResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Protects URLs configured in restapi-secured-apis-config.json using opaque bearer tokens. */
@Configuration
public class ResourceServerConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(ResourceServerConfiguration.class);
    private static final String APIS_CONFIG = "config/restapi-secured-apis-config.json";
    private static final String APIS_CONFIG_OVERRIDE = "config/restapi-secured-apis-config-override.json";

    private final ObjectMapper objectMapper;

    public ResourceServerConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Required by Spring Security 6 when configuring request matchers to inspect MVC handler mappings.
     */
    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(
            HttpSecurity http, OpaqueTokenIntrospector opaqueTokenIntrospector) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .opaqueToken(opaqueToken -> opaqueToken.introspector(opaqueTokenIntrospector)));

        http.authorizeHttpRequests(authorization -> {
            for (ResourceDetail resource : getSecuredResourceFromResource().getResources()) {
                AntPathRequestMatcher requestMatcher = AntPathRequestMatcher.antMatcher(resource.getUrl());
                if (StringUtils.isNotEmpty(resource.getRoles())) {
                    authorization.requestMatchers(requestMatcher)
                            .access(new WebExpressionAuthorizationManager(resource.getRoles()));
                } else {
                    authorization.requestMatchers(requestMatcher).authenticated();
                }
            }
            authorization.anyRequest().permitAll();
        });

        return http.build();
    }

    /**
     * Replaces the old ResourceServerTokenServices/RedisTokenStore lookup.
     * It validates the opaque token held by the authorization service and
     * reconstructs the authenticated principal and authorities.
     */
    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector(OAuth2AuthorizationService authorizationService) {
        return token -> {
            OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
            if (authorization == null || authorization.getAccessToken() == null || !authorization.getAccessToken().isActive()) {
                throw new BadOpaqueTokenException("Invalid or expired access token");
            }

            Authentication principal = authorization.getAttribute(Principal.class.getName());
            List<GrantedAuthority> authorities = principal != null
                    ? new ArrayList<>(principal.getAuthorities())
                    : authorization.getAuthorizedScopes().stream()
                    .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                    .collect(Collectors.toList());

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", authorization.getPrincipalName());
            attributes.put("scope", String.join(" ", authorization.getAuthorizedScopes()));
            return new DefaultOAuth2AuthenticatedPrincipal(authorization.getPrincipalName(), attributes, authorities);
        };
    }

    private SecuredResource getSecuredResourceFromResource() {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        InputStream inputStream = null;
        try {
            inputStream = getResourcesConfig().getInputStream();
            SecuredResource securedResource = objectMapper.readValue(inputStream, SecuredResource.class);
            if (securedResource == null || securedResource.getResources() == null) {
                throw new IllegalStateException("No protected API resources are configured");
            }
            return securedResource;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read protected API configuration", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private Resource getResourcesConfig() {
        Resource resource = new ClassPathResource(APIS_CONFIG_OVERRIDE);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Overridden config present: " + resource.exists());
        }
        return resource.exists() ? resource : new ClassPathResource(APIS_CONFIG);
    }
}