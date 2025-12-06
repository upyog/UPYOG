package org.egov.dx.gis.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class GisDxConfiguration {

    // GIS Service Configuration
    @Value("${egov.gis.host}")
    private String gisHost;

    @Value("${egov.gis.points.search.endpoint}")
    private String gisPointsSearchEndpoint;

    @Value("${egov.gis.polygons.search.endpoint}")
    private String gisPolygonsSearchEndpoint;

    // Collection Service Configuration
    @Value("${egov.collection.host}")
    private String collectionHost;

    @Value("${egov.collection.payment.search.endpoint}")
    private String collectionPaymentSearchEndpoint;
}

