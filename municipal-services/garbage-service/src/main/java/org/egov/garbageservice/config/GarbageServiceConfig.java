package org.egov.garbageservice.config;

import lombok.Getter;
import lombok.Setter;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Spring configuration properties bean holding application settings, external endpoints, Kafka topics, and MDMS configs.
 */
@Component
@Getter
@Setter
public class GarbageServiceConfig {

    @Value("${kafka.topics.save.grbg.account}")
    public String saveGarbageAccountTopic;

    @Value("${frontend.base.uri}")
    public String frontEndBaseUri;

    @Value("${workflow.context.path}")
    public String workflowHost;

    @Value("${workflow.transition.path}")
    public String workflowEndpointTransition;

    @Value("${workflow.business.search}")
    public String workflowBusinessServiceSearchPath;

    @Value("${egov.bill.context.host}")
    public String billHost;

    @Value("${egov.bill.endpoint.fetch}")
    public String fetchBillEndpoint;

    @Value("${egov.bill.endpoint.search}")
    public String searchBillEndpoint;

    @Value("${egov.cancel.bill.endpoint}")
    public String cancleBillEndpoint;

    @Value("${egov.report.host}")
    public String reportHost;

    @Value("${egov.report.endpoint.create}")
    public String reportCreateEndPoint;

    @Value("${egov.alfresco.host}")
    public String alfrescoHost;

    @Value("${egov.alfresco.endpoint.upload}")
    public String alfrescoUploadEndPoint;

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

    // save monthly rent payment topic
    @Value("${save-monthly-rent-payment}")
    private String monthlyRentPaymentSaveTopic;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsSearchEndpoint;

    @Value(("${state.level.tenant.id}"))
    private String stateLevelTenantId;

    @Value(("${kafka.topics.sanatize.failure}"))
    private String sanatizeLogger;

    @Value(("${kafka.topics.bill.tracker.status.update}"))
    private String testKafka;

    @Value("${kafka.topics.update.grbg.account}")
    private String updateGarbageAccountTopic;

    @Value(("${egov.grbg.bill.expiry.after}"))
    private String grbgBillExpiryAfter;

    @Value("${workflow.valid.action.search.path}")
    private String workflowValidActionSearchPath;

    @Value("${egov.mdms.host}")
    private String mdmsServiceHostUrl;

    @Value("${mdms.v2.host}")
    private String mdmsV2Host;

    @Value("${mdms.v2.searchEndpoint}")
    private String mdmsV2SearchEndpoint;

    @Value("${garbage.service.host}")
    private String grbgServiceHostUrl;

    @Value("${garbage.service.pay.now.bill.endpoint}")
    private String grbgPayNowBillEndpoint;

    @Value("${egov.enc.host}")
    private String encServiceHostUrl;

    @Value("${egov.enc.encrypt.endpoint}")
    private String encEncryptEndpoint;

    @Value("${egov.enc.decrypt.endpoint}")
    private String encDecrypyEndpoint;

    @Value("${egov.user.host}")
    private String userServiceHostUrl;

    @Value("${egov.user.search.endpoint}")
    private String userSearchEndpoint;

    @Value("${egov.url.shortning.host}")
    private String urlShortningHost;

    @Value("${egov.url.shortning.context.path}")
    private String urlShortningContextPath;

    @Value("${egov.url.shortning.endpoint}")
    private String urlShortenEndpoint;

    @Value("${egov.update.bill.endpoint}")
    private String updateBillEndpoint;
}