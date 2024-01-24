package org.egov.hrms.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.hrms.config.PropertiesManager;
import org.egov.hrms.model.Employee;
import org.egov.hrms.model.enums.UserType;
import org.egov.hrms.repository.RestCallRepository;
import org.egov.hrms.utils.HRMSConstants;
import org.egov.hrms.web.contract.UserRequest;
import org.egov.hrms.web.contract.UserResponse;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.egov.hrms.utils.HRMSConstants.*;

@Slf4j
@Service
public class UserService {

    UserResponse createUser(UserRequest userRequest);

    UserResponse updateUser(UserRequest userRequest);

	@Autowired
	private MultiStateInstanceUtil centralInstanceUtil;

	@Value("${egov.user.create.endpoint}")
	private String userCreateEndpoint;

	@Value("${egov.user.search.endpoint}")
	private String userSearchEndpoint;

	@Value("${egov.user.update.endpoint}")
	private String userUpdateEndpoint;

	private String internalMicroserviceRoleUuid = null;

	@PostConstruct
	void initalizeSystemuser(){
		RequestInfo requestInfo = new RequestInfo();
		StringBuilder uri = new StringBuilder();
		uri.append(propertiesManager.getUserHost()).append(propertiesManager.getUserSearchEndpoint()); // URL for user search call
		Map<String, Object> userSearchRequest = new HashMap<>();
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", propertiesManager.getStateLevelTenantId());
		userSearchRequest.put("roleCodes", Collections.singletonList(INTERNALMICROSERVICEROLE_CODE));
		try {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) restCallRepository.fetchResult(uri, userSearchRequest);
			List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responseMap.get("user");
			if(users.size()==0)
				createInternalMicroserviceUser(requestInfo);
			internalMicroserviceRoleUuid = (String) users.get(0).get("uuid");
		}catch (Exception e) {
			throw new CustomException("EG_USER_SEARCH_ERROR", "Service returned null while fetching user");
		}

	}

	private void createInternalMicroserviceUser(RequestInfo requestInfo){
		Map<String, Object> userCreateRequest = new HashMap<>();
		//Creating role with INTERNAL_MICROSERVICE_ROLE
		Role role = Role.builder()
				.name(INTERNALMICROSERVICEROLE_NAME).code(INTERNALMICROSERVICEROLE_CODE)
				.tenantId(propertiesManager.getStateLevelTenantId()).build();
		User user = User.builder().userName(INTERNALMICROSERVICEUSER_USERNAME)
				.name(INTERNALMICROSERVICEUSER_NAME).mobileNumber(INTERNALMICROSERVICEUSER_MOBILENO)
				.type(INTERNALMICROSERVICEUSER_TYPE).tenantId(propertiesManager.getStateLevelTenantId())
				.roles(Collections.singletonList(role)).id(0L).build();

		userCreateRequest.put("RequestInfo", requestInfo);
		userCreateRequest.put("user", user);

		StringBuilder uri = new StringBuilder();
		uri.append(propertiesManager.getUserHost()).append(propertiesManager.getUserCreateEndpoint()); // URL for user create call

		try {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) restCallRepository.fetchResult(uri, userCreateRequest);
			List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responseMap.get("user");
			internalMicroserviceRoleUuid = (String) users.get(0).get("uuid");
		}catch (Exception e) {
			throw new CustomException("EG_USER_CRETE_ERROR", "Service returned throws error while creating user");
		}
	}
	
	public UserResponse createUser(UserRequest userRequest) {
		StringBuilder uri = new StringBuilder();
		uri.append(propertiesManager.getUserHost()).append(propertiesManager.getUserCreateEndpoint());
		UserResponse userResponse = null;
		try {
			userResponse = userCall(userRequest,uri);
		}catch(Exception e) {
			log.error("User created failed: ",e);
		}

		return userResponse;
	}
	
	public UserResponse updateUser(UserRequest userRequest) {
		StringBuilder uri = new StringBuilder();
		uri.append(propertiesManager.getUserHost()).append(propertiesManager.getUserUpdateEndpoint());
		UserResponse userResponse = null;
		try {
			userResponse = userCall(userRequest,uri);
		}catch(Exception e) {
			log.error("User created failed: ",e);
		}

		return userResponse;
	}
	
	public UserResponse getUser(RequestInfo requestInfo, Map<String, Object> userSearchCriteria ) {
		StringBuilder uri = new StringBuilder();
		Map<String, Object> userSearchReq = new HashMap<>();
		User userInfoCopy = requestInfo.getUserInfo();

		if(propertiesManager.getIsDecryptionEnable()){
			User enrichedUserInfo = getEncrichedandCopiedUserInfo(String.valueOf(userSearchCriteria.get("tenantId")));
			requestInfo.setUserInfo(enrichedUserInfo);
		}

		userSearchReq.put("RequestInfo", requestInfo);
		userSearchReq.put(HRMSConstants.HRMS_USER_SERACH_CRITERIA_USERTYPE_CODE,HRMSConstants.HRMS_USER_SERACH_CRITERIA_USERTYPE);
		for( String key: userSearchCriteria.keySet())
			userSearchReq.put(key, userSearchCriteria.get(key));
		uri.append(propertiesManager.getUserHost()).append(propertiesManager.getUserSearchEndpoint());
		UserResponse userResponse = new UserResponse();
		try {
			userResponse = userCall(userSearchReq,uri);
		}catch(Exception e) {
			log.error("User search failed: ",e);
		}
		if(propertiesManager.getIsDecryptionEnable())
			requestInfo.setUserInfo(userInfoCopy);

		return userResponse;
	}

	private User getEncrichedandCopiedUserInfo(String tenantId){
		//Creating role with INTERNAL_MICROSERVICE_ROLE
		Role role = Role.builder()
				.name(INTERNALMICROSERVICEROLE_NAME).code(INTERNALMICROSERVICEROLE_CODE)
				.tenantId(centralInstanceUtil.getStateLevelTenant(tenantId)).build();

		//Creating userinfo with uuid and role of internal micro service role
		User userInfo = User.builder()
				.uuid(internalMicroserviceRoleUuid)
				.type(INTERNALMICROSERVICEUSER_TYPE)
				.roles(Collections.singletonList(role)).id(0L).build();

		return userInfo;
	}


	/**
	 * Returns UserDetailResponse by calling user service with given uri and object
	 * @param userRequest Request object for user service
	 * @param uri The address of the endpoint
	 * @return Response from user service as parsed as userDetailResponse
	 */
	@SuppressWarnings("all")
	private UserResponse userCall(Object userRequest, StringBuilder uri) {
		String dobFormat = null;
		if(uri.toString().contains(userSearchEndpoint) || uri.toString().contains(userUpdateEndpoint))
			dobFormat="yyyy-MM-dd";
		else if(uri.toString().contains(userCreateEndpoint))
			dobFormat = "dd/MM/yyyy";
		try{
			LinkedHashMap responseMap = (LinkedHashMap) restCallRepository.fetchResult(uri, userRequest);
			parseResponse(responseMap,dobFormat);
			UserResponse userDetailResponse = objectMapper.convertValue(responseMap,UserResponse.class);
			return userDetailResponse;
		}
		catch(IllegalArgumentException  e) {
			throw new CustomException("IllegalArgumentException","ObjectMapper not able to convertValue in userCall");
		}
	}


	/**
	 * Parses date formats to long for all users in responseMap
	 * @param responeMap LinkedHashMap got from user api response
	 * @param dobFormat dob format (required because dob is returned in different format's in search and create response in user service)
	 */
	@SuppressWarnings("all")
	private void parseResponse(LinkedHashMap responeMap,String dobFormat){
		List<LinkedHashMap> users = (List<LinkedHashMap>)responeMap.get("user");
		String format1 = "dd-MM-yyyy HH:mm:ss";
		if(users!=null){
			users.forEach( map -> {
						map.put("createdDate",dateTolong((String)map.get("createdDate"),format1));
						if((String)map.get("lastModifiedDate")!=null)
							map.put("lastModifiedDate",dateTolong((String)map.get("lastModifiedDate"),format1));
						if((String)map.get("dob")!=null)
							map.put("dob",dateTolong((String)map.get("dob"),dobFormat));
						if((String)map.get("pwdExpiryDate")!=null)
							map.put("pwdExpiryDate",dateTolong((String)map.get("pwdExpiryDate"),format1));
					}
			);
		}
	}

	/**
	 * Converts date to long
	 * @param date date to be parsed
	 * @param format Format of the date
	 * @return Long value of date
	 */
	private Long dateTolong(String date,String format){
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date d = null;
		try {
			d = f.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return  d.getTime();
	}


}
