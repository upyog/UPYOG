package org.upyog.wms.calculator.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CalculatorConfiguration {

    @Value("${wms.service.host}")
    private String wmsHost;

    @Value("${wms.service.search.path}")
    private String wmsSearchPath;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.path}")
    private String mdmsSearchPath;

    @Value("${calculator.mdms.cache.ttl-seconds}")
    private Long mdmsCacheTtlSeconds;

    @Value("${calculator.demand.enabled}")
    private Boolean demandEnabled;

    @Value("${calculator.business-service}")
    private String businessService;

    @Value("${calculator.consumer-type}")
    private String consumerType;

    @Value("${egov.billingservice.host}")
    private String billingHost;

    @Value("${egov.demand.create.path}")
    private String demandCreatePath;

    @Value("${egov.demand.update.path}")
    private String demandUpdatePath;

    @Value("${egov.demand.search.path}")
    private String demandSearchPath;
}
