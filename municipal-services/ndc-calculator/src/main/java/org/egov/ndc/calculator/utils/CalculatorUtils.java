package org.egov.ndc.calculator.utils;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.ndc.calculator.config.NDCCalculatorConfig;


import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.egov.ndc.calculator.web.models.bill.GetBillCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static org.egov.ndc.calculator.utils.NDCConstants.*;

@Slf4j
@Component
@Getter
public class CalculatorUtils {

    @Autowired
    private NDCCalculatorConfig configurations;



    @Autowired
    private ObjectMapper mapper;

    private Map<String, Integer> taxHeadApportionPriorityMap;

    private static String timeZone;

//    @Value("${id.timezone}")
//    public  void setTimeZone(String zone){
//        CalculatorUtils.timeZone = zone;
//    }
//
//    public Map<String, Integer> getTaxHeadApportionPriorityMap() {
//
//        if (null == taxHeadApportionPriorityMap) {
//            Map<String, Integer> map = new HashMap<>();
//            map.put(CalculatorConstants.PT_TAX, 3);
//            map.put(CalculatorConstants.PT_TIME_PENALTY, 1);
//            map.put(CalculatorConstants.PT_FIRE_CESS, 2);
//            map.put(CalculatorConstants.PT_TIME_INTEREST, 0);
//            map.put(CalculatorConstants.MAX_PRIORITY_VALUE, 100);
//        }
//        return taxHeadApportionPriorityMap;
//    }

    /**
     * Prepares and returns Mdms search request with financial master criteria
     *
     * @param requestInfo
     * @param assesmentYear
     * @return
     */
//    public MdmsCriteriaReq getFinancialYearRequest(RequestInfo requestInfo, String assesmentYear, String tenantId) {
//
//        MasterDetail mstrDetail = MasterDetail.builder().name(CalculatorConstants.FINANCIAL_YEAR_MASTER)
//                .filter("[?(@." + CalculatorConstants.FINANCIAL_YEAR_RANGE_FEILD_NAME + " IN [" + assesmentYear + "])]")
//                .build();
//        ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(CalculatorConstants.FINANCIAL_MODULE)
//                .masterDetails(Arrays.asList(mstrDetail)).build();
//        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId(tenantId)
//                .build();
//        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
//    }

    /**
     * Prepares and returns Mdms search request with financial master criteria
     *
     * @param requestInfo
     * @param assesmentYears
     * @return
     */
//    public MdmsCriteriaReq getFinancialYearRequest(RequestInfo requestInfo, Set<String> assesmentYears, String tenantId) {
//
//        String assessmentYearStr = StringUtils.join(assesmentYears, ",");
//        MasterDetail mstrDetail = MasterDetail.builder().name(CalculatorConstants.FINANCIAL_YEAR_MASTER)
//                .filter("[?(@." + CalculatorConstants.FINANCIAL_YEAR_RANGE_FEILD_NAME + " IN [" + assessmentYearStr + "]" +
//                        " && @.module== '" + SERVICE_FIELD_VALUE_PT + "')]")
//                .build();
//        ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(CalculatorConstants.FINANCIAL_MODULE)
//                .masterDetails(Arrays.asList(mstrDetail)).build();
//        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId(tenantId)
//                .build();
//        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
//    }
//
//    /**
//     * Methods provides all the usage category master for property tax module
//     */
//    public MdmsCriteriaReq getPropertyModuleRequest(RequestInfo requestInfo, String tenantId) {
//
//        List<MasterDetail> details = new ArrayList<>();
//
//        details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_MAJOR_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_MINOR_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_SUB_MINOR_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_DETAIL_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.OWNER_TYPE_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.REBATE_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.PENANLTY_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.FIRE_CESS_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.CANCER_CESS_MASTER).build());
//        details.add(MasterDetail.builder().name(CalculatorConstants.INTEREST_MASTER).build());
//        ModuleDetail mdDtl = ModuleDetail.builder().masterDetails(details)
//                .moduleName(CalculatorConstants.PROPERTY_TAX_MODULE).build();
//        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(mdDtl)).tenantId(tenantId)
//                .build();
//        return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
//    }

