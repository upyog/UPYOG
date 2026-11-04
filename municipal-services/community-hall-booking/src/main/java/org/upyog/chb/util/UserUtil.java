package org.upyog.chb.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.UserDetailResponse;
import org.upyog.chb.repository.ServiceRequestRepository;

/**
 * Utility for interacting with the eGov user service.
 */
@Component
public class UserUtil {

    private static final String USER_FIELD = "user";
    private static final String CREATED_DATE_FIELD = "createdDate";
    private static final String LAST_MODIFIED_DATE_FIELD = "lastModifiedDate";
    private static final String DOB_FIELD = "dob";
    private static final String PWD_EXPIRY_DATE_FIELD = "pwdExpiryDate";
    private static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    private final ObjectMapper mapper;
    private final ServiceRequestRepository serviceRequestRepository;
    private final String userCreateEndpoint;
    private final String userSearchEndpoint;
    private final String userUpdateEndpoint;

    public UserUtil(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository,
            @Value("${egov.user.create.path}") String userCreateEndpoint,
            @Value("${egov.user.search.path}") String userSearchEndpoint,
            @Value("${egov.user.update.path}") String userUpdateEndpoint) {
        this.mapper = mapper;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userCreateEndpoint = userCreateEndpoint;
        this.userSearchEndpoint = userSearchEndpoint;
        this.userUpdateEndpoint = userUpdateEndpoint;
    }

    public UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
        String dobFormat = null;
        if (uri.toString().contains(userSearchEndpoint) || uri.toString().contains(userUpdateEndpoint)) {
            dobFormat = "yyyy-MM-dd";
        } else if (uri.toString().contains(userCreateEndpoint)) {
            dobFormat = "dd/MM/yyyy";
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = (Map<String, Object>) serviceRequestRepository.fetchResult(uri,
                    userRequest);
            parseResponse(responseMap, dobFormat);
            return mapper.convertValue(responseMap, UserDetailResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("IllegalArgumentException",
                    "ObjectMapper not able to convertValue in userCall");
        }
    }

    public void parseResponse(Map<String, Object> responseMap, String dobFormat) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> users = (List<Map<String, Object>>) responseMap.get(USER_FIELD);
        if (users != null) {
            users.forEach(map -> {
                map.put(CREATED_DATE_FIELD, dateTolong((String) map.get(CREATED_DATE_FIELD), DATETIME_FORMAT));
                if (map.get(LAST_MODIFIED_DATE_FIELD) != null) {
                    map.put(LAST_MODIFIED_DATE_FIELD,
                            dateTolong((String) map.get(LAST_MODIFIED_DATE_FIELD), DATETIME_FORMAT));
                }
                if (map.get(DOB_FIELD) != null) {
                    map.put(DOB_FIELD, dateTolong((String) map.get(DOB_FIELD), dobFormat));
                }
                if (map.get(PWD_EXPIRY_DATE_FIELD) != null) {
                    map.put(PWD_EXPIRY_DATE_FIELD,
                            dateTolong((String) map.get(PWD_EXPIRY_DATE_FIELD), DATETIME_FORMAT));
                }
            });
        }
    }

    private Long dateTolong(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try {
            if (format.contains("H") || format.contains("m") || format.contains("s")) {
                return LocalDateTime.parse(date, formatter).atZone(SYSTEM_ZONE).toInstant().toEpochMilli();
            }
            return LocalDate.parse(date, formatter).atStartOfDay(SYSTEM_ZONE).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            throw new CustomException("INVALID_DATE_FORMAT", "Failed to parse date format in user");
        }
    }

    public void addUserDefaultFields(String mobileNumber, String tenantId, User userInfo) {
        Role role = getCitizenRole(tenantId);
        userInfo.setRoles(Collections.singletonList(role));
        userInfo.setType("CITIZEN");
        userInfo.setUserName(mobileNumber);
        userInfo.setTenantId(getStateLevelTenant(tenantId));
    }

    private Role getCitizenRole(String tenantId) {
        Role role = new Role();
        role.setCode("CITIZEN");
        role.setName("Citizen");
        role.setTenantId(getStateLevelTenant(tenantId));
        return role;
    }

    public String getStateLevelTenant(String tenantId) {
        return tenantId.split("\\.")[0];
    }

}
