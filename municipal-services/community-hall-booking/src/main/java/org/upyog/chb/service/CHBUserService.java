package org.upyog.chb.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;

import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.web.models.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.upyog.chb.web.models.user.CreateUserRequest;
import org.upyog.chb.web.models.user.UserResponse;
import org.upyog.chb.web.models.user.UserSearchRequest;


@Service
@Slf4j
public class CHBUserService {

  @Value("${egov.user.host}")
  private String userHost;

  @Value("${egov.user.context.path}")
  private String userContextPath;

  @Value("${egov.user.search.path}")
  private String userSearchEndpoint;

  @Value("${egov.user.create.path}")
  private String userCreateEndpoint;

  @Value("${egov.user.update.path}")
  private String userUpdateEndpoint;

  @Autowired
  private CommunityHallBookingConfiguration config;

  @Autowired
  private org.upyog.chb.repository.ServiceRequestRepository serviceRequestRepository;

  @Autowired
  private ObjectMapper mapper;

  /**
   * Call search in user service based on ownerids from criteria
   *
   * @param criteria
   *            The search criteria containing the ownerids
   * @param requestInfo
   *            The requestInfo of the request
   * @return Search response from user service based on ownerIds
   */
  public UserResponse getUser(CommunityHallBookingSearchCriteria criteria, RequestInfo requestInfo) {
    UserSearchRequest userSearchRequest = getUserSearchRequest(criteria, requestInfo);
    boolean hasUuids = userSearchRequest.getUuid() != null && !userSearchRequest.getUuid().isEmpty();
    boolean hasMobile = org.apache.commons.lang3.StringUtils.isNotBlank(userSearchRequest.getMobileNumber());
    boolean hasName = org.apache.commons.lang3.StringUtils.isNotBlank(userSearchRequest.getName());
    boolean hasUserName = org.apache.commons.lang3.StringUtils.isNotBlank(userSearchRequest.getUserName());
    if (!(hasUuids || hasMobile || hasName || hasUserName)) {
      return UserResponse.builder().user(java.util.Collections.emptyList()).build();
    }
    StringBuilder uri = new StringBuilder(userHost).append(userSearchEndpoint);
    UserResponse userDetailResponse = userCall(userSearchRequest, uri);
    return userDetailResponse;
  }

  /**
   * Creates userSearchRequest from ndcSearchCriteria
   *
   * @param criteria
   *            The ndcSearch criteria
   * @param requestInfo
   *            The requestInfo of the request
   * @return The UserSearchRequest based on ownerIds
   */
  private UserSearchRequest getUserSearchRequest(CommunityHallBookingSearchCriteria criteria, RequestInfo requestInfo) {
    UserSearchRequest userSearchRequest = new UserSearchRequest();
    userSearchRequest.setRequestInfo(requestInfo);
    userSearchRequest.setTenantId(criteria.getTenantId().split("\\.")[0]);
    userSearchRequest.setActive(true);
    // Only pass valid 10-digit mobile to user service
    if (StringUtils.isNotBlank(criteria.getMobileNumber())) {
      String digits = criteria.getMobileNumber().replaceAll("\\D", "");
      if (digits.length() == 10) {
        userSearchRequest.setMobileNumber(digits);
      }
    }
    userSearchRequest.setName(criteria.getName());
    /* userSearchRequest.setUserType("CITIZEN"); */
    Set<String> userIds = criteria.getOwnerIds();
    if (!CollectionUtils.isEmpty(criteria.getOwnerIds()))
      userSearchRequest.setUuid( new ArrayList<>(userIds));
    return userSearchRequest;
  }

