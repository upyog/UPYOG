package org.egov.garbageservice.config;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class GarbageServiceConfig {

    @Value("${egov.grbg.business.service}")
    private String businessService;

    @Value("${egov.grbg.module.name}")
    private String moduleName;

    @Value("${egov.billing.host}")
    private String billingHost;

    @Value("${egov.demand.create.endpoint}")
    private String demandCreateEndpoint;

    @Value("${egov.demand.update.endpoint}")
    private String demandUpdateEndpoint;

    @Value("${egov.demand.search.endpoint}")
    private String demandSearchEndpoint;

    @Value("${egov.grbg.penalty.rate:0.10}")
    private BigDecimal penaltyRate;

    @Value("${kafka.topics.scheduler.log}")
    private String schedulerLogTopic;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsSearchEndpoint;
}