package org.egov.user.domain.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.user.config.UserServiceConstants;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.UserSearchCriteria;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.web.contract.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.EmailValidator;

import lombok.extern.slf4j.Slf4j;
import okhttp3.internal.http2.ErrorCode;

@Service
@Slf4j
public class LoginService {

	@Value("${egov.user.host}")
	private String userServiceHost;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserService userService;

//	public Object enterpriseUserLogin(String username, String tenantId, String otp, String password) {
//		String emailId = StringUtils.EMPTY;
//		String mobileNumber = StringUtils.EMPTY;
//
//		// Determining the username from Email or Phone Number
//		if (EmailValidator.getInstance().isValid(username)) {
//			emailId = username;
//			UserSearchCriteria searchCriteria = UserSearchCriteria.builder().emailId(emailId).tenantId(tenantId)
//					.build();
//			List<User> users = userService.searchUsers(searchCriteria, true, null);
//			if (CollectionUtils.isNotEmpty(users)) {
//				mobileNumber = users.get(0).getMobileNumber();
//			}
//		} else if (StringUtils.isNumeric(username)) {
//			mobileNumber = username;
//		}
//
//		if (StringUtils.isEmpty(mobileNumber)) {
//			throw new UserServiceException(ErrorCode.ERR_UNAUTHORIZE_LOGIN);
//		}
//
//		return authenticateUser(mobileNumber, password, tenantId, UserType.ENTERPRISE.name());
//	}

	public Object employeeUserLogin(LoginRequest loginRequest) {
		String password = loginRequest.getPassword();
		String emailId = StringUtils.EMPTY;
		String mobileNumber = StringUtils.EMPTY;
		String usernameValue = loginRequest.getUsername();
		String finalUsername = StringUtils.EMPTY ;
		UserType userType = UserType.fromValue(loginRequest.getUserType());
		String tenantId = loginRequest.getTenantId();
		List<User> users = null;

		// Determining the username from Email or Phone Number
		
		if (EmailValidator.getInstance().isValid(usernameValue)) {
			emailId = usernameValue;
			UserSearchCriteria searchCriteria = UserSearchCriteria.builder().emailId(emailId).tenantId(tenantId).type(userType)
					.build();
			users = userService.searchUsers(searchCriteria, true, null);
			if (CollectionUtils.isNotEmpty(users)) {
				finalUsername = users.get(0).getUsername();
			}
		} else if (usernameValue.matches(UserServiceConstants.PATTERN_MOBILE)) {
			mobileNumber = usernameValue;
			UserSearchCriteria searchCriteria = UserSearchCriteria.builder().mobileNumber(mobileNumber).type(userType)
					.tenantId(tenantId).build();
			users = userService.searchUsers(searchCriteria, true, null);
			if (CollectionUtils.isNotEmpty(users)) {
				finalUsername = users.get(0).getUsername();
			}
		} else {
			UserSearchCriteria searchCriteria = UserSearchCriteria.builder().userName(usernameValue).tenantId(tenantId).type(userType)
					.build();
			users = userService.searchUsers(searchCriteria, true, null);
			if (CollectionUtils.isNotEmpty(users)) {
				finalUsername = users.get(0).getUsername();
			} else {
				throw new RuntimeException("Unauthorized login.");
			}
		}

		LinkedHashMap<String, LinkedHashMap<String, String>> authenticateUserResponse = (LinkedHashMap<String, LinkedHashMap<String, String>>) authenticateUser(
				finalUsername, password, tenantId, userType.name());

		if (authenticateUserResponse.get("access_token") != null) {
			User user = users.get(0);
			if (user.getPasswordExpiryDate().before(new Date())) {
				throw new RuntimeException("Password Expired.");
			}
		}

		return authenticateUserResponse;
	}

	private Object authenticateUser(String username, String password, String tenantId, String userType) {

		log.info("Fetch access token for register with login flow");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.set("Authorization", "Basic ZWdvdi11c2VyLWNsaWVudDo=");
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("username", username);
			map.add("password", password);
			map.add("grant_type", "password");
			map.add("scope", "read");
			map.add("tenantId", tenantId);
			map.add("userType", userType);

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			return restTemplate.postForEntity(userServiceHost + "/user/oauth/token", request, Map.class).getBody();

		} catch (Exception e) {
			log.error("Error occurred while user logging", e);
			throw new RuntimeException("Unauthorized login.");
		}
	}
}
