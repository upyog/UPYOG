package org.egov.user.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.domain.model.*;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.tracer.model.CustomException;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.UserDetail;
import org.egov.user.domain.model.UserSearchCriteria;
import org.egov.user.domain.service.TokenService;
import org.egov.user.domain.service.UserService;
import org.egov.user.web.contract.*;
import org.egov.user.web.contract.auth.CustomUserDetails;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

import static org.egov.tracer.http.HttpUtils.isInterServiceCall;
import static org.springframework.util.CollectionUtils.isEmpty;

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

    @Value("${digilocker.register}")
    private boolean isDigiLockerRegistration;

    @Value("${digilocker.search}")
    private boolean isDigiLockerSearch;

    @Value("${requester.service.host}")
    private String requesterServiceHost;
    
    @Value("${requester.service.endpoint}")
    private String requesterServiceEndpoint;
    
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
        return  createResponseforUpdate(updatedUser);
    }

    @PostMapping("/digilocker/oauth/token")
    public Object authDigiLocker(@RequestBody @Valid CreateUserRequest createUserRequest, @RequestHeader HttpHeaders headers) {
        RestTemplate restTemplate = new RestTemplate();
       // String url = "http://localhost:8270/requester-services-dx/user/details";
        
        UserSearchRequest request = new UserSearchRequest();

        TokenReq tokenReq = TokenReq.builder()
                .authToken(createUserRequest.getUser().getAccess_token())
                .build();

        TokenRequest tokenRequest = TokenRequest.builder()
                .requestInfo(createUserRequest.getRequestInfo())
                .tokenReq(tokenReq)
                .build();

        HttpEntity<TokenRequest> entity = new HttpEntity<>(tokenRequest, headers);

        ResponseEntity<String> validationApiResponse;
        try {
        	validationApiResponse = restTemplate.exchange(requesterServiceHost + requesterServiceEndpoint , HttpMethod.POST, entity, String.class);
            //validationApiResponse = restTemplate.exchange(url, HttpMethod.POST, entity, Strings.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Validation failed: " + e.getResponseBodyAsString(), e);
        }

        String validationApiResponseBody = validationApiResponse.getBody();
        JSONObject jsonResponse = new JSONObject(validationApiResponseBody);
        JSONObject userRes = jsonResponse.getJSONObject("UserRes");
        String validationMobileNumber = userRes.getString("mobile");
        log.info("validationMobileNumber"+validationMobileNumber);
        log.info("user Mobile Number is"+(createUserRequest.getUser().getMobileNumber()));
        
        

        if (validationMobileNumber.equalsIgnoreCase(createUserRequest.getUser().getMobileNumber())) {
            log.info("entering IF condition");

            request.setUserName(createUserRequest.getUser().getMobileNumber());
            request.setTenantId(createUserRequest.getUser().getTenantId());

            List<UserSearchResponseContent> userContracts = searchUsers(request, headers).getUserSearchResponseContent();
            if (!userContracts.isEmpty()) {

                User existingUser = userContracts.get(0).toUser();
                User user = new User().toUser(existingUser);
                user.setDigilockerid(createUserRequest.getUser().getDigilockerid());
                user.setDigilockerRegistration(isDigiLockerRegistration);
                Object updatedUser = userService.updateDigilockerID(user, existingUser, createUserRequest.getRequestInfo());
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);

            } else {
                User user = createUserRequest.toDomain(true);
                user.setUsername(user.getMobileNumber());
                user.setDigilockerRegistration(isDigiLockerRegistration);
                user.setDigilockerid(createUserRequest.getUser().getDigilockerid());
                Object createdUser = userService.registerWithLogin(user, createUserRequest.getRequestInfo());
                return new ResponseEntity<>(createdUser, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Mobile number validation failed", HttpStatus.BAD_REQUEST);
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
        log.info("searchCriteria_in controller"+searchCriteria);
        log.info("headers"+headers);

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

}