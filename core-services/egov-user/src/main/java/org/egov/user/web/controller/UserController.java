package org.egov.user.web.controller;

import static org.egov.tracer.http.HttpUtils.isInterServiceCall;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.domain.model.UpdateRequest;
import org.egov.user.domain.model.UpdateResponse;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.enums.UserType;
import org.egov.user.domain.model.UserDetail;
import org.egov.user.domain.model.UserSearchCriteria;
import org.egov.user.domain.service.LoginService;
import org.egov.user.domain.service.SsoService;
import org.egov.user.domain.service.TokenService;
import org.egov.user.domain.service.UserService;
import org.egov.user.web.contract.CreateUserRequest;
import org.egov.user.web.contract.LoginRequest;
import org.egov.user.web.contract.UserDetailResponse;
import org.egov.user.web.contract.UserRequest;
import org.egov.user.web.contract.UserSearchRequest;
import org.egov.user.web.contract.UserSearchResponse;
import org.egov.user.web.contract.UserSearchResponseContent;
import org.egov.user.web.contract.auth.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserController {

    private UserService userService;
    private TokenService tokenService;

    @Value("${mobile.number.validation.workaround.enabled}")
    private String mobileValidationWorkaroundEnabled;

    @Value("${otp.validation.register.mandatory}")
    private boolean IsValidationMandatory;

    @Value("${citizen.registration.withlogin.enabled}")
    private boolean isRegWithLoginEnabled;

    @Value("${egov.user.search.default.size}")
    private Integer defaultSearchSize;

//    @Autowired
//    private HpSsoValidateTokenService hpSsoValidateTokenService; 

    @Autowired
    private SsoService ssoService;

    @Autowired
    private LoginService loginService;

    @Autowired
    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    /**
     * end-point to create the citizen with otp.Here otp is mandatory to create
     * citizen.
     *
     * @param createUserRequest
     * @return
     */
    @PostMapping("/citizen/_create")
    public Object createCitizen(@RequestBody @Valid CreateUserRequest createUserRequest) {
        log.info("Received Citizen Registration Request  " + createUserRequest);
        User user = createUserRequest.toDomain(true);
        user.setOtpValidationMandatory(IsValidationMandatory);
        if (isRegWithLoginEnabled) {
            Object object = userService.registerWithLogin(user, createUserRequest.getRequestInfo());
            return new ResponseEntity<>(object, HttpStatus.OK);
        }
        User createdUser = userService.createCitizen(user, createUserRequest.getRequestInfo());
        return createResponse(createdUser);
    }

    /**
     * end-point to create the user without otp validation.
     *
     * @param createUserRequest
     * @param headers
     * @return
     */
    @PostMapping("/users/_createnovalidate")
    public UserDetailResponse createUserWithoutValidation(@RequestBody @Valid CreateUserRequest createUserRequest,
                                                          @RequestHeader HttpHeaders headers) {

        User user = createUserRequest.toDomain(true);
        user.setMobileValidationMandatory(isMobileValidationRequired(headers));
        user.setOtpValidationMandatory(false);
        final User newUser = userService.createUser(user, createUserRequest.getRequestInfo());
        return createResponse(newUser);
    }

    /**
     * end-point to search the users by providing userSearchRequest. In Request
     * if there is no active filed value, it will fetch only active users
     *
     * @param request
     * @return
     */
    @PostMapping("/_search")
    public UserSearchResponse get(@RequestBody @Valid UserSearchRequest request, @RequestHeader HttpHeaders headers) {

        log.info("Received User search Request  " + request);
        if (request.getActive() == null) {
            request.setActive(true);
        }
        return searchUsers(request, headers);
    }

    /**
     * end-point to search the users by providing userSearchRequest. In Request
     * if there is no active filed value, it will fetch all(active & inactive)
     * users.
     *
     * @param request
     * @return
     */
    @PostMapping("/v1/_search")
    public UserSearchResponse getV1(@RequestBody UserSearchRequest request, @RequestHeader HttpHeaders headers) {
        return searchUsers(request, headers);
    }

    /**
     * end-point to fetch the user details by access-token
     *
     * @param accessToken
     * @return
     */
    @PostMapping("/_details")
    public CustomUserDetails getUser(@RequestParam(value = "access_token") String accessToken) {
        final UserDetail userDetail = tokenService.getUser(accessToken);
        return new CustomUserDetails(userDetail);
        //  no encrypt/decrypt
    }

    /**
     * end-point to update the user details without otp validations.
     *
     * @param createUserRequest
     * @param headers
     * @return
     */
    @PostMapping("/users/_updatenovalidate")
    public UpdateResponse updateUserWithoutValidation(@RequestBody final @Valid CreateUserRequest createUserRequest,
                                                      @RequestHeader HttpHeaders headers) {
        User user = createUserRequest.toDomain(false);
        user.setMobileValidationMandatory(isMobileValidationRequired(headers));
        final User updatedUser = userService.updateWithoutOtpValidation(user, createUserRequest.getRequestInfo());
        return createResponseforUpdate(updatedUser);
    }

    /**
     * end-point to update user profile.
     *
     * @param createUserRequest
     * @return
     */
    @PostMapping("/profile/_update")
    public UpdateResponse patch(@RequestBody final @Valid CreateUserRequest createUserRequest) {
        log.info("Received Profile Update Request  " + createUserRequest);
        User user = createUserRequest.toDomain(false);
        final User updatedUser = userService.partialUpdate(user, createUserRequest.getRequestInfo());
        return createResponseforUpdate(updatedUser);
    }

    private UserDetailResponse createResponse(User newUser) {
        UserRequest userRequest = new UserRequest(newUser);
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
        return new UserDetailResponse(responseInfo, Collections.singletonList(userRequest));
    }

    private UpdateResponse createResponseforUpdate(User newUser) {
        UpdateRequest updateRequest = new UpdateRequest(newUser);
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
        return new UpdateResponse(responseInfo, Collections.singletonList(updateRequest));
    }

    private UserSearchResponse searchUsers(@RequestBody UserSearchRequest request, HttpHeaders headers) {

        UserSearchCriteria searchCriteria = request.toDomain();

        if (!isInterServiceCall(headers)) {
            if ((isEmpty(searchCriteria.getId()) && isEmpty(searchCriteria.getUuid())) && (searchCriteria.getLimit() > defaultSearchSize
                    || searchCriteria.getLimit() == 0))
                searchCriteria.setLimit(defaultSearchSize);
        }

        List<User> userModels = userService.searchUsers(searchCriteria, isInterServiceCall(headers), request.getRequestInfo());
        List<UserSearchResponseContent> userContracts = userModels.stream().map(UserSearchResponseContent::new)
                .collect(Collectors.toList());
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
        return new UserSearchResponse(responseInfo, userContracts);
    }

    private boolean isMobileValidationRequired(HttpHeaders headers) {
        boolean x_pass_through_gateway = !isInterServiceCall(headers);
        if (mobileValidationWorkaroundEnabled != null && Boolean.valueOf(mobileValidationWorkaroundEnabled)
                && !x_pass_through_gateway) {
            return false;
        }
        return true;
    }

	@PostMapping("/_landingPage")
	
    private ResponseEntity<?> landingPage(@RequestBody Map<String, String> tokenMap){
    	log.info("## landing page token : "+tokenMap.get("token"));
    	ResponseEntity<?> response = ssoService.getHpSsoValidateTokenResponse(tokenMap.get("token"));
    	return response;
    }
	
//	@PostMapping("/_landingPage")
//	
//    private ResponseEntity<?> landingPage(@RequestParam(value = "token") String token){
//    	log.info("## landing page token : "+token);
//    	ResponseEntity<?> response = ssoService.getHpSsoValidateTokenResponse(token);
//    	return response;
//    }
	
	
	@PostMapping("/_login")
	
	public Object employeeUserLogin(@RequestBody LoginRequest loginRequest) {
		if (StringUtils.isEmpty(loginRequest.getUserType())) {
			throw new RuntimeException("Employee login failed.");
		}

		Object response = loginService.employeeUserLogin(loginRequest);
		return response;
	}
	
	
	@GetMapping("/login/_uuid")
	public ResponseEntity<?>  getTokenAfterOtp(@RequestParam(value = "uuid") String uuid) {
		if (StringUtils.isEmpty(uuid)) {
			throw new RuntimeException("LoginFailed login failed.");
		}
		
	    List<String> users = new ArrayList<>();
	    users.add(uuid);
	    UserSearchCriteria searchCriteria = new UserSearchCriteria();
	    searchCriteria.setUuid(users);
	    searchCriteria.setTenantId("hp");
//	    searchCriteria.setType(UserType.CITIZEN);
        List<User> userModels = userService.searchUsers(searchCriteria, true, new RequestInfo());
		Object loginResponse = userService.getLoginAccess(userModels.get(0), userModels.get(0).getPassword());
		return new ResponseEntity<Object>(loginResponse, HttpStatus.OK);
	}

}
