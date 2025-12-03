package org.egov.noc.calculator.config;

import lombok.Data;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class NOCCalculatorConfig {


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

    @Value("${noc.module.code}")
    private String moduleCode;

    @Value("${noc.taxhead.master.code}")
    private String taxHeadMasterCode;

    @Value("${egov.demand.minimum.payable.amount}")
    private BigDecimal minimumPayableAmount;


    //MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsSearchEndpoint;

//    Kafka Topics
    @Value("${persister.save.noc.topic}")
    private String saveTopic;

    @Value("${egov.noc.host}")
    private String nocHost;
    
    @Value("${egov.noc.context.path}")
    private String nocContextPath;
    
    @Value("${egov.noc.search.endpoint}")
    private String nocSearchEndpoint;

}
