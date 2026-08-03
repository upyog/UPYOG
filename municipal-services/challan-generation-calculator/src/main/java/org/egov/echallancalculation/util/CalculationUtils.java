package org.egov.echallancalculation.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.echallancalculation.config.ChallanConfiguration;
import org.egov.echallancalculation.model.AuditDetails;
import org.egov.echallancalculation.model.Challan;
import org.egov.echallancalculation.model.ChallanResponse;
import org.egov.echallancalculation.model.RequestInfoWrapper;
import org.egov.echallancalculation.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class CalculationUtils {

    private static final String TENANT_ID_PARAM = "tenantId=";
    private static final String REQUEST_INFO = "RequestInfo";
    private static final String TENANT_ID = "tenantId";
    private static final String MODULE_NAME = "moduleName";
    private static final String MODULE_CHALLAN = "Challan";
    private static final String MASTER_NAME = "masterName";
    private static final String MASTER_SUB_CATEGORY = "SubCategory";
    private static final String MASTER_OFFENCE_TYPE = "OffenceType";
    private static final String FILTER = "filter";
    private static final String FILTER_NAME_PREFIX = "[?(@.name=='";
    private static final String FILTER_OFFENCE_TYPE_PREFIX = "[?(@.offenceTypeId=='";
    private static final String MASTER_DETAILS = "masterDetails";
    private static final String MODULE_DETAILS = "moduleDetails";
    private static final String MDMS_CRITERIA = "MdmsCriteria";
    private static final String MDMS_RES = "MdmsRes";
    private static final String DEFAULT_TAX_HEAD_CODE = "CH.CHALLAN_FINE";
    private static final String MODULE_BILLING_SERVICE = "BillingService";
    private static final String MASTER_TAX_HEAD = "TaxHeadMaster";
    private static final String FILTER_CODE_PREFIX = "[?(@.code=='";

    private final ChallanConfiguration config;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ObjectMapper mapper;


    /**
     * Creates tradeLicense search url based on tenantId and applicationNumber
     * @return tradeLicense search url
     */
  private String getChallanSearchURL(){
      StringBuilder url = new StringBuilder(config.getChallanHost());
      url.append(config.getChallanContextPath());
      url.append(config.getChallansearchEndPoint());
      url.append("?");
      url.append(TENANT_ID_PARAM);
      url.append("{1}");
      url.append("&");
      url.append("applicationNumber=");
      url.append("{2}");
      return url.toString();
  }


    /**
     * Creates demand Search url based on tenanatId,businessService and ConsumerCode
     * @return demand search url
     */
    public String getDemandSearchURL(){
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandSearchEndpoint());
        url.append("?");
        url.append(TENANT_ID_PARAM);
        url.append("{1}");
        url.append("&");
        url.append("businessService=");
        url.append("{2}");
        url.append("&");
        url.append("consumerCode=");
        url.append("{3}");
        return url.toString();
    }


    /**
     * Creates generate bill url using tenantId,consumerCode and businessService
     * @return Bill Generate url
     */
    public String getBillGenerateURI(){
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getFetchBillEndpoint());
        url.append("?");
        url.append(TENANT_ID_PARAM);
        url.append("{1}");
        url.append("&");
        url.append("consumerCode=");
        url.append("{2}");
        url.append("&");
        url.append("businessService=");
        url.append("{3}");

        return url.toString();
    }

    public AuditDetails getAuditDetails(String by, boolean isCreate) {
        Long time = System.currentTimeMillis();
        if(isCreate)
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        else
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }

    public Challan getChallan(RequestInfo requestInfo, String challanNo, String tenantId){
        String url = getChallanSearchURL();
        url = url.replace("{1}",tenantId).replace("{2}",challanNo);

        Object result = serviceRequestRepository.fetchResult(new StringBuilder(url),RequestInfoWrapper.builder().
                requestInfo(requestInfo).build()).orElse(null);

        ChallanResponse response =null;
        try {
                response = mapper.convertValue(result,ChallanResponse.class);
        }
        catch (IllegalArgumentException e){
            throw new CustomException("PARSING ERROR","Error while parsing response of challan Search");
        }

        if(response==null || CollectionUtils.isEmpty(response.getChallans()))
            return null;

        return response.getChallans().get(0);
    }

    /**
     * Dynamically maps offence subcategory name to subcategory ID by fetching from MDMS
     * @param requestInfo The RequestInfo of the calculation request
     * @param tenantId The tenantId
     * @param offenceSubCategoryName The offence subcategory name
     * @return Mapped subcategory ID from MDMS
     */
    public String mapSubCategoryNameToId(RequestInfo requestInfo, String tenantId, String offenceSubCategoryName) {
        if (offenceSubCategoryName == null || offenceSubCategoryName.isEmpty()) {
            return null;
        }
        
        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost());
        uri.append(config.getMdmsEndPoint());

        Map<String, Object> request = buildMdmsRequest(requestInfo, tenantId, MASTER_SUB_CATEGORY,
                FILTER_NAME_PREFIX + offenceSubCategoryName + "')]");

        Object result = serviceRequestRepository.fetchResult(uri, request).orElse(null);
        
        try {
            Map<String, Object> response = mapper.convertValue(result, Map.class);
            Map<String, Object> mdmsRes = (Map<String, Object>) response.get(MDMS_RES);
            Map<String, Object> challan = (Map<String, Object>) mdmsRes.get(MODULE_CHALLAN);
            List<Map<String, Object>> subCategories = (List<Map<String, Object>>) challan.get(MASTER_SUB_CATEGORY);
            
            if (!CollectionUtils.isEmpty(subCategories)) {
                Map<String, Object> subCategory = subCategories.get(0);
                String subCategoryId = (String) subCategory.get("id");
                log.info("Mapped offence subcategory '{}' to ID: {}", offenceSubCategoryName, subCategoryId);
                return subCategoryId;
            }
        } catch (Exception e) {
            log.error("Error fetching subcategory mapping from MDMS for: {}", offenceSubCategoryName, e);
        }
        
        log.warn("No mapping found in MDMS for offence subcategory: {}", offenceSubCategoryName);
        return null;
    }

    /**
     * Dynamically maps offence type name to offence type ID by fetching from MDMS
     * @param requestInfo The RequestInfo of the calculation request
     * @param tenantId The tenantId
     * @param offenceTypeName The offence type name
     * @return Mapped offence type ID from MDMS
     */
    public String mapOffenceTypeNameToId(RequestInfo requestInfo, String tenantId, String offenceTypeName) {
        if (offenceTypeName == null || offenceTypeName.isEmpty()) {
            return null;
        }
        
        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost());
        uri.append(config.getMdmsEndPoint());

        Map<String, Object> request = buildMdmsRequest(requestInfo, tenantId, MASTER_OFFENCE_TYPE,
                FILTER_NAME_PREFIX + offenceTypeName + "')]");

        Object result = serviceRequestRepository.fetchResult(uri, request).orElse(null);
        
        try {
            Map<String, Object> response = mapper.convertValue(result, Map.class);
            Map<String, Object> mdmsRes = (Map<String, Object>) response.get(MDMS_RES);
            Map<String, Object> challan = (Map<String, Object>) mdmsRes.get(MODULE_CHALLAN);
            List<Map<String, Object>> offenceTypes = (List<Map<String, Object>>) challan.get(MASTER_OFFENCE_TYPE);
            
            if (!CollectionUtils.isEmpty(offenceTypes)) {
                Map<String, Object> offenceType = offenceTypes.get(0);
                String offenceTypeId = (String) offenceType.get("id");
                log.info("Mapped offence type '{}' to ID: {}", offenceTypeName, offenceTypeId);
                return offenceTypeId;
            }
        } catch (Exception e) {
            log.error("Error fetching offence type mapping from MDMS for: {}", offenceTypeName, e);
        }
        
        log.warn("No mapping found in MDMS for offence type: {}", offenceTypeName);
        return null;
    }

    /**
     * Fetches rates from MDMS based on offence type
     * @param requestInfo The RequestInfo of the calculation request
     * @param tenantId The tenantId
     * @param offenceTypeId The offence type ID
     * @return Rate amount for the offence type
     */
    public BigDecimal getRateFromMDMS(RequestInfo requestInfo, String tenantId, String offenceTypeId) {
        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost());
        uri.append(config.getMdmsEndPoint());

        Map<String, Object> request = buildMdmsRequest(requestInfo, tenantId, "Rates",
                FILTER_OFFENCE_TYPE_PREFIX + offenceTypeId + "')]");

        Object result = serviceRequestRepository.fetchResult(uri, request).orElse(null);
        
        try {
            Map<String, Object> response = mapper.convertValue(result, Map.class);
            Map<String, Object> mdmsRes = (Map<String, Object>) response.get(MDMS_RES);
            Map<String, Object> challan = (Map<String, Object>) mdmsRes.get(MODULE_CHALLAN);
            List<Map<String, Object>> rates = (List<Map<String, Object>>) challan.get("Rates");
            
            if (!CollectionUtils.isEmpty(rates)) {
                Map<String, Object> rate = rates.get(0);
                Object amount = rate.get("amount");
                if (amount instanceof Number number) {
                    return BigDecimal.valueOf(number.doubleValue());
                }
            }
        } catch (Exception e) {
            throw new CustomException("MDMS_ERROR", "Failed to fetch rate from MDMS for offence type: " + offenceTypeId);
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Fetches tax head code from MDMS based on offence type name
     * @param requestInfo The RequestInfo of the calculation request
     * @param tenantId The tenantId
     * @param offenceTypeName The offence type name
     * @return Tax head code for the offence type
     */
    public String getTaxHeadCodeFromOffenceTypeName(RequestInfo requestInfo, String tenantId, String offenceTypeName) {
        if (offenceTypeName == null || offenceTypeName.isEmpty()) {
            return DEFAULT_TAX_HEAD_CODE;
        }
        
        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost());
        uri.append(config.getMdmsEndPoint());

        Map<String, Object> request = buildMdmsRequest(requestInfo, tenantId, MASTER_OFFENCE_TYPE,
                FILTER_NAME_PREFIX + offenceTypeName + "')]");

        Object result = serviceRequestRepository.fetchResult(uri, request).orElse(null);
        
        try {
            Map<String, Object> response = mapper.convertValue(result, Map.class);
            Map<String, Object> mdmsRes = (Map<String, Object>) response.get(MDMS_RES);
            Map<String, Object> challan = (Map<String, Object>) mdmsRes.get(MODULE_CHALLAN);
            List<Map<String, Object>> offenceTypes = (List<Map<String, Object>>) challan.get(MASTER_OFFENCE_TYPE);
            
            if (!CollectionUtils.isEmpty(offenceTypes)) {
                Map<String, Object> offenceType = offenceTypes.get(0);
                String taxHeadCode = (String) offenceType.get("taxHeadCode");
                if (taxHeadCode != null && !taxHeadCode.isEmpty()) {
                    log.info("Fetched tax head code '{}' from OffenceType '{}'", taxHeadCode, offenceTypeName);
                    return taxHeadCode;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching tax head code from MDMS for offence type: {}", offenceTypeName, e);
        }
        
        log.warn("No tax head code found in MDMS for offence type: {}, using default", offenceTypeName);
        return DEFAULT_TAX_HEAD_CODE;
    }

    /**
     * Fetches tax head code from MDMS based on category
     * @param requestInfo The RequestInfo of the calculation request
     * @param tenantId The tenantId
     * @param categoryCode The category code
     * @return Tax head code for the category
     */
    public String getTaxHeadCodeFromMDMS(RequestInfo requestInfo, String tenantId, String categoryCode) {
        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost());
        uri.append(config.getMdmsEndPoint());

        Map<String, Object> request = new HashMap<>();
        request.put(REQUEST_INFO, requestInfo);
        
        Map<String, Object> mdmsCriteria = new HashMap<>();
        mdmsCriteria.put(TENANT_ID, tenantId);
        Map<String, Object> moduleDetail2 = new HashMap<>();
        moduleDetail2.put(MODULE_NAME, MODULE_BILLING_SERVICE);
        
        Map<String, Object> masterDetail2 = new HashMap<>();
        masterDetail2.put(MASTER_NAME, MASTER_TAX_HEAD);
        masterDetail2.put(FILTER, FILTER_CODE_PREFIX + categoryCode + "')]");
        
        moduleDetail2.put(MASTER_DETAILS, Arrays.asList(masterDetail2));
        mdmsCriteria.put(MODULE_DETAILS, Arrays.asList(moduleDetail2));
        
        request.put(MDMS_CRITERIA, mdmsCriteria);

        Object result = serviceRequestRepository.fetchResult(uri, request).orElse(null);
        
        try {
            Map<String, Object> response = mapper.convertValue(result, Map.class);
            Map<String, Object> mdmsRes = (Map<String, Object>) response.get(MDMS_RES);
            Map<String, Object> billingService = (Map<String, Object>) mdmsRes.get(MODULE_BILLING_SERVICE);
            List<Map<String, Object>> taxHeadMasters = (List<Map<String, Object>>) billingService.get(MASTER_TAX_HEAD);
            
            if (!CollectionUtils.isEmpty(taxHeadMasters)) {
                Map<String, Object> taxHeadMaster = taxHeadMasters.get(0);
                return (String) taxHeadMaster.get("code");
            }
        } catch (Exception e) {
            throw new CustomException("MDMS_ERROR", "Failed to fetch tax head code from MDMS for category: " + categoryCode);
        }
        
        return categoryCode;
    }

    /**
     * Fetches all subcategories from MDMS for debugging and monitoring
     * @param requestInfo The RequestInfo of the calculation request
     * @param tenantId The tenantId
     * @return List of all subcategories from MDMS
     */
    public List<Map<String, Object>> getAllSubCategoriesFromMDMS(RequestInfo requestInfo, String tenantId) {
        StringBuilder uri = new StringBuilder();
        uri.append(config.getMdmsHost());
        uri.append(config.getMdmsEndPoint());

        Map<String, Object> request = buildMdmsRequest(requestInfo, tenantId, MASTER_SUB_CATEGORY, null);

        Object result = serviceRequestRepository.fetchResult(uri, request).orElse(null);
        
        try {
            Map<String, Object> response = mapper.convertValue(result, Map.class);
            Map<String, Object> mdmsRes = (Map<String, Object>) response.get(MDMS_RES);
            Map<String, Object> challan = (Map<String, Object>) mdmsRes.get(MODULE_CHALLAN);
            List<Map<String, Object>> subCategories = (List<Map<String, Object>>) challan.get(MASTER_SUB_CATEGORY);
            
            log.info("Fetched {} subcategories from MDMS", subCategories != null ? subCategories.size() : 0);
            return subCategories != null ? subCategories : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching all subcategories from MDMS", e);
            return new ArrayList<>();
        }
    }

    private Map<String, Object> buildMdmsRequest(RequestInfo requestInfo, String tenantId,
            String masterName, String filter) {
        Map<String, Object> request = new HashMap<>();
        request.put(REQUEST_INFO, requestInfo);

        Map<String, Object> mdmsCriteria = new HashMap<>();
        mdmsCriteria.put(TENANT_ID, tenantId);

        Map<String, Object> moduleDetail = new HashMap<>();
        moduleDetail.put(MODULE_NAME, MODULE_CHALLAN);

        Map<String, Object> masterDetail = new HashMap<>();
        masterDetail.put(MASTER_NAME, masterName);
        if (filter != null) {
            masterDetail.put(FILTER, filter);
        }

        moduleDetail.put(MASTER_DETAILS, Arrays.asList(masterDetail));
        mdmsCriteria.put(MODULE_DETAILS, Arrays.asList(moduleDetail));

        request.put(MDMS_CRITERIA, mdmsCriteria);
        return request;
    }

}
