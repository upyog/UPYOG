package org.upyog.chb.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.*;
import digit.models.coremodels.UserDetailResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.chb.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This utility class provides methods for interacting with the user service
 * in the Community Hall Booking module.
 * 
 * Purpose:
 * - To handle user-related operations such as creating, searching, and updating user details.
 * - To simplify the process of sending requests to the user service and processing responses.
 * 
 * Dependencies:
 * - ServiceRequestRepository: Sends HTTP requests to the user service.
 * - ObjectMapper: Serializes and deserializes JSON objects for requests and responses.
 * - userCreateEndpoint: The endpoint for creating new users.
 * - userSearchEndpoint: The endpoint for searching existing users.
 * - userUpdateEndpoint: The endpoint for updating user details.
 * 
 * Features:
 * - Sends requests to the user service for creating, searching, and updating users.
 * - Processes responses and maps them to UserDetailResponse objects.
 * - Handles exceptions and logs errors for debugging and monitoring purposes.
 * 
 * Fields:
 * - userCreateEndpoint: The URL path for creating users.
 * - userSearchEndpoint: The URL path for searching users.
 * - userUpdateEndpoint: The URL path for updating users.
 * 
 * Methods:
 * 1. createUser:
 *    - Sends a request to the user service to create a new user.
 *    - Processes the response and returns the created user details.
 * 
 * 2. searchUser:
 *    - Sends a request to the user service to search for users based on criteria.
 *    - Returns a list of matching users.
 * 
 * 3. updateUser:
 *    - Sends a request to the user service to update user details.
 *    - Processes the response and returns the updated user details.
 * 
 * Usage:
 * - This class is used throughout the module to manage user-related operations.
 * - It ensures consistent and reusable logic for interacting with the user service.
 */
@Component
public class UserUtil {


    private ObjectMapper mapper;

    private ServiceRequestRepository serviceRequestRepository;

    @Value("${egov.user.create.path}")
    private String userCreateEndpoint;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.user.update.path}")
    private String userUpdateEndpoint;

    @Autowired
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
            dobFormat="yyyy-MM-dd";
        else if(uri.toString().contains(userCreateEndpoint))
            dobFormat = "dd/MM/yyyy";
        try{
            LinkedHashMap responseMap = (LinkedHashMap)serviceRequestRepository.fetchResult(uri, userRequest);
            parseResponse(responseMap,dobFormat);
            UserDetailResponse userDetailResponse = mapper.convertValue(responseMap,UserDetailResponse.class);
            return userDetailResponse;
        }
        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convertValue in userCall");
        }
    }


    /**
     * Parses date formats to long for all users in responseMap
     * @param responeMap LinkedHashMap got from user api response
     */

    public void parseResponse(LinkedHashMap responeMap, String dobFormat){
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
            throw new CustomException("INVALID_DATE_FORMAT","Failed to parse date format in user");
        }
        return  d.getTime();
    }

    /**
     * enriches the userInfo with statelevel tenantId and other fields
     * @param mobileNumber
     * @param tenantId
     * @param userInfo
     */
    public void addUserDefaultFields(String mobileNumber,String tenantId, User userInfo){
        Role role = getCitizenRole(tenantId);
        userInfo.setRoles(Collections.singletonList(role));
        userInfo.setType("CITIZEN");
        userInfo.setUserName(mobileNumber);
        userInfo.setTenantId(getStateLevelTenant(tenantId));
      //  userInfo.setActive(true);
    }

    /**
     * Returns role object for citizen
     * @param tenantId
     * @return
     */
    private Role getCitizenRole(String tenantId){
        Role role = new Role();
        role.setCode("CITIZEN");
        role.setName("Citizen");
        role.setTenantId(getStateLevelTenant(tenantId));
        return role;
    }

    public String getStateLevelTenant(String tenantId){
        return tenantId.split("\\.")[0];
    }

}