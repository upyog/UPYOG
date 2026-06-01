package org.egov.garbagecollection.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.egov.garbagecollection.config.GCConfiguration;
import org.egov.garbagecollection.constants.GCConstants;
import org.egov.garbagecollection.repository.ServiceRequestRepository;
import org.egov.garbagecollection.repository.GcDao;
import org.egov.garbagecollection.util.NotificationUtil;
import org.egov.garbagecollection.util.GcServicesUtil;
import org.egov.garbagecollection.validator.ValidateProperty;
import org.egov.garbagecollection.web.models.*;
import org.egov.garbagecollection.web.models.collection.PaymentDetail;
import org.egov.garbagecollection.web.models.collection.PaymentRequest;
import org.egov.garbagecollection.web.models.users.UserDetailResponse;
import org.egov.garbagecollection.web.models.workflow.ProcessInstance;
import org.egov.garbagecollection.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.egov.garbagecollection.constants.GCConstants.*;
import static org.egov.garbagecollection.constants.GCConstants.PENDING_FOR_PAYMENT_STATUS_CODE;

@Slf4j
@Service
public class PaymentUpdateService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private GCConfiguration config;

	@Autowired
	private GcServiceImpl gcService;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private GcDao repo;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ValidateProperty validateProperty;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private NotificationUtil notificationUtil;

	@Autowired
	private WorkflowNotificationService workflowNotificationService;

	@Autowired
	private GcServicesUtil gcServiceUtil;

	@Autowired
	private CalculationService calculationService;
	/**
	 * After payment change the application status
	 *
	 * @param record
	 *            payment request
	 */
	public void process(HashMap<String, Object> record) {
		try {
			PaymentRequest paymentRequest = mapper.convertValue(record, PaymentRequest.class);
			boolean isServiceMatched = false;
			for (PaymentDetail paymentDetail : paymentRequest.getPayment().getPaymentDetails()) {
				if (GCConstants.GARBAGE_SERVICE_BUSINESS_ID.equals(paymentDetail.getBusinessService()) ||
            paymentDetail.getBusinessService().equalsIgnoreCase(config.getReceiptBusinessservice()) || paymentDetail.getBusinessService().equalsIgnoreCase(config.getReconnectBusinessServiceName())) {
					isServiceMatched = true;
				}
			}
			if (!isServiceMatched)
				return;
			paymentRequest.getRequestInfo().setUserInfo(fetchUser(
					paymentRequest.getRequestInfo().getUserInfo().getUuid(), paymentRequest.getRequestInfo()));
			for (PaymentDetail paymentDetail : paymentRequest.getPayment().getPaymentDetails()) {
				log.info("Consuming Business Service : {}" , paymentDetail.getBusinessService());
				SearchCriteria criteria = new SearchCriteria();
				if (paymentDetail.getBusinessService().equalsIgnoreCase(config.getReceiptDisconnectionBusinessservice())) {
					criteria = SearchCriteria.builder()
							.tenantId(paymentRequest.getPayment().getTenantId())
							.connectionNumber(Stream.of(paymentDetail.getBill().getConsumerCode().toString()).collect(Collectors.toSet()))
							.applicationStatus(Collections.singleton(PENDING_FOR_PAYMENT_STATUS_CODE)).build();
				}
				if (paymentDetail.getBusinessService().equalsIgnoreCase(config.getReceiptReconnectionBusinessservice()) || paymentDetail.getBusinessService().equalsIgnoreCase(config.getReceiptBusinessservice())) {
					criteria = SearchCriteria.builder()
							.tenantId(paymentRequest.getPayment().getTenantId())
							.applicationNumber(Stream.of(paymentDetail.getBill().getConsumerCode().toString()).collect(Collectors.toSet())).build();
				}
				criteria.setIsInternalCall(Boolean.TRUE);
				List<GarbageConnection> garbageConnections = gcService.search(criteria,
							paymentRequest.getRequestInfo());
					if (CollectionUtils.isEmpty(garbageConnections)) {
						throw new CustomException("INVALID_RECEIPT",
								"No garbageConnection found for the consumerCode " + criteria.getApplicationNumber());
					}
					Optional<GarbageConnection> connections = garbageConnections.stream().findFirst();
					GarbageConnection connection = connections.get();
					if (garbageConnections.size() > 1) {
						throw new CustomException("INVALID_RECEIPT",
								"More than one application found on consumerCode " + criteria.getApplicationNumber());
					}
					garbageConnections.forEach(waterConnection -> waterConnection.getProcessInstance().setAction((GCConstants.ACTION_PAY)));
					GarbageConnectionRequest garbageConnectionRequest = GarbageConnectionRequest.builder()
							.garbageConnection(connection).requestInfo(paymentRequest.getRequestInfo())
							.build();
					try {
						log.info("GarbageConnection Request " + mapper.writeValueAsString(garbageConnectionRequest));
					} catch (Exception ex) {
						log.error("Temp Catch Excption:", ex);
					}

					Property property = validateProperty.getOrValidateProperty(garbageConnectionRequest);

					// Enrich tenantId in userInfo for workflow call
					RequestInfo requestInfo = garbageConnectionRequest.getRequestInfo();
					Role role = Role.builder().code("SYSTEM_PAYMENT").tenantId(property.getTenantId()).build();
					requestInfo.getUserInfo().getRoles().add(role);
					if(paymentDetail.getBusinessService().equalsIgnoreCase(config.getReconnectBusinessServiceName()))
						garbageConnectionRequest.setReconnectRequest(true);
					wfIntegrator.callWorkFlow(garbageConnectionRequest, property);
					// For trimmed NewGC workflow: PAY → CONNECTION_ACTIVATED directly.
					// postStatusEnrichment generates the connection number and sets execution date.
					enrichmentService.postStatusEnrichment(garbageConnectionRequest);
					// Generate the previous month recurring GC demand (on connectionNo)
					// only for new connections — this is the GC charge, separate from the one-time fee
					if (GCConstants.NEW_GARBAGE_CONNECTION.equalsIgnoreCase(
							garbageConnectionRequest.getGarbageConnection().getApplicationType())) {
						calculationService.generatePreviousMonthConnectionDemand(garbageConnectionRequest, property);
					}
					enrichmentService.enrichFileStoreIds(garbageConnectionRequest);
					repo.updateGarbageConnection(garbageConnectionRequest, false);
				}
			sendNotificationForPayment(paymentRequest);
		} catch (Exception ex) {
			log.error("Failed to process payment topic message. Exception: ", ex);
		}
	}

	/**
	 *
	 * @param uuid
	 * @param requestInfo
	 * @return User
	 */
	private User fetchUser(String uuid, RequestInfo requestInfo) {
		StringBuilder uri = new StringBuilder();
		uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
		Map<String, Object> userSearchRequest = new HashMap<>();
		List<String> uuids = Arrays.asList(uuid);
		userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("uuid", uuids);
		Object response = serviceRequestRepository.fetchResult(uri, userSearchRequest);
		List<Object> users = new ArrayList<>();
		try {
			log.info("user info response" + mapper.writeValueAsString(response));
			DocumentContext context = JsonPath.parse(mapper.writeValueAsString(response));
			users = context.read("$.user");
		} catch (JsonProcessingException e) {
			log.error("error occured while parsing user info", e);
		}
		if (CollectionUtils.isEmpty(users)) {
			throw new CustomException("INVALID_SEARCH_ON_USER",
					"No user found on given criteria!!!");
		}
		return mapper.convertValue(users.get(0), User.class);
	}

	/**
	 *
	 * @param paymentRequest
	 */
	public void sendNotificationForPayment(PaymentRequest paymentRequest) {
		try {
			log.info("Payment Notification consumer :");
			boolean isServiceMatched = false;
			for (PaymentDetail paymentDetail : paymentRequest.getPayment().getPaymentDetails()) {
				String businessservice = paymentDetail.getBusinessService();
				if (GCConstants.GARBAGE_SERVICE_BUSINESS_ID.equals(businessservice) || WATER_SERVICE_ONE_TIME_FEE_BUSINESS_ID.equals(businessservice)) {
					isServiceMatched = true;
				}
			}
			if (!isServiceMatched)
				return;
			for (PaymentDetail paymentDetail : paymentRequest.getPayment().getPaymentDetails()) {
				log.info("Consuming Business Service : {}", paymentDetail.getBusinessService());
				if (GCConstants.GARBAGE_SERVICE_BUSINESS_ID.equals(paymentDetail.getBusinessService()) ||
						config.getReceiptBusinessservice().equals(paymentDetail.getBusinessService())) {
					SearchCriteria criteria = new SearchCriteria();
					if (GCConstants.GARBAGE_SERVICE_BUSINESS_ID.equals(paymentDetail.getBusinessService())) {
						criteria = SearchCriteria.builder()
								.tenantId(paymentRequest.getPayment().getTenantId())
								.connectionNumber(Stream.of(paymentDetail.getBill().getConsumerCode().toString()).collect(Collectors.toSet())).build();
					} else {
						criteria = SearchCriteria.builder()
								.tenantId(paymentRequest.getPayment().getTenantId())
								.applicationNumber(Stream.of(paymentDetail.getBill().getConsumerCode().toString()).collect(Collectors.toSet())).build();
					}
					criteria.setIsInternalCall(Boolean.TRUE);
					List<GarbageConnection> waterConnections = gcService.search(criteria,
							paymentRequest.getRequestInfo());
					if (CollectionUtils.isEmpty(waterConnections)) {
						throw new CustomException("INVALID_RECEIPT",
								"No garbageConnection found for the consumerCode " + paymentDetail.getBill().getConsumerCode());
					}
					Collections.sort(waterConnections, Comparator.comparing(wc -> wc.getAuditDetails().getLastModifiedTime()));
					long count = waterConnections.stream().count();
					Optional<GarbageConnection> connections = Optional.of(waterConnections.stream().skip(count - 1).findFirst().get());
					GarbageConnectionRequest garbageConnectionRequest = GarbageConnectionRequest.builder()
							.garbageConnection(connections.get()).requestInfo(paymentRequest.getRequestInfo())
							.build();
					if(!(garbageConnectionRequest.getGarbageConnection().getApplicationType().equalsIgnoreCase(GCConstants.GARBAGE_RECONNECTION)) && !(garbageConnectionRequest.getGarbageConnection().getApplicationType().equalsIgnoreCase(GCConstants.DISCONNECT_GARBAGE_CONNECTION)) )
					sendPaymentNotification(garbageConnectionRequest, paymentDetail);
				}
			}
		} catch (Exception ex) {
			log.error("Failed to process payment topic message. Exception: ", ex);
		}
	}

	/**
	 *
	 * @param garbageConnectionRequest
	 */
	public void sendPaymentNotification(GarbageConnectionRequest garbageConnectionRequest, PaymentDetail paymentDetail) {
		User userInfoCopy = garbageConnectionRequest.getRequestInfo().getUserInfo();
		User userInfo = notificationUtil.getInternalMicroserviceUser(garbageConnectionRequest.getGarbageConnection().getTenantId());
		garbageConnectionRequest.getRequestInfo().setUserInfo(userInfo);

		Property property = validateProperty.getOrValidateProperty(garbageConnectionRequest);

		garbageConnectionRequest.getRequestInfo().setUserInfo(userInfoCopy);
		List<String> configuredChannelNames =  notificationUtil.fetchChannelList(garbageConnectionRequest.getRequestInfo(), garbageConnectionRequest.getGarbageConnection().getTenantId(), GARBAGE_SERVICE_BUSINESS_ID, garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction());

		if(configuredChannelNames.contains(CHANNEL_NAME_EVENT)) {
			if (config.getIsUserEventsNotificationEnabled() != null && config.getIsUserEventsNotificationEnabled()) {
				EventRequest eventRequest = getEventRequest(garbageConnectionRequest, property, paymentDetail);
				if (eventRequest != null) {
					notificationUtil.sendEventNotification(eventRequest);
				}
			}
		}
		if(configuredChannelNames.contains(CHANNEL_NAME_SMS)) {
			if (config.getIsSMSEnabled() != null && config.getIsSMSEnabled()) {
				List<SMSRequest> smsRequests = getSmsRequest(garbageConnectionRequest, property, paymentDetail);
				if (!CollectionUtils.isEmpty(smsRequests)) {
					notificationUtil.sendSMS(smsRequests);
				}
			}
		}

		if(configuredChannelNames.contains(CHANNEL_NAME_EMAIL)) {
			if (config.getIsEmailNotificationEnabled() != null && config.getIsEmailNotificationEnabled()) {
				List<EmailRequest> emailRequests = getEmailRequest(garbageConnectionRequest, property, paymentDetail);
				if (!CollectionUtils.isEmpty(emailRequests)) {
					notificationUtil.sendEmail(emailRequests);
				}
			}
		}
	}
	/**
	 *
	 * @param request
	 * @param property
	 * @return
	 */
	private EventRequest getEventRequest(GarbageConnectionRequest request, Property property, PaymentDetail paymentDetail) {

		if(paymentDetail.getTotalAmountPaid().intValue() == 0)
			return null;

		String localizationMessage = notificationUtil
				.getLocalizationMessages(property.getTenantId(), request.getRequestInfo());

		String applicationStatus = request.getGarbageConnection().getApplicationStatus();
		String notificationTemplate = GCConstants.PAYMENT_NOTIFICATION_APP;
		ProcessInstance workflow = request.getGarbageConnection().getProcessInstance();
		StringBuilder builder = new StringBuilder();
		int reqType;

		//Condition to assign Disconnection application Payment Notification code
		if ((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION)) &&
				(!workflow.getAction().equalsIgnoreCase(APPROVE_CONNECTION)) && gcServiceUtil.isDisconnectConnectionRequest(request)) {
			reqType = DISCONNECT_CONNECTION;
			notificationTemplate = notificationUtil.getCustomizedMsgForInAppForPayment(workflow.getAction(), applicationStatus, reqType);
		}

		String message = notificationUtil.getMessageTemplate(notificationTemplate, localizationMessage);

		if (message == null) {
			log.info("No message template found for, {} " + GCConstants.PAYMENT_NOTIFICATION_APP);
			return null;
		}
		Map<String, String> mobileNumbersAndNames = new HashMap<>();
		Map<String, String> mapOfPhnoAndUUIDs = new HashMap<>();

		Set<String> ownersMobileNumbers = new HashSet<>();

		property.getOwners().forEach(owner -> {
			if (owner.getMobileNumber() != null)
				ownersMobileNumbers.add(owner.getMobileNumber());
		});
		//send the notification to the connection holders
		if (!CollectionUtils.isEmpty(request.getGarbageConnection().getConnectionHolders())) {
			request.getGarbageConnection().getConnectionHolders().forEach(holder -> {
				if (!StringUtils.isEmpty(holder.getMobileNumber())) {
					ownersMobileNumbers.add(holder.getMobileNumber());
				}
			});
		}

		for(String mobileNumber:ownersMobileNumbers) {
			UserDetailResponse userDetailResponse = workflowNotificationService.fetchUserByUsername(mobileNumber, request.getRequestInfo(), request.getGarbageConnection().getTenantId());
			if(!CollectionUtils.isEmpty(userDetailResponse.getUser()))
			{
				OwnerInfo user = userDetailResponse.getUser().get(0);
				mobileNumbersAndNames.put(user.getMobileNumber(),user.getName());
				mapOfPhnoAndUUIDs.put(user.getMobileNumber(),user.getUuid());
			}
			else
			{	log.info("No User for mobile {} skipping event", mobileNumber);}

		}

		//Send the notification to applicant
		if(!org.apache.commons.lang.StringUtils.isEmpty(request.getRequestInfo().getUserInfo().getMobileNumber()))
		{
			mobileNumbersAndNames.put(request.getRequestInfo().getUserInfo().getMobileNumber(), request.getRequestInfo().getUserInfo().getName());
			mapOfPhnoAndUUIDs.put(request.getRequestInfo().getUserInfo().getMobileNumber(), request.getRequestInfo().getUserInfo().getUuid());
		}
		Map<String, String> getReplacedMessage = workflowNotificationService.getMessageForMobileNumber(mobileNumbersAndNames, request,
				message, property);
		Map<String, String> mobileNumberAndMesssage = replacePaymentInfo(getReplacedMessage, paymentDetail);
		Set<String> mobileNumbers = mobileNumberAndMesssage.keySet().stream().collect(Collectors.toSet());
		if (CollectionUtils.isEmpty(mapOfPhnoAndUUIDs.keySet())) {
			log.info("UUID search failed!");
		}
		List<Event> events = new ArrayList<>();
		for (String mobile : mobileNumbers) {
			if (null == mapOfPhnoAndUUIDs.get(mobile) || null == mobileNumberAndMesssage.get(mobile)) {
				log.error("No UUID/SMS for mobile {} skipping event", mobile);
				continue;
			}
			List<String> toUsers = new ArrayList<>();
			toUsers.add(mapOfPhnoAndUUIDs.get(mobile));
			Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(null).build();
			Action action = workflowNotificationService.getActionForEventNotification(mobileNumberAndMesssage, mobile, request, property);
			events.add(Event.builder().tenantId(property.getTenantId())
					.description(mobileNumberAndMesssage.get(mobile)).eventType(GCConstants.USREVENTS_EVENT_TYPE)
					.name(GCConstants.USREVENTS_EVENT_NAME).postedBy(GCConstants.USREVENTS_EVENT_POSTEDBY)
					.source(Source.WEBAPP).recepient(recepient).eventDetails(null).actions(action).build());
		}
		if (!CollectionUtils.isEmpty(events)) {
			return EventRequest.builder().requestInfo(request.getRequestInfo()).events(events).build();
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param garbageConnectionRequest
	 * @param property
	 * @return
	 */
	private List<SMSRequest> getSmsRequest(GarbageConnectionRequest garbageConnectionRequest,
										   Property property, PaymentDetail paymentDetail) {

		if(paymentDetail.getTotalAmountPaid().intValue() == 0)
			return null;

		String localizationMessage = notificationUtil.getLocalizationMessages(property.getTenantId(),
				garbageConnectionRequest.getRequestInfo());

		String applicationStatus = garbageConnectionRequest.getGarbageConnection().getApplicationStatus();
		String notificationTemplate = GCConstants.PAYMENT_NOTIFICATION_SMS;
		ProcessInstance workflow = garbageConnectionRequest.getGarbageConnection().getProcessInstance();
		StringBuilder builder = new StringBuilder();
		int reqType;

		//Condition to assign Disconnection application Payment Notification code
		if ((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION)) &&
				(!workflow.getAction().equalsIgnoreCase(APPROVE_CONNECTION)) && gcServiceUtil.isDisconnectConnectionRequest(garbageConnectionRequest)) {
			reqType = DISCONNECT_CONNECTION;
			notificationTemplate = notificationUtil.getCustomizedMsgForSMSForPayment(workflow.getAction(), applicationStatus, reqType);
		}

		String message = notificationUtil.getMessageTemplate(notificationTemplate, localizationMessage);

		if (message == null) {
			log.info("No message template found for, {} " + GCConstants.PAYMENT_NOTIFICATION_SMS);
			return Collections.emptyList();
		}
		Map<String, String> mobileNumbersAndNames = new HashMap<>();
		property.getOwners().forEach(owner -> {
			if (owner.getMobileNumber() != null)
				mobileNumbersAndNames.put(owner.getMobileNumber(), owner.getName());
		});
		//send the notification to the connection holders
		if (!CollectionUtils.isEmpty(garbageConnectionRequest.getGarbageConnection().getConnectionHolders())) {
			garbageConnectionRequest.getGarbageConnection().getConnectionHolders().forEach(holder -> {
				if (!StringUtils.isEmpty(holder.getMobileNumber())) {
					mobileNumbersAndNames.put(holder.getMobileNumber(), holder.getName());
				}
			});
		}

		//Send the notification to applicant
		
		/*Payemnt SMS is coming to Employee also So just commenting it -- Abhishek (09-12-2024)*/ 
		
//		if(!org.apache.commons.lang.StringUtils.isEmpty(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber()))
//		{
//			mobileNumbersAndNames.put(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber(), garbageConnectionRequest.getRequestInfo().getUserInfo().getName());
//		}

		Map<String, String> getReplacedMessage = workflowNotificationService.getMessageForMobileNumber(mobileNumbersAndNames,
				garbageConnectionRequest, message, property);
		Map<String, String> mobileNumberAndMessage = replacePaymentInfo(getReplacedMessage, paymentDetail);
		List<SMSRequest> smsRequest = new ArrayList<>();
		mobileNumberAndMessage.forEach((mobileNumber, msg) -> {
			SMSRequest req = SMSRequest.builder().mobileNumber(mobileNumber).message(msg).category(Category.TRANSACTION).build();
			smsRequest.add(req);
		});
		return smsRequest;
	}


	/**
	 * Creates email request for each owner
	 *
	 * @param garbageConnectionRequest Water Connection Request
	 * @param property Property Object
	 * @param paymentDetail Payment Detail Object
	 * @return List of EmailRequest
	 */
	private List<EmailRequest> getEmailRequest(GarbageConnectionRequest garbageConnectionRequest,
											   Property property, PaymentDetail paymentDetail) {

		if(paymentDetail.getTotalAmountPaid().intValue() == 0)
			return null;

		String localizationMessage = notificationUtil.getLocalizationMessages(property.getTenantId(),
				garbageConnectionRequest.getRequestInfo());

		String applicationStatus = garbageConnectionRequest.getGarbageConnection().getApplicationStatus();
		String notificationTemplate = PAYMENT_NOTIFICATION_EMAIL;
		ProcessInstance workflow = garbageConnectionRequest.getGarbageConnection().getProcessInstance();
		StringBuilder builder = new StringBuilder();
		int reqType;

		//Condition to assign Disconnection application Payment Notification code
		if ((!workflow.getAction().equalsIgnoreCase(GCConstants.ACTIVATE_CONNECTION)) &&
				(!workflow.getAction().equalsIgnoreCase(APPROVE_CONNECTION)) && gcServiceUtil.isDisconnectConnectionRequest(garbageConnectionRequest)) {
			reqType = DISCONNECT_CONNECTION;
			notificationTemplate = notificationUtil.getCustomizedMsgForEmailForPayment(workflow.getAction(), applicationStatus, reqType);
		}

		String message = notificationUtil.getMessageTemplate(notificationTemplate, localizationMessage);

		if (message == null) {
			log.info("No message template found for, {} " + GCConstants.PAYMENT_NOTIFICATION_EMAIL);
			return Collections.emptyList();
		}

		Map<String, String> mobileNumbersAndNames = new HashMap<>();
		Set<String> mobileNumbers = new HashSet<>();

		//Send the notification to all owners
		Set<String> ownersUuids = new HashSet<>();

		property.getOwners().forEach(owner -> {
			if (owner.getUuid() != null)
				ownersUuids.add(owner.getUuid());
		});

		//send the notification to the connection holders
		if (!CollectionUtils.isEmpty(garbageConnectionRequest.getGarbageConnection().getConnectionHolders())) {
			garbageConnectionRequest.getGarbageConnection().getConnectionHolders().forEach(holder -> {
				if (!org.apache.commons.lang.StringUtils.isEmpty(holder.getUuid())) {
					ownersUuids.add(holder.getUuid());
				}
			});
		}

		UserDetailResponse userDetailResponse = workflowNotificationService.fetchUserByUUID(ownersUuids, garbageConnectionRequest.getRequestInfo(), garbageConnectionRequest.getGarbageConnection().getTenantId());
		for (OwnerInfo user : userDetailResponse.getUser()) {
			mobileNumbersAndNames.put(user.getMobileNumber(), user.getName());
		}

		mobileNumbers.addAll(mobileNumbersAndNames.keySet());

		Map<String, String> mobileNumberAndEmailId = new HashMap<>();
		for (OwnerInfo user : userDetailResponse.getUser()) {
			mobileNumberAndEmailId.put(user.getMobileNumber(), user.getEmailId());
		}

		//Send the notification to applicant
		if (!StringUtils.isEmpty(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber())) {
			mobileNumbersAndNames.put(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber(), garbageConnectionRequest.getRequestInfo().getUserInfo().getName());
			mobileNumbers.add(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber());
			mobileNumberAndEmailId.put(garbageConnectionRequest.getRequestInfo().getUserInfo().getMobileNumber(), garbageConnectionRequest.getRequestInfo().getUserInfo().getEmailId());
		}

		Map<String, String> getReplacedMessage = workflowNotificationService.getMessageForMobileNumber(mobileNumbersAndNames,
				garbageConnectionRequest, message, property);

		Map<String, String> mobileNumberAndMessage =replacePaymentInfo(getReplacedMessage, paymentDetail);
		List<EmailRequest> emailRequest = new LinkedList<>();
		for (Map.Entry<String, String> entryset : mobileNumberAndEmailId.entrySet()) {
			String customizedMsg = mobileNumberAndMessage.get(entryset.getKey());
			String subject = customizedMsg.substring(customizedMsg.indexOf("<h2>")+4,customizedMsg.indexOf("</h2>"));
			String body = customizedMsg.substring(customizedMsg.indexOf("</h2>")+5);
			Email emailobj = Email.builder().emailTo(Collections.singleton(entryset.getValue())).isHTML(true).body(body).subject(subject).build();
			EmailRequest email = new EmailRequest(garbageConnectionRequest.getRequestInfo(),emailobj);
			emailRequest.add(email);
		}
		return emailRequest;

	}

	/**
	 *
	 * @param mobileAndMessage
	 * @param paymentDetail
	 * @return replaced message
	 */
	private Map<String, String> replacePaymentInfo(Map<String, String> mobileAndMessage, PaymentDetail paymentDetail) {
		Map<String, String> messageToReturn = new HashMap<>();
		for (Map.Entry<String, String> mobAndMesg : mobileAndMessage.entrySet()) {
			String message = mobAndMesg.getValue();
			if (message.contains("{Amount paid}")) {
				message = message.replace("{Amount paid}", paymentDetail.getTotalAmountPaid().toString());
			}
			if (message.contains("{Billing Period}")) {
				int fromDateLength = (int) (Math.log10(paymentDetail.getBill().getBillDetails().get(0).getFromPeriod()) + 1);
				LocalDate fromDate = Instant
						.ofEpochMilli(fromDateLength > 10 ? paymentDetail.getBill().getBillDetails().get(0).getFromPeriod() :
								paymentDetail.getBill().getBillDetails().get(0).getFromPeriod() * 1000)
						.atZone(ZoneId.systemDefault()).toLocalDate();
				int toDateLength = (int) (Math.log10(paymentDetail.getBill().getBillDetails().get(0).getToPeriod()) + 1);
				LocalDate toDate = Instant
						.ofEpochMilli(toDateLength > 10 ? paymentDetail.getBill().getBillDetails().get(0).getToPeriod() :
								paymentDetail.getBill().getBillDetails().get(0).getToPeriod() * 1000)
						.atZone(ZoneId.systemDefault()).toLocalDate();
				StringBuilder builder = new StringBuilder();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String billingPeriod = builder.append(fromDate.format(formatter)).append(" - ").append(toDate.format(formatter)).toString();
				message = message.replace("{Billing Period}", billingPeriod);
			}

			if (message.contains("{receipt download link}")){
				String link = config.getNotificationUrl() + config.getMyPaymentsLink();
				link = link.replace("$consumerCode", paymentDetail.getBill().getConsumerCode());
				link = link.replace("$tenantId", paymentDetail.getTenantId());
				link = link.replace("$businessService",paymentDetail.getBusinessService());
				link = link.replace("$receiptNumber",paymentDetail.getReceiptNumber());
				link = link.replace("$mobile", mobAndMesg.getKey());
				link = gcServiceUtil.getShortnerURL(link);
				message = message.replace("{receipt download link}",link);
			}

			messageToReturn.put(mobAndMesg.getKey(), message);
		}
		return messageToReturn;
	}

	public void noPaymentWorkflow(GarbageConnectionRequest request, Property property, RequestInfo requestInfo) {
		//Updating the workflow from approve for disconnection to pending for disconnection execution when there are no dues
		GarbageConnection waterRequest = request.getGarbageConnection();
		SearchCriteria criteria = new SearchCriteria();
		criteria = SearchCriteria.builder()
				.tenantId(waterRequest.getTenantId())
				.connectionNumber(Stream.of(waterRequest.getConnectionNo().toString()).collect(Collectors.toSet()))
				.applicationStatus(Collections.singleton(PENDING_FOR_PAYMENT_STATUS_CODE)).build();
		List<GarbageConnection> waterConnections = gcService.search(criteria,
				requestInfo);
		waterConnections.forEach(waterConnection -> waterConnection.getProcessInstance().setAction((GCConstants.ACTION_PAY)));
		Optional<GarbageConnection> connections = waterConnections.stream().findFirst();
		GarbageConnection connection = connections.get();
		GarbageConnectionRequest garbageConnectionRequest = GarbageConnectionRequest.builder()
				.garbageConnection(connection).requestInfo(requestInfo)
				.build();
		ProcessInstance processInstanceReq = garbageConnectionRequest.getGarbageConnection().getProcessInstance();
		processInstanceReq.setComment(WORKFLOW_NO_PAYMENT_CODE + " : " +WORKFLOW_NODUE_COMMENT);
		// Enrich tenantId in userInfo for workflow call
		Role role = Role.builder().code("SYSTEM_PAYMENT").tenantId(property.getTenantId()).build();
		Role counterEmployeeRole = Role.builder().name(COUNTER_EMPLOYEE_ROLE_NAME).code(COUNTER_EMPLOYEE_ROLE_CODE).tenantId(property.getTenantId()).build();
		requestInfo.getUserInfo().getRoles().add(role);
		requestInfo.getUserInfo().getRoles().add(counterEmployeeRole);
		//move the workflow
		wfIntegrator.callWorkFlow(garbageConnectionRequest, property);
		enrichmentService.enrichFileStoreIds(garbageConnectionRequest);
		repo.updateGarbageConnection(garbageConnectionRequest, false);
	}
}
