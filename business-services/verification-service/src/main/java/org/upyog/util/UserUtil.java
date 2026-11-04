package org.upyog.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.UserDetailResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.repository.ServiceRequestRepository;
import org.upyog.web.models.Role;
import org.upyog.web.models.User;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class UserUtil {

	private static final String FIELD_USER = "user";
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

	@Autowired
	public UserUtil(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository) {
		this.mapper = mapper;
		this.serviceRequestRepository = serviceRequestRepository;
	}

	/**
	 * Returns UserDetailResponse by calling user service with given uri and object
	 * @param userRequest Request object for user service
	 * @param uri The address of the endpoint
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
			Map<String, Object> responseMap = (Map<String, Object>) serviceRequestRepository.fetchResult(uri, userRequest);
			parseResponse(responseMap, dobFormat);
			return mapper.convertValue(responseMap, UserDetailResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
		}
	}

	/**
	 * Parses date formats to long for all users in responseMap
	 * @param responseMap Map got from user api response
	 */
	public void parseResponse(Map<String, Object> responseMap, String dobFormat) {
		List<Map<String, Object>> users = (List<Map<String, Object>>) responseMap.get(FIELD_USER);
		String format1 = "dd-MM-yyyy HH:mm:ss";
		if (users != null) {
			users.forEach(map -> {
				map.put("createdDate", dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get(LAST_MODIFIED_DATE) != null) {
					map.put(LAST_MODIFIED_DATE, dateTolong((String) map.get(LAST_MODIFIED_DATE), format1));
				}
				if ((String) map.get("dob") != null) {
					map.put("dob", dateTolong((String) map.get("dob"), dobFormat));
				}
				if ((String) map.get(PWD_EXPIRY_DATE) != null) {
					map.put(PWD_EXPIRY_DATE, dateTolong((String) map.get(PWD_EXPIRY_DATE), format1));
				}
			});
		}
	}

	/**
	 * Converts date to long
	 * @param date date to be parsed
	 * @param format Format of the date
	 * @return Long value of date
	 */
	private Long dateTolong(String date, String format) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			if (format.contains("H") || format.contains("m") || format.contains("s")) {
				LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
				return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			}
			LocalDate localDate = LocalDate.parse(date, formatter);
			return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		} catch (DateTimeParseException e) {
			throw new CustomException("INVALID_DATE_FORMAT", "Failed to parse date format in user");
		}
	}

	/**
	 * enriches the userInfo with statelevel tenantId and other fields
	 * @param mobileNumber
	 * @param tenantId
	 * @param userInfo
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
	 * @param tenantId
	 * @return
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
