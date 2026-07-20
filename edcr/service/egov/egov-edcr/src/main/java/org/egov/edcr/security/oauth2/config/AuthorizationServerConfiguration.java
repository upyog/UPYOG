package org.egov.edcr.security.oauth2.config;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.edcr.security.oauth2.entity.SecuredClient;
import org.egov.infra.rest.support.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * OAuth2 authorization server configuration
 *
 * @author subhash
 *
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private static final Logger LOGGER = LogManager.getLogger(AuthorizationServerConfiguration.class);
    private static final String CLIENTS_CONFIG = "config/restapi-secured-clients-config.json";
    private static final String CLIENTS_CONFIG_OVERRIDE = "config/restapi-secured-clients-config-override.json";
    private static final String SCOPE_WRITE = "write";
    private static final String SCOPE_READ = "read";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String RESOURCE_ID = "egov-edcr";

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private CustomTokenEnhancer customTokenEnhancer;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        InMemoryClientDetailsServiceBuilder serviceBuilder = clients.inMemory();
        getSecuredClientFromResource().getClients().forEach(client -> {
            try {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Client Id:" + client.getClientId());
                serviceBuilder.withClient(client.getClientId()).secret(client.getClientSecret())
                        .authorizedGrantTypes(GRANT_TYPE_AUTHORIZATION_CODE, GRANT_TYPE_REFRESH_TOKEN,
                                GRANT_TYPE_PASSWORD)
                        .scopes(SCOPE_READ, SCOPE_WRITE).resourceIds(RESOURCE_ID)
                        .accessTokenValiditySeconds(client.getAccessTokenValidity() * 60)
                        .refreshTokenValiditySeconds(client.getRefreshTokenValidity() * 60);
            } catch (Exception e) {
                LOGGER.error("Exception occured while configuring: ", e);
            }
        });
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore).tokenEnhancer(customTokenEnhancer).authenticationManager(authenticationManager)
                .setClientDetailsService(clientDetailsService);
    }

    private SecuredClient getSecuredClientFromResource() {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        InputStream inputStream = null;
        try {
        	inputStream = getClientsConfig().getInputStream();
            return objectMapper.readValue(inputStream, SecuredClient.class);
        } catch (IOException e) {
            LOGGER.error("Exception occured while reading data: ", e);
        } finally {
			IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    private Resource getClientsConfig() {
        Resource res = new ClassPathResource(CLIENTS_CONFIG_OVERRIDE);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Overridden config present:" + res.exists());
        if (!res.exists())
            res = new ClassPathResource(CLIENTS_CONFIG);
        return res;
    }

}