  /**
   * Returns UserDetailResponse by calling user service with given uri and
   * object
   *
   * @param userRequest
   *            Request object for user service
   * @param uri
   *            The address of the end point
   * @return Response from user service as parsed as userDetailResponse
   */
  @SuppressWarnings("rawtypes")
  UserResponse userCall(Object userRequest, StringBuilder uri) {
    String dobFormat = null;
    if(uri.toString().contains(userSearchEndpoint)||uri.toString().contains(userUpdateEndpoint))
      dobFormat="yyyy-MM-dd";
    else if(uri.toString().contains(userCreateEndpoint))
      dobFormat = "dd/MM/yyyy";
    try {
      LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(uri, userRequest);
      parseResponse(responseMap, dobFormat);
      UserResponse userDetailResponse = mapper.convertValue(responseMap, UserResponse.class);
      return userDetailResponse;
    } catch (IllegalArgumentException e) {
      throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
    }
  }

  /**
   * Parses date formats to long for all users in responseMap
   *
   * @param responeMap
   *            LinkedHashMap got from user api response
   */

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void parseResponse(LinkedHashMap responeMap, String dobFormat) {
    List<LinkedHashMap> users = (List<LinkedHashMap>) responeMap.get("user");
    String format1 = "dd-MM-yyyy HH:mm:ss";
    if (users != null) {
      users.forEach(map -> {
        map.put("createdDate", dateTolong((String) map.get("createdDate"), format1));
        if ((String) map.get("lastModifiedDate") != null)
          map.put("lastModifiedDate", dateTolong((String) map.get("lastModifiedDate"), format1));
        if ((String) map.get("dob") != null)
          map.put("dob", dateTolong((String) map.get("dob"), dobFormat));
        if ((String) map.get("pwdExpiryDate") != null)
          map.put("pwdExpiryDate", dateTolong((String) map.get("pwdExpiryDate"), format1));
      });
    }
  }

