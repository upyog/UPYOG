package org.upyog.sv.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.*;
import digit.models.coremodels.UserDetailResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.ServiceRequestRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@SuppressWarnings({"java:S2143", "java:S2638", "java:S3437"})
public class UserUtil {

	private static final String FIELD_USER = "user";
	private static final String FIELD_CREATED_DATE = "createdDate";
	private static final String FIELD_LAST_MODIFIED_DATE = "lastModifiedDate";
	private static final String FIELD_DOB = "dob";
	private static final String FIELD_PWD_EXPIRY_DATE = "pwdExpiryDate";
	private static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	private static final String SEARCH_DOB_FORMAT = "yyyy-MM-dd";
	private static final String CREATE_DOB_FORMAT = "dd/MM/yyyy";

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
     * Returns UserDetailResponse by calling user service with given uri and object
     * @param userRequest Request object for user service
     * @param uri The address of the endpoint
     * @return Response from user service as parsed as userDetailResponse
     */

    public UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
        String dobFormat = null;
        if(uri.toString().contains(userSearchEndpoint)  || uri.toString().contains(userUpdateEndpoint))
            dobFormat = SEARCH_DOB_FORMAT;
        else if(uri.toString().contains(userCreateEndpoint))
            dobFormat = CREATE_DOB_FORMAT;
        try{
            Map<String, Object> responseMap = serviceRequestRepository.fetchResult(uri, userRequest);
            parseResponse(responseMap, dobFormat);
            return mapper.convertValue(responseMap, UserDetailResponse.class);
        }
        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convertValue in userCall");
        }
    }


    /**
     * Parses date formats to long for all users in responseMap
     * @param responeMap LinkedHashMap got from user api response
     * @param dobFormat date of birth format
     */

    public void parseResponse(Map<String, Object> responeMap, String dobFormat){
        List<Map<String, Object>> users = mapper.convertValue(responeMap.get(FIELD_USER),
                mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        if(users!=null){
            users.forEach( map -> {
                        map.put(FIELD_CREATED_DATE, dateTolong((String) map.get(FIELD_CREATED_DATE), DATE_TIME_FORMAT));
                        if((String) map.get(FIELD_LAST_MODIFIED_DATE) != null)
                            map.put(FIELD_LAST_MODIFIED_DATE, dateTolong((String) map.get(FIELD_LAST_MODIFIED_DATE), DATE_TIME_FORMAT));
                        if((String) map.get(FIELD_DOB) != null)
                            map.put(FIELD_DOB, dateTolong((String) map.get(FIELD_DOB), dobFormat));
                        if((String) map.get(FIELD_PWD_EXPIRY_DATE) != null)
                            map.put(FIELD_PWD_EXPIRY_DATE, dateTolong((String) map.get(FIELD_PWD_EXPIRY_DATE), DATE_TIME_FORMAT));
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
    @SuppressWarnings("java:S2143")
    private Long dateTolong(String date, String format){
        SimpleDateFormat f = new SimpleDateFormat(format);
        Date d;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            throw new CustomException("INVALID_DATE_FORMAT","Failed to parse date format in user");
        }
        return  d.getTime();
    }

    /**
     * enriches the userInfo with statelevel tenantId and other fields
     * @param mobileNumber mobile number of the user
     * @param tenantId tenant identifier
     * @param userInfo user information to enrich
     */
    public void addUserDefaultFields(String mobileNumber, String tenantId, User userInfo){
        Role role = getCitizenRole();
        userInfo.setRoles(Collections.singletonList(role));
        userInfo.setType(StreetVendingConstants.CITIZEN);
        userInfo.setUserName(mobileNumber);
    }

    /**
     * Returns role object for citizen
     * @return citizen role
     */
    private Role getCitizenRole(){
        Role role = new Role();
        role.setName("Citizen");
        return role;
    }

    public String getStateLevelTenant(String tenantId){
        return tenantId.split("\\.")[0];
    }

}
