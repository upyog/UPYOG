package org.upyog.cdwm.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.UserDetailResponse;
import digit.models.coremodels.user.Role;
import digit.models.coremodels.user.User;
import digit.models.coremodels.user.enums.UserType;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.repository.ServiceRequestRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class UserUtil {

	private static final String KEY_USER = "user";
	private static final String KEY_CREATED_DATE = "createdDate";
	private static final String KEY_LAST_MODIFIED_DATE = "lastModifiedDate";
	private static final String KEY_DOB = "dob";
	private static final String KEY_PWD_EXPIRY_DATE = "pwdExpiryDate";

	private final ObjectMapper mapper;

	private final ServiceRequestRepository serviceRequestRepository;

	private final CNDConfiguration config;

	public UserUtil(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository,
			CNDConfiguration config) {
		this.mapper = mapper;
		this.serviceRequestRepository = serviceRequestRepository;
		this.config = config;
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
		if (uri.toString().contains(config.getUserV2SearchEndpoint()) || uri.toString().contains(config.getUserV2UpdateEndpoint()))
			dobFormat = "yyyy-MM-dd";
		else if (uri.toString().contains(config.getUserV2CreateEndpoint()))
			dobFormat = "dd/MM/yyyy";
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> responseMap = (Map<String, Object>) serviceRequestRepository.fetchResult(uri, userRequest);
			parseResponse(responseMap, dobFormat);
			return mapper.convertValue(responseMap, UserDetailResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
		}
	}

	/**
	 * Parses date formats to long for all users in responseMap
	 * 
	 * @param responeMap Map got from user api response
	 */

	public void parseResponse(Map<String, Object> responeMap, String dobFormat) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> users = (List<Map<String, Object>>) responeMap.get(KEY_USER);
		String format1 = "dd-MM-yyyy HH:mm:ss";
		if (users != null) {
			users.forEach(map -> {
				map.put(KEY_CREATED_DATE, dateTolong((String) map.get(KEY_CREATED_DATE), format1));
				if ((String) map.get(KEY_LAST_MODIFIED_DATE) != null)
					map.put(KEY_LAST_MODIFIED_DATE, dateTolong((String) map.get(KEY_LAST_MODIFIED_DATE), format1));
				if ((String) map.get(KEY_DOB) != null)
					map.put(KEY_DOB, dateTolong((String) map.get(KEY_DOB), dobFormat));
				if ((String) map.get(KEY_PWD_EXPIRY_DATE) != null)
					map.put(KEY_PWD_EXPIRY_DATE, dateTolong((String) map.get(KEY_PWD_EXPIRY_DATE), format1));
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
	public static Long dateTolong(String date, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = f.parse(date);
		} catch (ParseException e) {
			throw new CustomException("INVALID_DATE_FORMAT", "Failed to parse date format in user");
		}
		return d.getTime();
	}

	/**
	 * enriches the userInfo with statelevel tenantId and other fields
	 * 
	 * @param tenantId
	 * @param userInfo
	 */
	public void addUserDefaultFields(String tenantId, User userInfo) {
		Role role = getCitizenRole(tenantId);
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(role);
		userInfo.setRoles(roleSet);
		userInfo.setType(UserType.CITIZEN);
		userInfo.setTenantId(getStateLevelTenant(tenantId));
		userInfo.setActive(true);
	}

	/**
	 * Returns role object for citizen
	 * 
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
