/**
 * 
 */
package org.egov.search.controller;

import javax.xml.ws.Endpoint;
import org.apache.cxf.metrics.MetricsFeature;
import org.apache.cxf.metrics.MetricsProvider;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.egov.search.service.SearchService;
import org.egov.search.webservice.SearchSoapServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**	
 * 
 */
@Configuration
public class CxfConfig {

	@Autowired
    private Bus bus;

    @Autowired
    private MetricsProvider metricsProvider;
    @Autowired
	private SearchService searchService;

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, new SearchSoapServiceImpl(searchService), null, null, new MetricsFeature[]{
            new MetricsFeature(metricsProvider)
        });
        endpoint.publish("/SearchPMIDC");
        return endpoint;
    }
}
