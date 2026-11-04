package org.egov.edcr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
This class is a Spring MVC configuration class whose purpose is not to add functionality,
but to prevent Spring Data from automatically registering DomainClassConverter,
thereby avoiding implicit repository lookups and hidden database queries during request parameter binding.
*/
@Configuration
public class DisableSpringDataWebConfig implements WebMvcConfigurer {
    // Do nothing → prevents DomainClassConverter registration
}