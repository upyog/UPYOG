
package org.upyog.adv.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.repository.ServiceRequestRepository;
import org.upyog.adv.web.models.billing.GetBillCriteria;

import static org.upyog.adv.constants.BookingConstants.*;

@Component
public class DemandUtil {


    @Autowired
    private BookingConfiguration config;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper mapper;


    public StringBuilder getDemandSearchUrl(GetBillCriteria getBillCriteria) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes())) {
            builder = builder.append(config.getBillingHost())
                    .append(config.getDemandSearchEndpoint()).append(URL_PARAMS_SEPARATER)
                    .append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
                    .append(SEPARATER)
                    .append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(getBillCriteria.getApplicationNumber())
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
}
