package org.upyog.pgrai.util;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.repository.ServiceRequestRepository;
import org.upyog.pgrai.web.models.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.upyog.pgrai.util.PGRConstants.HRMS_DEPARTMENT_JSONPATH;

/**
 * Utility class for interacting with the HRMS service.
 * Provides methods to fetch department details for employees and build HRMS search URLs.
 */
@Component
public class HRMSUtil {

    private ServiceRequestRepository serviceRequestRepository;

    private PGRConfiguration config;

    /**
     * Constructor for `HRMSUtil`.
     *
     * @param serviceRequestRepository Repository for making service requests.
     * @param config                   Configuration object for PGR.
     */
    @Autowired
    public HRMSUtil(ServiceRequestRepository serviceRequestRepository, PGRConfiguration config) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.config = config;
    }

    /**
     * Fetches the list of departments for the given list of employee UUIDs.
     *
     * @param uuids       The list of employee UUIDs.
     * @param requestInfo The request information.
     * @return A list of department names corresponding to the given UUIDs.
     * @throws CustomException If the HRMS response cannot be parsed or no departments are found.
     */
    public List<String> getDepartment(List<String> uuids, RequestInfo requestInfo) {
        StringBuilder url = getHRMSURI(uuids);

        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

        Object res = serviceRequestRepository.fetchResult(url, requestInfoWrapper);

        List<String> departments;

        try {
            departments = JsonPath.read(res, HRMS_DEPARTMENT_JSONPATH);
        } catch (Exception e) {
            throw new CustomException("PARSING_ERROR", "Failed to parse HRMS response");
        }

        if (CollectionUtils.isEmpty(departments)) {
            throw new CustomException("DEPARTMENT_NOT_FOUND", "The Department of the user with UUIDs: " + uuids.toString() + " is not found");
        }

        return departments;
    }

    /**
     * Builds the HRMS search URL for fetching employee details.
     *
     * @param uuids The list of employee UUIDs.
     * @return The constructed HRMS search URL.
     */
    public StringBuilder getHRMSURI(List<String> uuids) {
        StringBuilder builder = new StringBuilder(config.getHrmsHost());
        builder.append(config.getHrmsEndPoint());
        builder.append("?uuids=");
        builder.append(StringUtils.join(uuids, ","));

        return builder;
    }
}