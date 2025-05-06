package org.upyog.pgrai.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.repository.ServiceRequestRepository;
import org.upyog.pgrai.web.models.RequestInfoWrapper;
import org.upyog.pgrai.web.models.User;
import org.upyog.pgrai.web.models.user.UserDetailResponse;
import org.upyog.pgrai.web.models.user.UserSearchRequest;
import org.upyog.pgrai.web.models.workflow.BusinessServiceResponse;
import org.upyog.pgrai.web.models.workflow.State;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.upyog.pgrai.util.PGRConstants.*;

/**
 * Utility class for handling migration-related operations.
 * Provides methods to map user IDs to UUIDs, fetch status-to-UUID mappings, and service code-to-SLA mappings.
 */
@Slf4j
@Component
public class MigrationUtils {

    private UserUtils userUtils;
    private PGRConfiguration config;
    private ObjectMapper mapper;
    private ServiceRequestRepository repository;
    private MDMSUtils mdmsUtils;

    /**
     * Constructor for `MigrationUtils`.
     *
     * @param userUtils   Utility class for user-related operations.
     * @param config      Configuration object for PGR.
     * @param mapper      ObjectMapper for JSON serialization and deserialization.
     * @param repository  Repository for making service requests.
     * @param mdmsUtils   Utility class for MDMS-related operations.
     */
    @Autowired
    public MigrationUtils(UserUtils userUtils, PGRConfiguration config, ObjectMapper mapper, ServiceRequestRepository repository, MDMSUtils mdmsUtils) {
        this.userUtils = userUtils;
        this.config = config;
        this.mapper = mapper;
        this.repository = repository;
        this.mdmsUtils = mdmsUtils;
    }

    /**
     * Maps user IDs to their corresponding UUIDs by calling the user search API.
     *
     * @param ids The list of user IDs.
     * @return A map of user IDs to UUIDs.
     * @throws CustomException If no users are found or the number of UUIDs does not match the number of IDs.
     */
    public Map<Long, String> getIdtoUUIDMap(List<String> ids) {
        ids.removeAll(Collections.singleton(null));

        UserSearchRequest userSearchRequest = new UserSearchRequest();

        if (!CollectionUtils.isEmpty(ids))
            userSearchRequest.setId(ids);

        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        UserDetailResponse userDetailResponse = userUtils.userCall(userSearchRequest, uri);
        List<User> users = userDetailResponse.getUser();

        if (CollectionUtils.isEmpty(users))
            throw new CustomException("USER_NOT_FOUND", "No user found for the uuids");

        Map<Long, String> idToUuidMap = users.stream().collect(Collectors.toMap(User::getId, User::getUuid));

        if (idToUuidMap.keySet().size() != ids.size())
            throw new CustomException("UUID_NOT_FOUND", "Number of ids searched: " + ids.size() + " uuids returned: " + idToUuidMap.keySet().size());

        return idToUuidMap;
    }

    /**
     * Fetches a mapping of workflow statuses to their corresponding UUIDs for a given tenant.
     *
     * @param tenantId The tenant ID.
     * @return A map of workflow statuses to UUIDs.
     * @throws CustomException If the business service or workflow states are not found.
     */
    public Map<String, String> getStatusToUUIDMap(String tenantId) {
        StringBuilder url = getSearchURLWithParams(tenantId, PGR_BUSINESSSERVICE);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(new RequestInfo()).build();
        Object result = repository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response;
        try {
            response = mapper.convertValue(result, BusinessServiceResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("PARSING ERROR", "Failed to parse response of workflow business service search");
        }

        if (CollectionUtils.isEmpty(response.getBusinessServices()))
            throw new CustomException("BUSINESSSERVICE_NOT_FOUND", "The businessService " + PGR_BUSINESSSERVICE + " is not found");

        return response.getBusinessServices().get(0).getStates().stream()
                .collect(Collectors.toMap(State::getState, State::getUuid));
    }

    /**
     * Fetches a mapping of service codes to their corresponding SLA (in milliseconds) for a given tenant.
     *
     * @param tenantId The tenant ID.
     * @return A map of service codes to SLA values in milliseconds.
     * @throws CustomException If the MDMS response cannot be parsed.
     */
    public Map<String, Long> getServiceCodeToSLAMap(String tenantId) {
        Map<String, Long> serviceCodeToSLA = new HashMap<>();

        MdmsCriteriaReq mdmsCriteriaReq = mdmsUtils.getMDMSRequest(new RequestInfo(), tenantId);
        Object result = repository.fetchResult(mdmsUtils.getMdmsSearchUrl(), mdmsCriteriaReq);
        List<Map<String, Object>> res;

        try {
            res = JsonPath.read(result, MDMS_DATA_JSONPATH);
        } catch (Exception e) {
            throw new CustomException("JSONPATH_ERROR", "Failed to parse mdms response");
        }

        for (Map<String, Object> map : res) {
            Long SLA = TimeUnit.HOURS.toMillis((Integer) map.get(MDMS_DATA_SLA_KEYWORD));
            serviceCodeToSLA.put((String) map.get(MDMS_DATA_SERVICE_CODE_KEYWORD), SLA);
        }

        return serviceCodeToSLA;
    }

    /**
     * Constructs a URL for searching business services based on the given tenant ID and business service name.
     *
     * @param tenantId        The tenant ID.
     * @param businessService The business service name.
     * @return The constructed URL.
     */
    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {
        StringBuilder url = new StringBuilder(config.getWfHost());
        url.append(config.getWfBusinessServiceSearchPath());
        url.append("?tenantId=");
        url.append(tenantId);
        url.append("&businessServices=");
        url.append(businessService);
        return url;
    }
}