    /**
     * Returns the url for mdms search endpoint
     *
     * @return
     */
    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(configurations.getMdmsHost()).append(configurations.getMdmsSearchEndpoint());
    }

    /**
     * Returns the tax head search Url with tenantId and PropertyTax service name
     * parameters
     *
     * @param tenantId
     * @return
//     */
//    public StringBuilder getTaxHeadSearchUrl(String tenantId) {
//
//        return new StringBuilder().append(configurations.getBillingServiceHost())
//                .append(configurations.getTaxheadsSearchEndpoint()).append(URL_PARAMS_SEPARATER)
//                .append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
//                .append(SEPARATER).append(SERVICE_FIELD_FOR_SEARCH_URL)
//                .append(SERVICE_FIELD_VALUE_PT);
//    }

    /**
     * Returns the tax head search Url with tenantId and PropertyTax service name
     * parameters
     *
     * @param tenantId
     * @return
     */
//    public StringBuilder getTaxPeriodSearchUrl(String tenantId) {
//
//        return new StringBuilder().append(configurations.getBillingServiceHost())
//                .append(configurations.getTaxPeriodSearchEndpoint()).append(URL_PARAMS_SEPARATER)
//                .append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
//                .append(SEPARATER).append(SERVICE_FIELD_FOR_SEARCH_URL)
//                .append(SERVICE_FIELD_VALUE_PT);
//    }

    /**
     * Returns the Receipt search Url with tenantId and cosumerCode service name
     * parameters
     *
     * @param tenantId
     * @return
     */
//    public StringBuilder getReceiptSearchUrl(String tenantId, List<String> consumerCodes) {
//
//        return new StringBuilder().append(configurations.getCollectionServiceHost())
//                .append(configurations.getReceiptSearchEndpoint()).append(CalculatorConstants.URL_PARAMS_SEPARATER)
//                .append(CalculatorConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
//                .append(CalculatorConstants.SEPARATER).append(CalculatorConstants.CONSUMER_CODE_SEARCH_FIELD_NAME)
//                .append(consumerCodes.toString().replace("[", "").replace("]", ""))
//                .append(CalculatorConstants.SEPARATER).append(STATUS_FIELD_FOR_SEARCH_URL)
//                .append(ALLOWED_RECEIPT_STATUS);
//    }


    /**
     * Returns the Receipt search Url with tenantId, cosumerCode,service name and tax period
     * parameters
     *
     * @param criteria
     * @return
     */
//    public StringBuilder getReceiptSearchUrl(ReceiptSearchCriteria criteria) {
//
//
//        return new StringBuilder().append(configurations.getCollectionServiceHost())
//                .append(configurations.getReceiptSearchEndpoint()).append(URL_PARAMS_SEPARATER)
//                .append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(criteria.getTenantId())
//                .append(SEPARATER).append(CONSUMER_CODE_SEARCH_FIELD_NAME)
//                .append(criteria.getPropertyId())
//                .append(SEPARATER).append(RECEIPT_START_DATE_PARAM)
//                .append(criteria.getFromDate())
//                .append(SEPARATER).append(RECEIPT_END_DATE_PARAM)
//                .append(criteria.getToDate())
//                .append(CalculatorConstants.SEPARATER).append(STATUS_FIELD_FOR_SEARCH_URL)
//                .append(ALLOWED_RECEIPT_STATUS);
//    }
	/**
	 * Returns the Receipt search Url with tenantId, cosumerCode,service name and
	 * tax period parameters
	 *
	 * @param criteria
	 * @return
	 */

    /**
     * method to create demandsearch url with demand criteria
     *
     * @param getBillCriteria
     * @return
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
        return new StringBuilder().append(configurations.getBillingHost()).append(configurations.getDemandUpdateEndpoint());
    }


}
