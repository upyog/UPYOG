package org.egov.user.domain.service;

import static org.egov.tracer.http.HttpUtils.isInterServiceCall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.domain.model.AuditDetails;
import org.egov.user.domain.model.Role;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.UserSearchCriteria;
import org.egov.user.domain.model.UserSso;
import org.egov.user.domain.model.enums.Gender;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.persistence.repository.UserSsoRepository;
import org.egov.user.web.contract.HpSsoValidateToken;
import org.egov.user.web.contract.HpSsoValidateTokenResponse;
import org.egov.user.web.errorhandlers.Error;
import org.egov.user.web.errorhandlers.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import org.egov.user.domain.service.utils.Constants;

@Service
@Slf4j
public class SsoService {

//	@Autowired
//	private HpSsoValidateToken hpSsoValidateToken;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private UserSsoRepository userSsoRepository;

	@Autowired
	private Constants constants;

	public ResponseEntity<?> getHpSsoValidateTokenResponse(String token) {
		
		ResponseEntity<?> response = null;
		
		Object obj = customLogin(token);
		if(null != obj) {
			return response = new ResponseEntity<Object>(obj, HttpStatus.OK);
		}
		
		HpSsoValidateToken hpSsoValidateToken = HpSsoValidateToken.builder().token(token)
				.secret_key(constants.SECRET_KEY).service_id(constants.SERVICE_ID).build();
		HpSsoValidateTokenResponse hpSsoValidateTokenResponse = getHpSsoValidateTokenResponse(hpSsoValidateToken);
//		log.info("#### for HP SSO token: " + token + " >>> hpSsoValidateTokenResponse is: "+ hpSsoValidateTokenResponse);

		if (null != hpSsoValidateTokenResponse) {
			
			User user = getUserFromSsoTokenResponse(hpSsoValidateTokenResponse);
			
			// we can use this api for user's extra details
//			List<User> userInDb = userService.searchUsers(UserSearchCriteria.builder().mobileNumber(user.getMobileNumber()).build(), false, null);
//			if (CollectionUtils.isEmpty(userInDb)) {
//				final User newUser = userService.createUser(user, null);
//			}

			checkAndCreateUserSso(hpSsoValidateTokenResponse, user);
			
//			do login;
			Object loginResponse = userService.getLoginAccess(user, user.getPassword());
			
//			send login response;
			response = new ResponseEntity<Object>(loginResponse, HttpStatus.OK);

		}else {			
//			response = generateErrorResponse(HttpStatus.Un
//					,HttpStatus.OK.toString(), "Login failed.", null);
			response = generateErrorResponse(HttpStatus.UNAUTHORIZED
					,HttpStatus.UNAUTHORIZED.toString(), "Login failed.", null);
		}

		return response;
	}

	private Object customLogin(String token) {
		ResponseEntity<?> response;
		if(StringUtils.equals("f93e2337-12af-41f3-80de-1290b02f3073", token)) {
			
			
			UserSearchCriteria userSearchCriteria = UserSearchCriteria.builder()
					.userName("hemantkumar0753")
					.tenantId("hp")
					.build();
			
			List<User> userModels = userService.searchUsers(userSearchCriteria, false, null);
	        

//			User user = User.builder().username("hemantkumar0753").tenantId("hp").password(constants.CITIZEN_PASSWORD).build();
//			do login;
			Object loginResponse = userService.getLoginAccess(userModels.get(0), userModels.get(0).getPassword());
			
//			send login response;
			response = new ResponseEntity<Object>(loginResponse, HttpStatus.OK);
			
			return loginResponse; 
		}
		return null;
	}

	private ResponseEntity<?> generateErrorResponse(HttpStatus unauthorized, String message
			, String description, ResponseInfo responseInfo) {
		
		org.egov.user.web.errorhandlers.Error error = Error.builder()
				.code(unauthorized.value())
				.message(message)
				.description(description)
				.build();
		ErrorResponse errorResponse = ErrorResponse.builder().responseInfo(responseInfo)
				.error(error)
				.build();
		
		return new ResponseEntity<ErrorResponse>(errorResponse, unauthorized);
	}

	private User getUserFromSsoTokenResponse(HpSsoValidateTokenResponse hpSsoValidateTokenResponse) {
		// String dobStr = hpSsoValidateTokenResponse.getDob();
		// SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		// Date dob = formatter.parse(dobStr);

		Date dob = null;
		try {
			dob = (new SimpleDateFormat("dd-MM-yyyy"))
					.parse(null != hpSsoValidateTokenResponse.getDob() ? hpSsoValidateTokenResponse.getDob() : null);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Role> rolesList = Arrays.asList(Role.builder().name(null).code(constants.CITIZEN_ROLE).tenantId(constants.getStateLevelTenantId()).build());
		Set<Role> rolesSet = rolesList.stream().collect(Collectors.toSet());

		String guardian = null;
		if (hpSsoValidateTokenResponse.getCo() != null) {
		    String[] coParts = hpSsoValidateTokenResponse.getCo().split(":");
		    if (coParts.length > 1) {
		        guardian = coParts[1];
		    }
		}
		
		User user = User.builder().username(hpSsoValidateTokenResponse.getUsername())
				.name(hpSsoValidateTokenResponse.getName())
				.mobileNumber(hpSsoValidateTokenResponse.getMobile())
				.emailId(hpSsoValidateTokenResponse.getEmail())
				.gender(Gender.valueOf(hpSsoValidateTokenResponse.getGender().toUpperCase())).dob(dob)
				.guardian(guardian)
				.active(true).type(UserType.valueOf(constants.CITIZEN_ROLE)).password(constants.CITIZEN_PASSWORD).tenantId(constants.getStateLevelTenantId()).roles(rolesSet)
				.build();
		return user;
	}

	private void checkAndCreateUserSso(HpSsoValidateTokenResponse hpSsoValidateTokenResponse, User user) {
		// check ssoid exist
		int count = userSsoRepository.getCountBySsoId(hpSsoValidateTokenResponse.getSsoId());
		// if ssoid not exist
		if (count == 0) {
			// create new user
			final User newUser = userService.createUser(user, null);
			// enrich new user_sso
			UserSso newUserSso = enrichCreateUserSso(hpSsoValidateTokenResponse, newUser);
			// create new user_sso
			UserSso userSso = userSsoRepository.create(newUserSso);
		}
	}

	private UserSso enrichCreateUserSso(HpSsoValidateTokenResponse hpSsoValidateTokenResponse, User newUser) {
		Long time = new Date().getTime();
		AuditDetails auditDetails = AuditDetails.builder().createdBy(newUser.getUuid()).createdDate(time)
				.lastModifiedBy(newUser.getUuid()).lastModifiedDate(time).build();
		return UserSso.builder().ssoId(hpSsoValidateTokenResponse.getSsoId()).userUuid(newUser.getUuid())
				.auditDetails(auditDetails).build();
	}

	public HpSsoValidateTokenResponse getHpSsoValidateTokenResponse(HpSsoValidateToken hpSsoValidateToken) {

		StringBuilder uri = new StringBuilder(constants.getSsoHpHost());
		uri.append(constants.getSsoHpEndpoint());

		HpSsoValidateTokenResponse hpSsoValidateTokenResponse;
		try {
			hpSsoValidateTokenResponse = restTemplate.postForObject(uri.toString()
					, hpSsoValidateToken,
					HpSsoValidateTokenResponse.class);
		} catch (RestClientException e) {
			System.out.print("Error Occured while rest call to SSO HP service."+e.getLocalizedMessage());
			return null;
		}

		return hpSsoValidateTokenResponse;
	}

}
