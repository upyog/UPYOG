package org.upyog.pgrai.service;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.repository.IdGenRepository;
import org.upyog.pgrai.util.PGRUtils;
import org.upyog.pgrai.web.models.*;
import org.upyog.pgrai.web.models.Idgen.IdResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.upyog.pgrai.util.PGRConstants.USERTYPE_CITIZEN;

/**
 * Service class for enriching PGR service requests.
 * Provides methods to enrich create, update, and search requests with additional details.
 */
@org.springframework.stereotype.Service
public class EnrichmentService {

    private PGRUtils utils;
    private IdGenRepository idGenRepository;
    private PGRConfiguration config;
    private UserService userService;

    /**
     * Constructor for `EnrichmentService`.
     *
     * @param utils           Utility class for common operations.
     * @param idGenRepository Repository for interacting with the ID generation service.
     * @param config          Configuration object for PGR.
     * @param userService     Service for user-related operations.
     */
    @Autowired
    public EnrichmentService(PGRUtils utils, IdGenRepository idGenRepository, PGRConfiguration config, UserService userService) {
        this.utils = utils;
        this.idGenRepository = idGenRepository;
        this.config = config;
        this.userService = userService;
    }

    /**
     * Enriches the create request with audit details, UUIDs, and custom IDs from the IDGen service.
     *
     * @param serviceRequest The create request to be enriched.
     */
    public void enrichCreateRequest(ServiceRequest serviceRequest) {
        RequestInfo requestInfo = serviceRequest.getRequestInfo();
        Service service = serviceRequest.getService();
        Workflow workflow = serviceRequest.getWorkflow();
        String tenantId = service.getTenantId();

        // Enrich accountId of the logged-in citizen
        if (requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN))
            serviceRequest.getService().setAccountId(requestInfo.getUserInfo().getUuid());

        userService.callUserService(serviceRequest);

        AuditDetails auditDetails = utils.getAuditDetails(requestInfo.getUserInfo().getUuid(), service, true);

        service.setAuditDetails(auditDetails);
        service.setId(UUID.randomUUID().toString());
        service.getAddress().setId(UUID.randomUUID().toString());
        service.getAddress().setTenantId(tenantId);
        service.setActive(true);

        if (workflow.getVerificationDocuments() != null) {
            workflow.getVerificationDocuments().forEach(document -> {
                document.setId(UUID.randomUUID().toString());
            });
        }

        if (StringUtils.isEmpty(service.getAccountId()))
            service.setAccountId(service.getCitizen().getUuid());

        List<String> customIds = getIdList(requestInfo, tenantId, config.getServiceRequestIdGenName(), config.getServiceRequestIdGenFormat(), 1);

        service.setServiceRequestId(customIds.get(0));
    }

    /**
     * Enriches the update request by updating the lastModifiedTime in audit details.
     *
     * @param serviceRequest The update request to be enriched.
     */
    public void enrichUpdateRequest(ServiceRequest serviceRequest) {
        RequestInfo requestInfo = serviceRequest.getRequestInfo();
        Service service = serviceRequest.getService();
        AuditDetails auditDetails = utils.getAuditDetails(requestInfo.getUserInfo().getUuid(), service, false);

        service.setAuditDetails(auditDetails);

        userService.callUserService(serviceRequest);
    }

    /**
     * Enriches the search criteria for default searches and enriches user IDs from mobile numbers.
     * Also sets default limit and offset if not provided.
     *
     * @param requestInfo The request information.
     * @param criteria    The search criteria to be enriched.
     */
    public void enrichSearchRequest(RequestInfo requestInfo, RequestSearchCriteria criteria) {
        if (criteria.isEmpty() && requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN)) {
            String citizenMobileNumber = requestInfo.getUserInfo().getUserName();
            criteria.setMobileNumber(citizenMobileNumber);
        }

        criteria.setAccountId(requestInfo.getUserInfo().getUuid());

        String tenantId = (criteria.getTenantId() != null) ? criteria.getTenantId() : requestInfo.getUserInfo().getTenantId();

        if (criteria.getMobileNumber() != null) {
            userService.enrichUserIds(tenantId, criteria);
        }

        if (criteria.getLimit() == null)
            criteria.setLimit(config.getDefaultLimit());

        if (criteria.getOffset() == null)
            criteria.setOffset(config.getDefaultOffset());

        if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxLimit())
            criteria.setLimit(config.getMaxLimit());
    }

    /**
     * Returns a list of IDs generated from the IDGen service.
     *
     * @param requestInfo RequestInfo from the request.
     * @param tenantId    Tenant ID of the city.
     * @param idKey       Code of the field defined in application properties for which IDs are generated.
     * @param idformat    Format in which IDs are to be generated.
     * @param count       Number of IDs to be generated.
     * @return List of IDs generated using the IDGen service.
     */
    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey,
                                   String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count).getIdResponses();

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException("IDGEN ERROR", "No IDs returned from IDGen Service");

        return idResponses.stream()
                .map(IdResponse::getId).collect(Collectors.toList());
    }
}