package org.egov.user.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.user.domain.model.*;

import org.egov.user.domain.model.User;
import org.egov.user.domain.model.UserDetail;
import org.egov.user.domain.model.UserSearchCriteria;
import org.egov.user.domain.service.TokenService;
import org.egov.user.domain.service.UserService;
import org.egov.user.web.contract.UserDetailResponseV2;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        return createResponseforUpdate(updatedUser);
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
/// Author - Abhijeet

    /**
     * Endpoint to create a new address for a user identified by their UUID.
     *
     * @param addressRequest The address details to be saved.
     * @return AddressResponse containing the created address details and response info.
     */
    @PostMapping("/_createAddress")
    public ResponseEntity<AddressResponse> createAddress(@RequestBody AddressRequest addressRequest) {

        Address userAddress = userService.createAddress(addressRequest.getUserUuid(), addressRequest.getAddress());
        List<Address> userAddressList = new ArrayList<>();
        userAddressList.add(userAddress);
        ResponseInfo responseInfo = ResponseInfo.builder().status(HttpStatus.CREATED.toString()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(new AddressResponse(responseInfo, userAddressList));
    }

    /**
     * Endpoint to retrieve address details for a user based on their UUID and tenant ID.
     *
     * @param uuid     The unique identifier of the user.
     * @param tenantId The tenant ID associated with the user.
     * @return AddressResponse containing the user's address details and response info.
     */
    @PostMapping("/_getAddress")
    public AddressResponse getAddress(@RequestParam(value = "uuid") String uuid, @RequestParam(value = "tenantId") String tenantId) {

        List<Address> userAddresses = userService.getAddress(uuid, tenantId);
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
        return new AddressResponse(responseInfo, userAddresses);
    }

    /**
     * Endpoint to update an existing address for a user identified by their UUID.
     *
     * @param addressRequest The address details to be updated.
     * @return AddressResponse containing the updated address details and response info.
     */
    @PostMapping("/_updateAddress")
    public AddressResponse updateAddress(@RequestBody AddressRequest addressRequest) {

        Address userAddress = userService.updateAddress(addressRequest.getAddress());
        List<Address> userAddressList = new ArrayList<>();
        userAddressList.add(userAddress);
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
        return new AddressResponse(responseInfo, userAddressList);
    }

    /**
     * API endpoint to create a new user with address details (V2).
     * <p>
     * Process:
     * - Converts the request DTO into a domain object.
     * - Determines if mobile validation is required based on headers.
     * - Calls the service layer to create a user.
     * - Returns a response containing the newly created user.
     *
     * @param createUserRequest the request payload containing user details
     * @param headers           HTTP headers for additional request context
     * @return the response object containing the created user details
     */
    @PostMapping("/users/v2/_create")
    public Object createUserWithAddress(@RequestBody @Valid CreateUserRequestV2 createUserRequest,
                                        @RequestHeader HttpHeaders headers) {
        log.info("Received User Registration Request " + createUserRequest);

        User user = createUserRequest.toDomain(true);
        user.setMobileValidationMandatory(isMobileValidationRequired(headers));
        user.setOtpValidationMandatory(false);

        final User newUser = userService.createUserWithAddressV2(user, createUserRequest.getRequestInfo());

        return createResponseV2(newUser);
    }

    /**
     * API endpoint to update user details along with address information (V2).
     * <p>
     * Process:
     * - Converts the request DTO into a domain object.
     * - Determines if mobile validation is required based on headers.
     * - Calls the service layer to update user details.
     * - Returns a response containing the updated user details.
     * <p>
     * Note: OTP validation is NOT required for this update process.
     *
     * @param createUserRequest the request payload containing updated user details
     * @param headers           HTTP headers for additional request context
     * @return the response object containing updated user details
     */
    @PostMapping("/users/v2/_update")
    public UpdateResponse updateUserV2(@RequestBody @Valid CreateUserRequestV2 createUserRequest,
                                                @RequestHeader HttpHeaders headers) {

        User user = createUserRequest.toDomain(false);
        user.setMobileValidationMandatory(isMobileValidationRequired(headers));

        final User updatedUser = userService.updateUserV2(user, createUserRequest.getRequestInfo());

        return createResponseforUpdate(updatedUser);
    }

    /**
     * API endpoint to search users along with their address details (V2).
     * <p>
     * Process:
     * - Converts the search request DTO into a domain object.
     * - If the request is not an inter-service call:
     * - Limits the search results if no specific user ID or UUID is provided.
     * - Calls the service layer to fetch matching users based on search criteria.
     * - Constructs the response with user details.
     * <p>
     * Note: If the `active` field is not specified in the request, both active
     * and inactive users will be fetched.
     *
     * @param request the request payload containing search criteria
     * @param headers HTTP headers for additional request context
     * @return the response object containing matching user details
     */
    @PostMapping("/users/v2/_search")
    public UserSearchResponse getUsersV2(@RequestBody UserSearchRequestV2 request, @RequestHeader HttpHeaders headers) {

        UserSearchCriteria searchCriteria = request.toDomain();

        if (!isInterServiceCall(headers)) {
            if ((isEmpty(searchCriteria.getId()) && isEmpty(searchCriteria.getUuid())) &&
                    (searchCriteria.getLimit() > defaultSearchSize || searchCriteria.getLimit() == 0)) {
                searchCriteria.setLimit(defaultSearchSize);
            }
        }

        List<User> userModels = userService.searchUsersV2(searchCriteria, isInterServiceCall(headers), request.getRequestInfo());
        List<UserSearchResponseContent> userContracts = userModels.stream()
                .map(UserSearchResponseContent::new)
                .collect(Collectors.toList());
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();

        return new UserSearchResponse(responseInfo, userContracts);
    }

    private UserDetailResponseV2 createResponseV2(User newUser) {
        UserRequestV2 userRequestV2 = new UserRequestV2(newUser);
        ResponseInfo responseInfo = ResponseInfo.builder().status(String.valueOf(HttpStatus.OK.value())).build();
        return new UserDetailResponseV2(responseInfo, Collections.singletonList(userRequestV2));
    }
}
