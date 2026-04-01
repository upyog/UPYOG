package org.egov.ptr.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.util.NotificationUtil;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.web.contracts.EmailRequest;
import org.egov.ptr.web.contracts.SMSRequest;
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
		String localizationMessages = util.getLocalizationMessages(request.getPetRegistrationApplications().get(0).getTenantId(), request.getRequestInfo());

		
		if (config.getIsSMSNotificationEnabled()) {
			log.info("Notification is enabled. Processing event notification for Pet Registration.");
			sendMessageNotification(localizationMessages, request);
		} else {
			log.info("Notification is disabled. Skipping event notification for Pet Registration.");
		}
		
		
		if (config.getIsEmailNotificationEnabled()) {
			log.info("Email Notification is enabled. Processing email notification for Pet Registration.");
			// Call method to send email notification
			 sendEmailNotification(localizationMessages, request);
		} else {
			log.info("Email Notification is disabled. Skipping email notification for Pet Registration.");
		}
		if(config.getIsUserEventsNotificationEnabled()) {
		EventRequest eventRequest = getEventsForPTR(request);
		log.info("Event Request in Pet process method" + eventRequest.toString());
		if (null != eventRequest)
			util.sendEventNotification(eventRequest);

	}
	}

	
	

	private void sendMessageNotification(String localizationMessages, PetRegistrationRequest request) {
	    
	    String message = null;
	    
	    try {
	        message = util.getCustomizedMsg(
	            request.getRequestInfo(), 
	            request.getPetRegistrationApplications().get(0), 
	            localizationMessages
	        );
	        
	    } catch (Exception e) {
	        log.error("Exception occurred while fetching localization message", e);
	    }

	    log.info("Message extracted for SMS: {}", message);

	    // Now this block is reachable
	    if (message != null && !message.trim().isEmpty()) {
	        if (config.getIsSMSNotificationEnabled()) {
	            List<SMSRequest> smsRequests = new LinkedList<>();
	            
	            String mobile = request.getPetRegistrationApplications().get(0).getOwner().getMobileNumber();
	            String name = request.getPetRegistrationApplications().get(0).getOwner().getName();
	            
	            Map<String, String> mobileNumberToOwner = new HashMap<>();
	            mobileNumberToOwner.put(mobile, name);
	            
	            util.enrichSMSRequest(request, smsRequests, mobileNumberToOwner, message);
	            
	            if (!CollectionUtils.isEmpty(smsRequests)) {
	                log.info("Sending SMS to {}", mobile);
	                util.sendSMS(smsRequests);
	            }
	        }
	    } else {
	        log.warn("Notification skipped: Localized message was null.");
	    }
	}
	
	
	
	private void sendEmailNotification(String localizationMessages, PetRegistrationRequest request) {
	    String message = null;

	    try {
	        // Safe check for nested applications
	        if (request.getPetRegistrationApplications() == null || request.getPetRegistrationApplications().isEmpty()) {
	            log.warn("No applications found. Skipping notification.");
	            return;
	        }

	        // Get the localized message
	        message = util.getCustomizedMailMsg(
	            request.getRequestInfo(), 
	            request.getPetRegistrationApplications().get(0), 
	            localizationMessages
	        );
	    } catch (Exception e) {
	        log.error("Exception occurred while fetching localization message", e);
	    }

	    // Process only if message was successfully retrieved
	    if (message != null && !message.trim().isEmpty()) {
	        if (config.getIsEmailNotificationEnabled()) {
	            
	            // 1. Initialize the correct list type: List<EmailRequest>
	            List<EmailRequest> emailRequests = new ArrayList<>();
	            
	            // 2. Extract owner info
	         // Replace 'PetRegistrationApplication' with the actual Class name in your project
	            PetRegistrationApplication application = request.getPetRegistrationApplications().get(0);	            String mail = application.getOwner().getEmailId();
	            String name = application.getOwner().getName();
	           

	            // 2. If email is missing, fetch from User Service
	            if (mail == null || mail.trim().isEmpty()) {
	                Map<String, String> userResponse = fetchMailOwner(application.getOwner().getUuid(), request.getRequestInfo(), application.getTenantId());
	                
	                if (userResponse != null && !userResponse.isEmpty()) {
	                    // Update the local variables with fetched data
	                    mail = userResponse.get("email");
	                    // Update name as well if it was missing or to ensure consistency
	                    if (userResponse.containsKey("name")) {
	                        name = userResponse.get("name");
	                        
	                    }
	                    log.info("Mail successfully fetched using UUID for {}: {}", name, mail);
	                } else {
	                    log.warn("Could not find email in User Service for UUID: {}", application.getOwner().getUuid());
	                }
	            }

	            // 3. Create the notification map
	            Map<String, String> emailToOwner = new HashMap<>();
	            if (mail != null && !mail.trim().isEmpty()) {
	            	message = message.replace("{ownerName}", name != null ? name : "Citizen");
	                // Key: Email, Value: Name (Standard for most Notification Producers)
	                emailToOwner.put( name ,mail);
	            }
	            
	            // 3. Enrich the list (This now matches the utility method signature)
	            
	            util.enrichEmailRequest(request, emailRequests, emailToOwner, message, request.getPetRegistrationApplications().get(0).getApplicationNumber());
	            
	            // 4. Send the list
	            if (!emailRequests.isEmpty()) {
	                log.info("Sending notification to {}", mail);
	                util.sendEmail(emailRequests); 
	            }
	        }
	    } else {
	        log.warn("Notification skipped: Localized message was null or empty.");
	    }
	}
	
	
	
	
	private EventRequest getEventsForPTR(PetRegistrationRequest request) {

		List<Event> events = new ArrayList<>();
		String tenantId = request.getPetRegistrationApplications().get(0).getTenantId();
		String localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
		List<String> toUsers = new ArrayList<>();
		String mobileNumber = request.getPetRegistrationApplications().get(0).getOwner().getMobileNumber();

		Map<String, String> mapOfPhoneNoAndUUIDs = fetchUserUUIDs(mobileNumber, request.getRequestInfo(), tenantId);

		if (CollectionUtils.isEmpty(mapOfPhoneNoAndUUIDs.keySet())) {
			log.info("UUID search failed!");
		}

		toUsers.add(mapOfPhoneNoAndUUIDs.get(mobileNumber));
		String message = null;
		message = util.getCustomizedMsg(request.getRequestInfo(), request.getPetRegistrationApplications().get(0),
				localizationMessages);
		log.info("Message for event in Pet:" + message);
		Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
		log.info("Recipient object in pet:" + recepient.toString());
		events.add(Event.builder().tenantId(tenantId).description(message).eventType(PTRConstants.USREVENTS_EVENT_TYPE)
				.name(PTRConstants.USREVENTS_EVENT_NAME).postedBy(PTRConstants.USREVENTS_EVENT_POSTEDBY)
				.source(Source.WEBAPP).recepient(recepient).eventDetails(null).actions(null).build());

		if (!CollectionUtils.isEmpty(events)) {
			return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
		} else {
			return null;
		}

	}

	/**
	 * Fetches UUIDs of CITIZEN based on the phone number.
	 *
	 * @param mobileNumber - Mobile Numbers
	 * @param requestInfo  - Request Information
	 * @param tenantId     - Tenant Id
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
			log.info("User fetched in fetUserUUID method of pet notfication consumer" + user.toString());
//			if (null != user) {
//				String uuid = JsonPath.read(user, "$.user[0].uuid");
			if (user instanceof Optional) {
				Optional<Object> optionalUser = (Optional<Object>) user;
				if (optionalUser.isPresent()) {
					List<String> uuids = JsonPath.read(optionalUser.get(), "$.user[*].uuid");
					if (!uuids.isEmpty()) {
						mapOfPhoneNoAndUUIDs.put(mobileNumber, uuids.get(0));
					} else {
						log.warn("No user found for mobile number: " + mobileNumber);
					}
				} else {
					log.error("Service returned empty Optional while fetching user for username - " + mobileNumber);
				}
			} else {
				log.error("Service returned null while fetching user for username - " + mobileNumber);
			}
		} catch (Exception e) {
			log.error("Exception while fetching user for username - " + mobileNumber);
			log.error("Exception trace: ", e);
		}

		return mapOfPhoneNoAndUUIDs;
	}
	
	
	public Map<String, String> fetchMailOwner(String uuid, RequestInfo requestInfo, String tenantId) {
	    Map<String, String> userData = new HashMap<>();
	    StringBuilder uri = new StringBuilder();
	    uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());

	    // Prepare User Search Request
	    Map<String, Object> userSearchRequest = new HashMap<>();
	    userSearchRequest.put("RequestInfo", requestInfo);
	    userSearchRequest.put("tenantId","pb" );
	    userSearchRequest.put("userType", "CITIZEN");
	    userSearchRequest.put("uuid", Collections.singletonList(uuid)); // User service usually expects a list for UUIDs

	    try {
	        Object response = serviceRequestRepository.fetchResult(uri, userSearchRequest);
	        
	        if (response != null) {
	            // Read Name and Email using JsonPath
	            List<String> names = JsonPath.read(response, "$.user[*].name");
	            List<String> emails = JsonPath.read(response, "$.user[*].emailId");

	            if (!names.isEmpty() && !emails.isEmpty()) {
	                userData.put("name", names.get(0));
	                userData.put("email", emails.get(0));
	                log.info("Fetched user: {} with email: {}", names.get(0), emails.get(0));
	            } else {
	                log.warn("User details missing in response for UUID: {}", uuid);
	            }
	        }
	    } catch (Exception e) {
	        log.error("Exception while fetching user details for UUID: {}", uuid, e);
	    }

	    return userData;
	}

}
