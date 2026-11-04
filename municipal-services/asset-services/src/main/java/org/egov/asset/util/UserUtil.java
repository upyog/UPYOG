package org.egov.asset.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.UserDetailResponse;
import org.egov.asset.repository.ServiceRequestRepository;
import org.egov.asset.web.models.Role;
import org.egov.asset.web.models.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserUtil {

    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    private static final String PWD_EXPIRY_DATE = "pwdExpiryDate";

    private final ObjectMapper mapper;
    private final ServiceRequestRepository serviceRequestRepository;

    @Value("${egov.user.create.path}")
    private String userCreateEndpoint;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.user.update.path}")
    private String userUpdateEndpoint;

    public UserUtil(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository) {
        this.mapper = mapper;
        this.serviceRequestRepository = serviceRequestRepository;
    }

    /**
     * Returns UserDetailResponse by calling user service with given uri and object
     *
     * @param userRequest Request object for user service
     * @param uri         The address of the endpoint
     * @return Response from user service as parsed as userDetailResponse
     */
    public UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
        String dobFormat = null;
        if (uri.toString().contains(userSearchEndpoint) || uri.toString().contains(userUpdateEndpoint)) {
            dobFormat = "yyyy-MM-dd";
        } else if (uri.toString().contains(userCreateEndpoint)) {
            dobFormat = "dd/MM/yyyy";
        }
        try {
            Object responseObject = serviceRequestRepository.fetchResult(uri, userRequest);
            Map<String, Object> responseMap = (Map<String, Object>) responseObject;
            parseResponse(responseMap, dobFormat);
            return mapper.convertValue(responseMap, UserDetailResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
        }
    }

    /**
     * Parses date formats to long for all users in responseMap
     *
     * @param responeMap LinkedHashMap got from user api response
     * @param dobFormat  date of birth format
     */
    public void parseResponse(Map<String, Object> responeMap, String dobFormat) {
        List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responeMap.get("user");
        String format1 = "dd-MM-yyyy HH:mm:ss";
        if (users != null) {
            users.forEach(map -> {
                map.put("createdDate", dateTolong((String) map.get("createdDate"), format1));
                if (map.get(LAST_MODIFIED_DATE) != null) {
                    map.put(LAST_MODIFIED_DATE, dateTolong((String) map.get(LAST_MODIFIED_DATE), format1));
                }
                if (map.get("dob") != null) {
                    map.put("dob", dateTolong((String) map.get("dob"), dobFormat));
                }
                if (map.get(PWD_EXPIRY_DATE) != null) {
                    map.put(PWD_EXPIRY_DATE, dateTolong((String) map.get(PWD_EXPIRY_DATE), format1));
                }
            });
        }
    }

    /**
     * Converts date to long
     *
     * @param date   date to be parsed
     * @param format Format of the date
     * @return Long value of date
     */
    private Long dateTolong(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try {
            if (format.contains("HH")) {
                return LocalDateTime.parse(date, formatter).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            return LocalDate.parse(date, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            throw new CustomException("INVALID_DATE_FORMAT", "Failed to parse date format in user");
        }
    }

    /**
     * enriches the userInfo with statelevel tenantId and other fields
     *
     * @param mobileNumber mobile number of the user
     * @param tenantId     tenant identifier
     * @param userInfo     user details to enrich
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
     * Returns role object for citizen
     *
     * @param tenantId tenant identifier
     * @return citizen role
     */
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
