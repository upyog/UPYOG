package org.egov.filemgmnt.validators;

import static org.egov.filemgmnt.web.enums.ErrorCodes.APPLICANT_PERSONAL_INVALID_CREATE;
import static org.egov.filemgmnt.web.enums.ErrorCodes.APPLICANT_PERSONAL_INVALID_UPDATE;
import static org.egov.filemgmnt.web.enums.ErrorCodes.APPLICANT_PERSONAL_REQUIRED;
import static org.egov.filemgmnt.web.enums.ErrorCodes.INVALID_SEARCH;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.filemgmnt.config.FMConfiguration;
import org.egov.filemgmnt.repository.ApplicantPersonalRepository;
import org.egov.filemgmnt.util.FMUtils;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class ApplicantPersonalValidator.
 */
@Component
public class ApplicantPersonalValidator {

    private final ApplicantPersonalRepository repository;
    private final FMConfiguration config;
    private final MdmsValidator mdmsValidator;

    @Autowired
    public ApplicantPersonalValidator(ApplicantPersonalRepository repository, FMConfiguration config,
                                      MdmsValidator mdmsValidator) {
        this.repository = repository;
        this.config = config;
        this.mdmsValidator = mdmsValidator;

    }

    /**
     * Validate applicant personal create request.
     *
     * @param request the
     *                {@link org.egov.filemgmnt.web.models.ApplicantPersonalRequest
     *                ApplicantPersonalRequest}
     */
    public void validateCreate(ApplicantPersonalRequest request, Object mdmsData) {
        List<ApplicantPersonal> applicantPersonals = request.getApplicantPersonals();

        if (CollectionUtils.isEmpty(applicantPersonals)) {
            throw new CustomException(APPLICANT_PERSONAL_REQUIRED.getCode(), "Applicant personal is required.");
        }

        if (applicantPersonals.size() > 1) { // NOPMD
            throw new CustomException(APPLICANT_PERSONAL_INVALID_CREATE.getCode(),
                    "Supports only single Applicant personal create request.");
        }

        // validateApplicantPersonalRequiredFields(request);
        mdmsValidator.validateMdmsData(request, mdmsData);
    }

    /**
     * Validate applicant personal update request.
     *
     * @param request the
     *                {@link org.egov.filemgmnt.web.models.ApplicantPersonalRequest
     *                ApplicantPersonalRequest}
     */
    public void validateUpdate(ApplicantPersonalRequest request, List<ApplicantPersonal> searchResult) {
        List<ApplicantPersonal> applicantPersonals = request.getApplicantPersonals();

        if (CollectionUtils.isEmpty(applicantPersonals)) {
            throw new CustomException(APPLICANT_PERSONAL_REQUIRED.getCode(), "Applicant personal is required.");
        }

        if (applicantPersonals.size() > 1) { // NOPMD
            throw new CustomException(APPLICANT_PERSONAL_INVALID_UPDATE.getCode(),
                    "Supports only single Applicant personal update request.");
        }

        if (applicantPersonals.size() != searchResult.size()) {
            throw new CustomException(APPLICANT_PERSONAL_INVALID_UPDATE.getCode(),
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
        if (StringUtils.isBlank(criteria.getTenantId())) {
            throw new CustomException(INVALID_SEARCH.getCode(), "Tenant id is required.");
        }

        String allowedSearchParams = config.getAllowedCitizenSearchParams();

        if (StringUtils.isNotBlank(allowedSearchParams)) {
            List<String> allowedSearchTokens = Arrays.asList(allowedSearchParams.split(","));
            validateSearchParams(criteria, allowedSearchTokens);
        }

        if (StringUtils.isNotBlank(criteria.getTenantId()) && StringUtils.isBlank(criteria.getId())
                && StringUtils.isBlank(criteria.getFileCode()) && criteria.getFromDate() == null
                && StringUtils.isBlank(criteria.getAadhaarNo())) {
            throw new CustomException(INVALID_SEARCH.getCode(), "Search based only on tenant id is not allowed.");
        }
    }

    private void validateSearchParams(ApplicantPersonalSearchCriteria criteria, List<String> allowedParams) {
        BeanWrapper bw = new BeanWrapperImpl(criteria);

        FMUtils.validateSearchParam(bw, "tenantId", allowedParams);
        FMUtils.validateSearchParam(bw, "id", allowedParams);
        FMUtils.validateSearchParam(bw, "fileCode", allowedParams);
        FMUtils.validateSearchParam(bw, "fromDate", allowedParams);
        FMUtils.validateSearchParam(bw, "toDate", allowedParams);
        FMUtils.validateSearchParam(bw, "aadhaarNo", allowedParams);
        FMUtils.validateSearchParam(bw, "offset", allowedParams);
        FMUtils.validateSearchParam(bw, "limit", allowedParams);
    }

//    private void validateApplicantPersonalRequiredFields(ApplicantPersonalRequest request) {
//        Map<String, String> errorMap = new HashMap<>();
//        request.getApplicantPersonals()
//               .forEach(personal -> {
//                   if (StringUtils.isBlank(personal.getFileDetail()
//                                                   .getFinancialYear()))
//                       errorMap.put("NULL_FINANCIALYEAR", " Financial Year cannot be null");
//                   if (StringUtils.isBlank(personal.getServiceDetails()
//                                                   .getServiceCode()))
//                       errorMap.put("NULL_SERVICESUBTYPE", " Service Sub Type cannot be null");
//               });
//        if (MapUtils.isNotEmpty(errorMap)) {
//            throw new CustomException(errorMap);
//        }
//
//        request.getApplicantPersonals()
//               .forEach(personal -> {
//                   if (StringUtils.isEmpty(personal.getApplicantAddress()
//                                                   .getHouseNo())) {
//                       throw new CustomException("NULL_HOUSENO", " House Number cannot be null");
//                   }
//                   if (StringUtils.isEmpty(personal.getApplicantAddress()
//                                                   .getHouseName())) {
//                       throw new CustomException("NULL_HOUSENAME", " House Name cannot be null");
//                   }
//
//               });
//    }

}