  /**
   * Converts date to long
   *
   * @param date
   *            date to be parsed
   * @param format
   *            Format of the date
   * @return Long value of date
   */
  private Long dateTolong(String date, String format) {
    SimpleDateFormat f = new SimpleDateFormat(format);
    Date d = null;
    try {
      d = f.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return d.getTime();
  }


  public void createUser(RequestInfo requestInfo,CommunityHallBookingDetail application) {
    Role role = getCitizenRole(application.getTenantId());
    if (application.getOwners() == null) {
      throw new CustomException("INVALID USER", "The applications owners list is empty");
    }
    application.getOwners().forEach(owner ->
    {
      if (owner.getUuid() == null) {
        addUserDefaultFields(application.getTenantId(), role, owner);

        // Normalize and validate mobileNumber (must be 10 digits) when uuid is absent
        if (org.apache.commons.lang3.StringUtils.isNotBlank(owner.getMobileNumber())) {
          String digits = owner.getMobileNumber().replaceAll("\\D", "");
          owner.setMobileNumber(digits);
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(owner.getMobileNumber()) || owner.getMobileNumber().length() != 10) {
          throw new CustomException("INVALID_OWNER_CONTACT", "Owner must provide a valid 10-digit mobile number or an existing uuid");
        }

        UserResponse existingUserResponse = userExists(owner, requestInfo);

        if (!existingUserResponse.getUser().isEmpty()) {
          OwnerInfo existingUser = (OwnerInfo) existingUserResponse.getUser().get(0);
          log.info("User already exists with UUID: " + existingUser.getUuid());
          owner.setUuid(existingUser.getUuid());
          setOwnerFields(owner, existingUserResponse, requestInfo);
        } else {
//						  UserResponse userResponse = userExists(owner,requestInfo);
          StringBuilder uri = new StringBuilder(userHost).append(userContextPath).append(userCreateEndpoint);
          setUserName(owner);
          UserResponse userResponse = userCall(new CreateUserRequest(requestInfo, owner), uri);
          if (userResponse.getUser().get(0).getUuid() == null) {
            throw new CustomException("INVALID USER RESPONSE", "The user created has uuid as null");
          }
          log.info("owner created --> " + userResponse.getUser().get(0).getUuid());
          setOwnerFields(owner, userResponse, requestInfo);
        }
      } else {
        UserResponse userResponse = userExists(owner, requestInfo);
        if (userResponse.getUser().isEmpty())
          throw new CustomException("INVALID USER", "The uuid " + owner.getUuid() + " does not exists");
        StringBuilder uri = new StringBuilder(userHost);
        uri.append(userContextPath).append(userUpdateEndpoint);
        OwnerInfo ownerInfo = new OwnerInfo();
        ownerInfo.addUserWithoutAuditDetail(owner);
        addNonUpdatableFields(ownerInfo, userResponse.getUser().get(0));
        userResponse = userCall(new CreateUserRequest(requestInfo, ownerInfo), uri);
        setOwnerFields(owner, userResponse, requestInfo);
      }
    });
  }

  private void addUserDefaultFields(String tenantId,Role role,OwnerInfo owner){
    owner.setActive(true);
    owner.setTenantId(tenantId.split("\\.")[0]);
    owner.setRoles(Collections.singletonList(role));
    owner.setType("CITIZEN");
    owner.setCreatedDate(null);
    owner.setCreatedBy(null );
    owner.setLastModifiedDate(null);
    owner.setLastModifiedBy(null );
  }

  private Role getCitizenRole(String tenantId){
    Role role = new Role();
    role.setCode("CITIZEN");
    role.setName("Citizen");
    role.setTenantId(getStateLevelTenant(tenantId));
    return role;
  }

  private String getStateLevelTenant(String tenantId){
    return tenantId.split("\\.")[0];
  }

  private void setUserName(OwnerInfo owner){
    String username;
    if(StringUtils.isNotBlank(owner.getMobileNumber()))
      username = owner.getMobileNumber();
    else
      username = UUID.randomUUID().toString();
    owner.setUserName(username);

  }

  private void setOwnerFields(OwnerInfo owner, UserResponse userResponse,RequestInfo requestInfo){
    owner.setUuid(userResponse.getUser().get(0).getUuid());
    owner.setId(userResponse.getUser().get(0).getId());
    owner.setUserName((userResponse.getUser().get(0).getUserName()));
//		owner.setCreatedBy(requestInfo.getUserInfo().getUuid());
//		owner.setLastModifiedBy(requestInfo.getUserInfo().getUuid());
    owner.setCreatedDate(System.currentTimeMillis());
    owner.setLastModifiedDate(System.currentTimeMillis());
    owner.setActive(userResponse.getUser().get(0).getActive());
  }

  private UserResponse userExists(OwnerInfo owner,RequestInfo requestInfo){
    UserSearchRequest userSearchRequest =new UserSearchRequest();
    userSearchRequest.setTenantId(owner.getTenantId());
    userSearchRequest.setRequestInfo(requestInfo);
    userSearchRequest.setActive(true);
    userSearchRequest.setUserType(owner.getType());
    if(StringUtils.isNotBlank(owner.getMobileNumber())) {
      String digits = owner.getMobileNumber().replaceAll("\\D", "");
      if (digits.length() == 10) {
        userSearchRequest.setMobileNumber(digits);
        if (StringUtils.isBlank(owner.getUserName()))
          userSearchRequest.setUserName(digits);
        else
          userSearchRequest.setUserName(owner.getUserName());
      } else {
        // Do not set invalid mobile; rely on uuid/username if available
        if (StringUtils.isNotBlank(owner.getUserName()))
          userSearchRequest.setUserName(owner.getUserName());
      }
    }
    if(StringUtils.isNotBlank(owner.getUuid()))
      userSearchRequest.setUuid(Arrays.asList(owner.getUuid()));
    StringBuilder uri = new StringBuilder(userHost).append(userSearchEndpoint);
    return userCall(userSearchRequest,uri);
  }

  private void addNonUpdatableFields(User user,User userFromSearchResult){
    user.setUserName(userFromSearchResult.getUserName());
    user.setId(userFromSearchResult.getId());
    user.setActive(userFromSearchResult.getActive());
    user.setPassword(userFromSearchResult.getPassword());
  }

}
