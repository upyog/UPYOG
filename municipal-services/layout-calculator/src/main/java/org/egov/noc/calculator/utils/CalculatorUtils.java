package org.egov.noc.calculator.utils;


import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.noc.calculator.config.LAYOUTCalculatorConfig;


import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.egov.noc.calculator.web.models.AuditDetails;
import org.egov.noc.calculator.web.models.bill.GetBillCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static org.egov.noc.calculator.utils.LAYOUTConstants.*;

@Slf4j
@Component
@Getter
public class CalculatorUtils {

    @Autowired
    private LAYOUTCalculatorConfig config;

    @Autowired
    private ObjectMapper mapper;

    private Map<String, Integer> taxHeadApportionPriorityMap;


    /**
     * Creates demand Search url based on tenanatId,businessService and ConsumerCode
     * @return demand search url
     */
    public String getDemandSearchURL(String isPaymentCompleted){
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandSearchEndpoint());
        url.append("?");
        url.append("tenantId=");
        url.append("{1}");
        url.append("&");
        url.append("businessService=");
        url.append("{2}");
        url.append("&");
        url.append("consumerCode=");
        url.append("{3}");
        if(!StringUtils.isEmpty(isPaymentCompleted)) {
        	url.append("&");
            url.append("isPaymentCompleted=");
            url.append("{4}");
        }
        return url.toString();
    }


    /**
     * Creates generate bill url using tenantId,consumerCode and businessService
     * @return Bill Generate url
     */
    public String getBillGenerateURI(){
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getBillGenerateEndpoint());
        url.append("?");
        url.append("tenantId=");
        url.append("{1}");
        url.append("&");
        url.append("consumerCode=");
        url.append("{2}");
        url.append("&");
        url.append("businessService=");
        url.append("{3}");

        return url.toString();
    }

    /**
     * method to create demandsearch url with demand criteria
     *
     * @param getBillCriteria
     * @return
     */
    public StringBuilder getDemandSearchUrl(GetBillCriteria getBillCriteria) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes())) {
            builder = builder.append(config.getBillingHost())
                    .append(config.getDemandSearchEndpoint()).append(URL_PARAMS_SEPARATER)
                    .append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
                    .append(SEPARATER)
                    .append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(getBillCriteria.getApplicantNumber())
                    .append(SEPARATER)
                    .append(DEMAND_STATUS_PARAM).append(DEMAND_STATUS_ACTIVE);
        }
        else {

             builder = builder.append(config.getBillingHost())
                    .append(config.getDemandSearchEndpoint()).append(URL_PARAMS_SEPARATER)
                    .append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
                    .append(SEPARATER)
                    .append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(StringUtils.join(getBillCriteria.getConsumerCodes(), ","))
                    .append(SEPARATER)
		    .append(paymentcompleted)
                    .append(SEPARATER)
                    .append(DEMAND_STATUS_PARAM).append(DEMAND_STATUS_ACTIVE);

        }
        if (getBillCriteria.getFromDate() != null && getBillCriteria.getToDate() != null)
            builder = builder.append(DEMAND_START_DATE_PARAM).append(getBillCriteria.getFromDate())
                    .append(SEPARATER)
                    .append(DEMAND_END_DATE_PARAM).append(getBillCriteria.getToDate())
                    .append(SEPARATER);

        return builder;
    }
    
    public StringBuilder getUpdateDemandUrl() {
        return new StringBuilder().append(config.getBillingHost()).append(config.getDemandUpdateEndpoint());
    }
    
    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if(isCreate)
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        else
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }
    
}
