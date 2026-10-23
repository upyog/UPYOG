package org.egov.asset.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.web.models.AssetSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class AssetValidator {

    private final MDMSValidator mdmsValidator;
    private final AssetConfiguration config;

    public AssetValidator(MDMSValidator mdmsValidator, AssetConfiguration config) {
        this.mdmsValidator = mdmsValidator;
        this.config = config;
    }

    /**
     * Validates MDMS master data required for asset creation.
     *
     * @param mdmsData master data returned from MDMS
     */
    public void validateCreate(Object mdmsData) {
        mdmsValidator.validateMdmsData(mdmsData);
    }

    /**
     * Validates if the search parameters are valid
     *
     * @param requestInfo The requestInfo of the incoming request
     * @param criteria    The BPASearch Criteria
     */
    public void validateSearch(RequestInfo requestInfo, AssetSearchCriteria criteria) {
        if (Boolean.TRUE.equals(criteria.getIsInterServiceCall())) {
            log.debug("Skipping validation for inter-service call");
            return;
        }

        if (requestInfo.getUserInfo() != null) {
            validateAuthenticatedUserSearch(requestInfo, criteria);
            return;
        }

        validateAnonymousUserSearch(criteria);
    }

    private void validateAuthenticatedUserSearch(RequestInfo requestInfo, AssetSearchCriteria criteria) {
        boolean isEmployee = requestInfo.getUserInfo().getType().equalsIgnoreCase(AssetConstants.EMPLOYEE);

        if (!isEmployee && criteria.isEmpty()) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "Search without any paramters is not allowed");
        }

        if (!isEmployee && !criteria.tenantIdOnly() && criteria.getTenantId() == null) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "TenantId is mandatory in search");
        }

        if (isEmployee && !criteria.isEmpty() && !criteria.tenantIdOnly() && criteria.getTenantId() == null) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "TenantId is mandatory in search");
        }

        if (!isEmployee) {
            throw new CustomException(AssetConstants.INVALID_SEARCH,
                    "The userType: " + requestInfo.getUserInfo().getType() + " does not have any search config");
        }
    }

    private void validateAnonymousUserSearch(AssetSearchCriteria criteria) {
        String allowedParamStr = config.getAllowedEmployeeSearchParameters();
        if (StringUtils.isEmpty(allowedParamStr) && !criteria.isEmpty()) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "No search parameters as expected");
        }
        if (!StringUtils.isEmpty(allowedParamStr)) {
            validateSearchParams(criteria, Arrays.asList(allowedParamStr.split(",")));
        }
    }

    /**
     * Validates if the paramters coming in search are allowed
     *
     * @param criteria      BPA search criteria
     * @param allowedParams Allowed Params for search
     */
    private void validateSearchParams(AssetSearchCriteria criteria, List<String> allowedParams) {
        long now = Instant.now().toEpochMilli();
        validateAllowedSearchFields(criteria, allowedParams);
        validateSearchDateRange(criteria, now);
    }

    private void validateAllowedSearchFields(AssetSearchCriteria criteria, List<String> allowedParams) {
        if (criteria.getApplicationNo() != null && !allowedParams.contains("applicationNo")) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on applicationNo is not allowed");
        }

        if (criteria.getStatus() != null && !allowedParams.contains("status")) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on Status is not allowed");
        }

        if (criteria.getIds() != null && !allowedParams.contains("ids")) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on ids is not allowed");
        }

        if (criteria.getOffset() != null && !allowedParams.contains("offset")) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on offset is not allowed");
        }

        if (criteria.getLimit() != null && !allowedParams.contains("limit")) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on limit is not allowed");
        }
    }

    private void validateSearchDateRange(AssetSearchCriteria criteria, long now) {
        if (criteria.getApprovalDate() != null && criteria.getApprovalDate() > now) {
            throw new CustomException(AssetConstants.INVALID_SEARCH,
                    "Permit Order Genarated date cannot be a future date");
        }

        if (criteria.getFromDate() != null && criteria.getFromDate() > now) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "From date cannot be a future date");
        }

        if (criteria.getToDate() != null && criteria.getFromDate() != null
                && criteria.getFromDate() > criteria.getToDate()) {
            throw new CustomException(AssetConstants.INVALID_SEARCH, "To date cannot be prior to from date");
        }
    }
}
