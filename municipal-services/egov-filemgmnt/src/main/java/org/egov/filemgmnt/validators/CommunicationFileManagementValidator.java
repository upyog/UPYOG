package org.egov.filemgmnt.validators;

import static org.egov.filemgmnt.web.enums.ErrorCodes.COMUNICATION_FILE_REQUIRED;
import static org.egov.filemgmnt.web.enums.ErrorCodes.INVALID_SEARCH;

import org.egov.common.contract.request.RequestInfo;
import org.egov.filemgmnt.config.FMConfiguration;
import org.egov.filemgmnt.repository.CommunicationFileManagementRepository;
import org.egov.filemgmnt.web.models.CommunicationFileRequest;
import org.egov.filemgmnt.web.models.CommunicationFileSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommunicationFileManagementValidator {

    private final CommunicationFileManagementRepository commRepository;
    private final FMConfiguration config;
    private final MdmsValidator mdmsValidator;

    public CommunicationFileManagementValidator(CommunicationFileManagementRepository commRepository,
                                                FMConfiguration config, MdmsValidator mdmsValidator) {

        this.commRepository = commRepository;
        this.config = config;
        this.mdmsValidator = mdmsValidator;
    }

    /**
     * Validate file details create request.
     *
     * @param request the {@link CommunicationFileRequest}
     */
    public void validateCreate(CommunicationFileRequest request, Object mdmsData) {

        if (CollectionUtils.isEmpty(request.getCommunicationFiles())) {
            throw new CustomException(COMUNICATION_FILE_REQUIRED.getCode(), "Atleast one file is required");

        }
    }

    /**
     * Validates if the search parameters are valid
     * 
     * @param requestInfo The requestInfo of the incoming request
     * @param criteria    The CommunicationFileSearchCriteria
     */
    public void validateSearch(RequestInfo requestInfo, CommunicationFileSearchCriteria criteria) {

        if (criteria.isEmpty()) {
            throw new CustomException(INVALID_SEARCH.getCode(), "Search without any paramters is not allowed");
        }

        if (criteria.tenantIdOnly()) {
            throw new CustomException(INVALID_SEARCH.getCode(), "Search based only on tenantId is not allowed");
        }

//        String allowedSearchParams = config.getAllowedCitizenSearchParams();
//
//        if (StringUtils.isBlank(allowedSearchParams) && !criteria.isEmpty()) {
//            throw new CustomException(INVALID_SEARCH.getCode(), "No search parameters are expected");
//        }
    }

}
