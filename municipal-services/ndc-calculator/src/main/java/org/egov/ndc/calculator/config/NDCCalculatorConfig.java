package org.egov.ndc.calculator.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class NDCCalculatorConfig {


    @Value("${egov.billingservice.host}")
    private String billingHost;

    @Value("${egov.taxhead.search.endpoint}")
    private String taxHeadSearchEndpoint;

    @Value("${egov.taxperiod.search.endpoint}")
    private String taxPeriodSearchEndpoint;

    @Value("${egov.demand.create.endpoint}")
    private String demandCreateEndpoint;

    @Value("${egov.demand.update.endpoint}")
    private String demandUpdateEndpoint;

    @Value("${egov.demand.search.endpoint}")
    private String demandSearchEndpoint;

    @Value("${egov.bill.gen.endpoint}")
    private String billGenerateEndpoint;

    @Value("${ndc.module.code}")
    private String moduleCode;

    @Value("${ndc.taxhead.master.code}")
    private String taxHeadMasterCode;

    //MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsSearchEndpoint;

    //Kafka Topics
    @Value("${persister.save.ndc.topic}")
    private String saveTopic;


}
