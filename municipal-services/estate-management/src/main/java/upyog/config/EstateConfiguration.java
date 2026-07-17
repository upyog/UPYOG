package upyog.config;

import lombok.*;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Data
@Import({TracerConfiguration.class})
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EstateConfiguration {


    // User Config
    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.context.path}")
    private String userContextPath;

    @Value("${egov.user.create.path}")
    private String userCreateEndpoint;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.user.update.path}")
    private String userUpdateEndpoint;


    //Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${idgen.service.endpoint}")
    private String idGenPath;


    //Workflow Config
    @Value("${egov.workflow.host}")
    private String wfHost;

    @Value("${egov.workflow.transition.path}")
    private String wfTransitionPath;

    @Value("${egov.workflow.businessservice.search.path}")
    private String wfBusinessServiceSearchPath;

    @Value("${egov.workflow.processinstance.search.path}")
    private String wfProcessInstanceSearchPath;


    //MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    //HRMS
    @Value("${egov.hrms.host}")
    private String hrmsHost;

    @Value("${egov.hrms.search.endpoint}")
    private String hrmsEndPoint;


    //URLShortening
    @Value("${egov.url.shortner.host}")
    private String urlShortnerHost;

    @Value("${egov.url.shortner.endpoint}")
    private String urlShortnerEndpoint;


    //SMSNotification
    @Value("${egov.sms.notification.topic}")
    private String smsNotificationTopic;


    @Value("${idgen.estate.management.asset.id.name}")
    private String estateAssetIdName;
    @Value("${idgen.estate.management.asset.id.format}")
    private String estateAssetIdFormat;

    @Value("${save-estate-management-asset-topic}")
    private String estateAssetSaveTopic;

    @Value("${update-estate-management-asset-topic}")
    private String estateAssetUpdateTopic;

    // save allotment topic
    @Value("${save-estate-management-allotment-topic}")
    private String estateAllotmentSaveTopic;

    // save monthly rent payment topic
    @Value("${save-monthly-rent-payment}")
    private String monthlyRentPaymentSaveTopic;

    // save scheduler log topic
    @Value("${save-scheduler-log-topic}")
    private String schedulerLogTopic;


    // EST Business Service Config
    @Value("${est.module.name}")
    private String moduleName;

    @Value("${est.business.service.name}")
    private String businessServiceName;


    // Asset Service Config
    @Value("${egov.asset.service.host}")
    private String assetServiceHost;

    @Value("${egov.asset.service.search.endpoint}")
    private String assetServiceSearchEndpoint;

    // Billing Service Config
    @Value("${egov.billingservice.host}")
    private String billingHost;

    @Value("${egov.demand.create.endpoint}")
    private String demandCreateEndpoint;

    @Value("${egov.demand.update.endpoint}")
    private String demandUpdateEndpoint;

    // Demand Search Endpoint
    @Value("${egov.demand.search.endpoint}")
    private String demandSearchEndpoint;
}
