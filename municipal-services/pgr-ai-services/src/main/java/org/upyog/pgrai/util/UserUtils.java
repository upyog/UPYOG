package org.upyog.pgrai.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.Role;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.upyog.pgrai.config.PGRConfiguration;
import org.upyog.pgrai.repository.ServiceRequestRepository;
import org.upyog.pgrai.web.models.User;
import org.upyog.pgrai.web.models.user.UserDetailResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Utility class for user-related operations in the PGR (Public Grievance Redressal) system.
 * Provides methods for user service calls, response parsing, and user enrichment.
 */
@Component
@Slf4j
public class UserUtils {

    private ObjectMapper mapper;

    private ServiceRequestRepository serviceRequestRepository;

    private PGRConfiguration config;

    @Autowired
    private MultiStateInstanceUtil centralInstanceUtil;

    /**
     * Constructor for UserUtils.
     *
     * @param mapper ObjectMapper for JSON operations.
     * @param serviceRequestRepository Repository for service requests.
     * @param config Configuration properties for the PGR system.
     */
    @Autowired
    public UserUtils(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository, PGRConfiguration config) {
        this.mapper = mapper;
        this.serviceRequestRepository = serviceRequestRepository;
        this.config = config;
    }

    /**
     * Calls the user service and returns a UserDetailResponse.
     *
     * @param userRequest Request object for the user service.
     * @param uri The endpoint URI for the user service.
     * @return Response from the user service as a UserDetailResponse object.
     * @throws CustomException If the response cannot be parsed.
     */
    public UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
        String dobFormat = null;
        if (uri.toString().contains(config.getUserSearchEndpoint()) || uri.toString().contains(config.getUserUpdateEndpoint()))
            dobFormat = "yyyy-MM-dd";
        else if (uri.toString().contains(config.getUserCreateEndpoint()))
            dobFormat = "dd/MM/yyyy";
        try {
            LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(uri, userRequest);
            parseResponse(responseMap, dobFormat);
            return mapper.convertValue(responseMap, UserDetailResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
        }
    }

    /**
     * Parses date fields in the response map to long values.
     *
     * @param responseMap The response map from the user API.
     * @param dobFormat The date format for the "dob" field.
     */
    public void parseResponse(LinkedHashMap responseMap, String dobFormat) {
        List<LinkedHashMap> users = (List<LinkedHashMap>) responseMap.get("user");
        String format1 = "dd-MM-yyyy HH:mm:ss";
        if (users != null) {
            users.forEach(map -> {
                map.put("createdDate", dateTolong((String) map.get("createdDate"), format1));
                if ((String) map.get("lastModifiedDate") != null)
                    map.put("lastModifiedDate", dateTolong((String) map.get("lastModifiedDate"), format1));
                if ((String) map.get("dob") != null)
                    map.put("dob", dateTolong((String) map.get("dob"), dobFormat));
                if ((String) map.get("pwdExpiryDate") != null)
                    map.put("pwdExpiryDate", dateTolong((String) map.get("pwdExpiryDate"), format1));
            });
        }
    }

    /**
     * Converts a date string to a long value.
     *
     * @param date The date string to be converted.
     * @param format The format of the date string.
     * @return The long value of the date.
     * @throws CustomException If the date cannot be parsed.
     */
    private Long dateTolong(String date, String format) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        try {
            Date d = f.parse(date);
            return d.getTime();
        } catch (ParseException e) {
            throw new CustomException("INVALID_DATE_FORMAT", "Failed to parse date format in user");
        }
    }

    /**
     * Enriches the user object with default fields for a citizen.
     *
     * @param mobileNumber The mobile number of the user.
     * @param tenantId The tenant ID of the user.
     * @param userInfo The user object to be enriched.
     */
    public void addUserDefaultFields(String mobileNumber, String tenantId, User userInfo) {
        Role role = getCitizenRole(tenantId);
        userInfo.setRoles(Collections.singletonList(role));
        userInfo.setType("CITIZEN");
        userInfo.setUserName(mobileNumber);
        userInfo.setTenantId(getStateLevelTenant(tenantId));
        userInfo.setActive(true);
    }

    /**
     * Creates a Role object for a citizen.
     *
     * @param tenantId The tenant ID for the role.
     * @return A Role object for a citizen.
     */
    private Role getCitizenRole(String tenantId) {
        Role role = new Role();
        role.setCode("CITIZEN");
        role.setName("Citizen");
        role.setTenantId(getStateLevelTenant(tenantId));
        return role;
    }

    /**
     * Retrieves the state-level tenant ID from a given tenant ID.
     *
     * @param tenantId The tenant ID.
     * @return The state-level tenant ID.
     */
    public String getStateLevelTenant(String tenantId) {
        log.info("tenantId" + tenantId);
        return centralInstanceUtil.getStateLevelTenant(tenantId);
    }
}