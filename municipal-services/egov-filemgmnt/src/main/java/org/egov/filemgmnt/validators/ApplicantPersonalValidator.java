package org.egov.filemgmnt.validators;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.filemgmnt.config.FilemgmntConfiguration;
import org.egov.filemgmnt.repository.ApplicantPersonalRepository;
import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class ApplicantPersonalValidator.
 */
@Slf4j
@Component
public class ApplicantPersonalValidator {

    private final ApplicantPersonalRepository appRepository;
    private final FilemgmntConfiguration config;
    private final MdmsValidator mdmsValidator;

    @Autowired
    public ApplicantPersonalValidator(ApplicantPersonalRepository appRepository, FilemgmntConfiguration config,
                                      MdmsValidator mdmsValidator) {
        this.appRepository = appRepository;
        this.config = config;
        this.mdmsValidator = mdmsValidator;

    }

    /**
     * Validate applicant personal create request.
     *
     * @param request the {@link ApplicantPersonalRequest}
     */
    public void validateCreate(ApplicantPersonalRequest request, Object mdmsData) {
        if (CollectionUtils.isEmpty(request.getApplicantPersonals())) {
            throw new CustomException(ErrorCodes.APPLICANT_PERSONAL_REQUIRED.getCode(),
                    "Atleast one applicant personal is required.");
        }

        mdmsValidator.validateMdmsData(request, mdmsData);
    }

    /**
     * Validate applicant personal update request.
     *
     * @param request the {@link ApplicantPersonalRequest}
     */
    public void validateUpdate(ApplicantPersonalRequest request, List<ApplicantPersonal> searchResult) {
        List<ApplicantPersonal> applicantPersonals = request.getApplicantPersonals();

        if (CollectionUtils.isEmpty(applicantPersonals)) {
            throw new CustomException(ErrorCodes.APPLICANT_PERSONAL_REQUIRED.getCode(),
                    "Atleast one applicant personal is required.");
        }

        if (applicantPersonals.size() != searchResult.size()) {
            throw new CustomException(ErrorCodes.APPLICANT_PERSONAL_INVALID_UPDATE.getCode(),
                    "Applicant Personal(s) not found in database.");
        }

    }

    /**
     * Validates if the search parameters are valid
     * 
     * @param requestInfo The requestInfo of the incoming request
     * @param criteria    The TradeLicenseSearch Criteria
     */
    public void validateSearch(RequestInfo requestInfo, ApplicantPersonalSearchCriteria criteria) {

        if (criteria.isEmpty()) {
            throw new CustomException("INVALID SEARCH", "Search without any paramters is not allowed");
        }

        if (criteria.tenantIdOnly())
            throw new CustomException("INVALID SEARCH", "Search based only on tenantId is not allowed");

        String allowedParamStr;
        allowedParamStr = config.getAllowedCitizenSearchParams();

        if (StringUtils.isEmpty(allowedParamStr) && !criteria.isEmpty()) {
            throw new CustomException("INVALID SEARCH", "No search parameters are expected");
        } else {
            List<String> allowedParams = Arrays.asList(allowedParamStr.split(","));
            validateSearchParams(criteria, allowedParams, requestInfo);
        }
    }

    /**
     * Validates if the paramters coming in search are allowed
     * 
     * @param criteria      TradeLicense search criteria
     * @param allowedParams Allowed Params for search
     */
    private void validateSearchParams(ApplicantPersonalSearchCriteria criteria, List<String> allowedParams,
                                      RequestInfo requestInfo) {

        if (criteria.getTenantId() != null && !allowedParams.contains("tenantId"))
            throw new CustomException("INVALID SEARCH", "Search on tenantId is not allowed");

        if (criteria.getToDate() != null && !allowedParams.contains("toDate"))
            throw new CustomException("INVALID SEARCH", "Search on toDate is not allowed");

        if (criteria.getFromDate() != null && !allowedParams.contains("fromDate"))
            throw new CustomException("INVALID SEARCH", "Search on fromDate is not allowed");

        if (criteria.getIds() != null && !allowedParams.contains("ids"))
            throw new CustomException("INVALID SEARCH", "Search on ids is not allowed");

        if (criteria.getFileCodes() != null && !allowedParams.contains("filecode"))
            throw new CustomException("INVALID SEARCH", "Search on filecode is not allowed");

        if (criteria.getOffset() != null && !allowedParams.contains("offset"))
            throw new CustomException("INVALID SEARCH", "Search on offset is not allowed");

        if (criteria.getLimit() != null && !allowedParams.contains("limit"))
            throw new CustomException("INVALID SEARCH", "Search on limit is not allowed");

    }

}
