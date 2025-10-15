package org.egov.gis.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class GisConfiguration {

    // Property Service Configuration
    @Value("${egov.property.host}")
    private String propertyHost;

    @Value("${egov.property.search.endpoint}")
    private String propertySearchEndpoint;

    // Asset Service Configuration
    @Value("${egov.asset.host}")
    private String assetHost;

    @Value("${egov.asset.search.endpoint}")
    private String assetSearchEndpoint;

}

