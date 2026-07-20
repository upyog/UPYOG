package org.upyog.rs.util;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.repository.ServiceRequestRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.UserDetailResponse;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;

@Component
public class UserUtil {


    private final ObjectMapper mapper;

    private final ServiceRequestRepository serviceRequestRepository;

    private final RequestServiceConfiguration config;

    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    private static final String PWD_EXPIRY_DATE = "pwdExpiryDate";

    @Autowired
    public UserUtil(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository,
                    RequestServiceConfiguration config) {
        this.mapper = mapper;
        this.serviceRequestRepository = serviceRequestRepository;
        this.config = config;
    }

    /**
     * Returns UserDetailResponse by calling user service with given uri and object
     * @param userRequest Request object for user service
     * @param uri The address of the endpoint
     * @return Response from user service as parsed as userDetailResponse
     */

    public UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
        String dobFormat = null;
        if(uri.toString().contains(config.getUserSearchEndpoint())  || uri.toString().contains(config.getUserUpdateEndpoint()))
            dobFormat="yyyy-MM-dd";
        else if(uri.toString().contains(config.getUserCreateEndpoint()))
            dobFormat = "dd/MM/yyyy";
        try{
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)serviceRequestRepository.fetchResult(uri, userRequest);
            parseResponse(responseMap,dobFormat);
            return mapper.convertValue(responseMap,UserDetailResponse.class);
        }
        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convertValue in userCall");
        }
    }


    /**
     * Parses date formats to long for all users in responseMap
     * @param responeMap LinkedHashMap got from user api response
     * @param dobFormat dob format used to parse the date of birth value
     */

    public void parseResponse(Map<String, Object> responeMap, String dobFormat){
        List<Map<String, Object>> users = (List<Map<String, Object>>)responeMap.get("user");
        String format1 = "dd-MM-yyyy HH:mm:ss";
        if(users!=null){
            users.forEach( map -> {
                        map.put("createdDate",dateTolong((String)map.get("createdDate"),format1));
                        if((String)map.get(LAST_MODIFIED_DATE)!=null)
                            map.put(LAST_MODIFIED_DATE,dateTolong((String)map.get(LAST_MODIFIED_DATE),format1));
                        if((String)map.get("dob")!=null)
                            map.put("dob",dateTolong((String)map.get("dob"),dobFormat));
                        if((String)map.get(PWD_EXPIRY_DATE)!=null)
                            map.put(PWD_EXPIRY_DATE,dateTolong((String)map.get(PWD_EXPIRY_DATE),format1));
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try {
            if (format.contains("H") || format.contains("m") || format.contains("s")) {
                return LocalDateTime.parse(date, formatter).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
            return LocalDate.parse(date, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } catch (DateTimeParseException e) {
            throw new CustomException("INVALID_DATE_FORMAT","Failed to parse date format in user");
        }
    }

    /**
     * enriches the userInfo with statelevel tenantId and other fields
     * @param mobileNumber
     * @param userInfo
     */
    public void addUserDefaultFields(String mobileNumber, User userInfo){
        Role role = getCitizenRole();
        userInfo.setRoles(Collections.singletonList(role));
        userInfo.setType("CITIZEN");
        userInfo.setUserName(mobileNumber);
    }

    /**
     * Returns role object for citizen
     * @return
     */
    private Role getCitizenRole(){
        Role role = new Role();
        role.setName("Citizen");
        return role;
    }

    public static String getStateLevelTenant(String tenantId){
        return tenantId.split("\\.")[0];
    }

    /**
     * Checks whether the logged-in user is the same as the applicant in the given application request.
     * <p>
     * This is done by comparing the mobile number from the user info in the request with
     * the mobile number provided in the applicant details of the application.
     *
     * @param waterTankerRequest The application request containing user and applicant details.
     * @return true if the mobile numbers match (case-insensitive), false otherwise.
     */
    public static boolean isCurrentUserApplicant(WaterTankerBookingRequest waterTankerRequest){
        String userMobileNumber = waterTankerRequest.getRequestInfo().getUserInfo().getMobileNumber();
        String applicationMobileNumber = waterTankerRequest.getWaterTankerBookingDetail().getApplicantDetail().getMobileNumber();
        return userMobileNumber.equals(applicationMobileNumber);
    }

    /**
     * Checks whether the logged-in user is the same as the applicant in the given application request.
     * <p>
     * This is done by comparing the mobile number from the user info in the request with
     * the mobile number provided in the applicant details of the application.
     *
     * @param mobileToiletRequest The application request containing user and applicant details.
     * @return true if the mobile numbers match (case-insensitive), false otherwise.
     */
    public static boolean isCurrentUserApplicant(MobileToiletBookingRequest mobileToiletRequest){
        String userMobileNumber = mobileToiletRequest.getRequestInfo().getUserInfo().getMobileNumber();
        String applicationMobileNumber = mobileToiletRequest.getMobileToiletBookingDetail().getApplicantDetail().getMobileNumber();
        return userMobileNumber.equals(applicationMobileNumber);
    }
}