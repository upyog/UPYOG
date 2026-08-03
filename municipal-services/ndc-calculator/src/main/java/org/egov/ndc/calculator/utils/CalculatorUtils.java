package org.egov.ndc.calculator.utils;


import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.ndc.calculator.config.NDCCalculatorConfig;


import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.ndc.calculator.web.models.bill.GetBillCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static org.egov.ndc.calculator.utils.NDCConstants.*;

@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class CalculatorUtils {

    private final NDCCalculatorConfig configurations;

    private final ObjectMapper mapper;

    private Map<String, Integer> taxHeadApportionPriorityMap;

    private static String timeZone;

    /**
     * Returns the url for mdms search endpoint
     *
     * @return the MDMS search url
     */
    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(configurations.getMdmsHost()).append(configurations.getMdmsSearchEndpoint());
    }

    /**
     * method to create demandsearch url with demand criteria
     *
     * @param getBillCriteria the criteria used to build the demand search url
     * @return the demand search url
     */
    public StringBuilder getDemandSearchUrl(GetBillCriteria getBillCriteria) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes())) {
            builder = builder.append(configurations.getBillingHost())
                    .append(configurations.getDemandSearchEndpoint()).append(URL_PARAMS_SEPARATER)
                    .append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
                    .append(SEPARATER)
                    .append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(getBillCriteria.getApplicantNumber())
                    .append(SEPARATER)
                    .append(DEMAND_STATUS_PARAM).append(DEMAND_STATUS_ACTIVE);
        }
        else {

             builder = builder.append(configurations.getBillingHost())
                    .append(configurations.getDemandSearchEndpoint()).append(URL_PARAMS_SEPARATER)
                    .append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
                    .append(SEPARATER)
                    .append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(StringUtils.join(getBillCriteria.getConsumerCodes(), ","))
                    .append(SEPARATER)
		    .append(PAYMENT_COMPLETED)
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
        return new StringBuilder().append(configurations.getBillingHost()).append(configurations.getDemandUpdateEndpoint());
    }


}
