package org.egov.ptr.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.util.NotificationUtil;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.web.contracts.PetRequest;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.event.*;
import org.egov.ptr.models.event.EventRequest;
import org.egov.ptr.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PTRNotificationService {
	@Autowired
	private PetConfiguration config;
	@Autowired
	private NotificationUtil util;
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	public void process(PetRegistrationRequest request) {
//		RequestInfo requestInfo = request.getRequestInfo();
//		Map<String, String> mobileNumberToOwner = new HashMap<>();
//		String tenantId = request.getPetApplication().getTenantId();
////		String action = request.getLicenses().get(0).getAction;
//		Map<Object, Object> configuredChannelList = new HashMap<>();
////		List<String> configuredChannelNames = Arrays.asList(new String[]{"SMS","EVENT","EMAIL"});
//		Set<String> mobileNumbers = new HashSet<>();


//				List<SMSRequest> smsRequestsTL = new LinkedList<>();
//				TradeLicense license = request.getLicenses().get(0);
//				String ACTION_STATUS = license.getAction() + "_" + license.getStatus();
//				if (request.getLicenses().get(0).getTradeLicenseDetail().getAdditionalDetail().get(PROPERTY_ID) != null)
//					propertyId = request.getLicenses().get(0).getTradeLicenseDetail().getAdditionalDetail().get(PROPERTY_ID).asText();
//				Property property = propertyUtil.getPropertyDetails(request.getLicenses().get(0), propertyId, requestInfo);
//				String source = property.getSource().name();

//				if (config.getIsTLSMSEnabled()) {
//					if (!propertyId.isEmpty() && ACTION_STATUS_INITIATED.equalsIgnoreCase(ACTION_STATUS)) {
//						List<SMSRequest> smsRequestsPT = new ArrayList<>();
//						String localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
//						String message = propertyUtil.getPropertySearchMsg(license, localizationMessages, CHANNEL_NAME_SMS, propertyId, source);
//						log.info("Message to be sent: ", message);
//						smsRequestsPT.addAll(propertyUtil.createPropertySMSRequest(message, property));
//						if (!CollectionUtils.isEmpty(smsRequestsPT))
//							util.sendSMS(smsRequestsPT, true);
//
//					}
//					enrichSMSRequest(request, smsRequestsTL, configuredChannelList);
//					if (!CollectionUtils.isEmpty(smsRequestsTL))
//						util.sendSMS(smsRequestsTL, true);
//				}

				
					
					EventRequest eventRequest = getEventsForPTR(request);
					if (null != eventRequest)
						util.sendEventNotification(eventRequest);
				

				
	}

	private EventRequest getEventsForPTR(PetRegistrationRequest request) {

    	List<Event> events = new ArrayList<>();
        String tenantId = request.getPetRegistrationApplications().get(0).getTenantId();
		String localizationMessages = util.getLocalizationMessages(tenantId,request.getRequestInfo());
		List<String> toUsers = new ArrayList<>();
		String mobileNumber = request.getPetRegistrationApplications().get(0).getMobileNumber();

	        Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(),tenantId);

	        if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
	            log.info("UUID search failed!");
	        }

	        
	        toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
			String message = null;
			message = util.getCustomizedMsg(request.getRequestInfo(), request.getPetRegistrationApplications().get(0), localizationMessages);
			 Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
			
				events.add(Event.builder().tenantId(tenantId).description("Pet Registration Description")
						.eventType(PTRConstants.USREVENTS_EVENT_TYPE).name(PTRConstants.USREVENTS_EVENT_NAME)
						.postedBy(PTRConstants.USREVENTS_EVENT_POSTEDBY).source(Source.WEBAPP).recepient(recepient)
						.eventDetails(null).actions(null).build());
    			
    		
        
        if(!CollectionUtils.isEmpty(events)) {
    		return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
        }else {
        	return null;
        }
		
    
	}
	
	  /**
     * Fetches UUIDs of CITIZEN based on the phone number.
     *
     * @param mobileNumber - Mobile Numbers
     * @param requestInfo - Request Information
     * @param tenantId - Tenant Id
     * @return Returns List of MobileNumbers and UUIDs
     */
    public Map<String, String> fetchUserUUIDs(String mobileNumber, RequestInfo requestInfo, String tenantId) {
        Map<String, String> mapOfPhoneNoAndUUIDs = new HashMap<>();
        StringBuilder uri = new StringBuilder();
        uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> userSearchRequest = new HashMap<>();
        userSearchRequest.put("RequestInfo", requestInfo);
        userSearchRequest.put("tenantId", tenantId);
        userSearchRequest.put("userType", "CITIZEN");
        userSearchRequest.put("userName", mobileNumber);
        try {
            Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
            if(null != user) {
                String uuid = JsonPath.read(user, "$.user[0].uuid");
                mapOfPhoneNoAndUUIDs.put(mobileNumber, uuid);
            }else {
                log.error("Service returned null while fetching user for username - "+mobileNumber);
            }
        }catch(Exception e) {
            log.error("Exception while fetching user for username - "+mobileNumber);
            log.error("Exception trace: ",e);
        }

        return mapOfPhoneNoAndUUIDs;
    }
	
}
