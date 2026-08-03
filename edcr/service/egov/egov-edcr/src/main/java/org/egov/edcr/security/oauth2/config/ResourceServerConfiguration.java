package org.egov.edcr.security.oauth2.config;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.edcr.security.oauth2.entity.SecuredResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration to protect the Api resources with Oauth2 Security
 *
 * @author subhash
 *
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final Logger LOGGER = LogManager.getLogger(ResourceServerConfiguration.class);
    private static final String APIS_CONFIG = "config/restapi-secured-apis-config.json";
    private static final String APIS_CONFIG_OVERRIDE = "config/restapi-secured-apis-config-override.json";
    private static final String RESOURCE_ID = "egov-edcr";

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) {
        http.requestMatchers().and();
        configurePatterns(http);
        try {
            http.exceptionHandling()
                    .accessDeniedHandler(new OAuth2AccessDeniedHandler());
        } catch (Exception e) {
            LOGGER.error("Exception occured while authenticating: ", e);
        }
    }

    private void configurePatterns(HttpSecurity http) {

        getSecuredResourceFromResource().getResources().forEach(record -> {
            try {
                ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl authorizedUrl = http.authorizeRequests()
                        .antMatchers(record.getUrl());
                if (StringUtils.isNotEmpty(record.getRoles()))
                    authorizedUrl.access(record.getRoles());
                else
                    authorizedUrl.authenticated();
            } catch (Exception e) {
                LOGGER.error("Exception occured while configuring: ", e);
            }
        });
    }

    private SecuredResource getSecuredResourceFromResource() {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        InputStream inputStream = null;
        try {
        	inputStream = getResourcesConfig().getInputStream();
            return objectMapper.readValue(inputStream,
                    SecuredResource.class);
        } catch (IOException e) {
            LOGGER.error("Exception occured while reading data: ", e);
        } finally {
			IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    private Resource getResourcesConfig() {
        Resource res = new ClassPathResource(APIS_CONFIG_OVERRIDE);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Overridden config present:" + res.exists());
        if (!res.exists())
            res = new ClassPathResource(APIS_CONFIG);
        return res;
    }

}