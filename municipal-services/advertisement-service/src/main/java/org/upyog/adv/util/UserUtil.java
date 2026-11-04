package org.upyog.adv.util;

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
import org.upyog.adv.repository.ServiceRequestRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.UserDetailResponse;
/**
 * Utility class for managing user-related operations in the Advertisement Booking Service.
 */
@Component
public class UserUtil {

	private static final String FIELD_CREATED_DATE = "createdDate";
	private static final String FIELD_LAST_MODIFIED_DATE = "lastModifiedDate";
	private static final String FIELD_DOB = "dob";
	private static final String FIELD_PWD_EXPIRY_DATE = "pwdExpiryDate";
	private static final String FIELD_USER = "user";
	private static final String FORMAT_TIMESTAMP = "dd-MM-yyyy HH:mm:ss";
	private static final String FORMAT_DOB_SEARCH_UPDATE = "yyyy-MM-dd";
	private static final String FORMAT_DOB_CREATE = "dd/MM/yyyy";

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
	 * Returns UserDetailResponse by calling user service with given uri and object.
	 *
	 * @param userRequest Request object for user service
	 * @param uri The address of the endpoint
	 * @return Response from user service parsed as userDetailResponse
	 */
	@SuppressWarnings("unchecked")
	public UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
		String dobFormat;
		if (uri.toString().contains(userSearchEndpoint) || uri.toString().contains(userUpdateEndpoint))
			dobFormat = FORMAT_DOB_SEARCH_UPDATE;
		else if (uri.toString().contains(userCreateEndpoint))
			dobFormat = FORMAT_DOB_CREATE;
		else
			dobFormat = FORMAT_DOB_SEARCH_UPDATE;
		try {
			Map<String, Object> responseMap = (Map<String, Object>) serviceRequestRepository.fetchResult(uri,
					userRequest);
			parseResponse(responseMap, dobFormat);
			return mapper.convertValue(responseMap, UserDetailResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
		}
	}

	/**
	 * Parses date formats to long for all users in responseMap.
	 *
	 * @param responseMap response map from user API
	 * @param dobFormat date-of-birth format for the current call
	 */
	@SuppressWarnings("unchecked")
	public void parseResponse(Map<String, Object> responseMap, String dobFormat) {
		List<Map<String, Object>> users = (List<Map<String, Object>>) responseMap.get(FIELD_USER);
		if (users == null) {
			return;
		}
		users.forEach(map -> {
			map.put(FIELD_CREATED_DATE, dateToLong((String) map.get(FIELD_CREATED_DATE), FORMAT_TIMESTAMP));
			if (map.get(FIELD_LAST_MODIFIED_DATE) != null)
				map.put(FIELD_LAST_MODIFIED_DATE,
						dateToLong((String) map.get(FIELD_LAST_MODIFIED_DATE), FORMAT_TIMESTAMP));
			if (map.get(FIELD_DOB) != null)
				map.put(FIELD_DOB, dateToLong((String) map.get(FIELD_DOB), dobFormat));
			if (map.get(FIELD_PWD_EXPIRY_DATE) != null)
				map.put(FIELD_PWD_EXPIRY_DATE, dateToLong((String) map.get(FIELD_PWD_EXPIRY_DATE), FORMAT_TIMESTAMP));
		});
	}

	private Long dateToLong(String date, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		try {
			if (FORMAT_DOB_CREATE.equals(format) || (FORMAT_DOB_SEARCH_UPDATE.equals(format) && !date.contains(" "))) {
				return java.time.LocalDate.parse(date, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant()
						.toEpochMilli();
			}
			return LocalDateTime.parse(date, formatter).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		} catch (DateTimeParseException e) {
			throw new CustomException("INVALID_DATE_FORMAT", "Failed to parse date format in user");
		}
	}

	public void addUserDefaultFields(String mobileNumber, String tenantId, User userInfo) {
		Role role = getCitizenRole();
		userInfo.setRoles(Collections.singletonList(role));
		userInfo.setType("CITIZEN");
		userInfo.setUserName(mobileNumber);
		userInfo.setTenantId(tenantId);
	}

	private Role getCitizenRole() {
		Role role = new Role();
		role.setName("Citizen");
		return role;
	}

	public String getStateLevelTenant(String tenantId) {
		return tenantId.split("\\.")[0];
	}

}
