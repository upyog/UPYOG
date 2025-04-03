package org.egov.collection.service;

import static java.util.Objects.isNull;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.egov.collection.config.ApplicationProperties;
import org.egov.collection.model.Payment;
import org.egov.collection.model.PaymentRequest;
import org.egov.collection.model.PaymentSearchCriteria;
import org.egov.collection.producer.CollectionProducer;
import org.egov.collection.repository.PaymentRepository;
import org.egov.collection.util.PaymentEnricher;
import org.egov.collection.util.PaymentValidator;
import org.egov.collection.web.contract.Bill;
import org.egov.collection.web.contract.PropertyDetail;
import org.egov.collection.web.contract.RoadCuttingInfo;
import org.egov.collection.web.contract.UsageCategoryInfo;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentService {

	private static String propertyId;

	private ApportionerService apportionerService;

	private PaymentEnricher paymentEnricher;

	private ApplicationProperties applicationProperties;

	private UserService userService;

	private PaymentValidator paymentValidator;

	private PaymentRepository paymentRepository;

	private CollectionProducer producer;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	public PaymentService(ApportionerService apportionerService, PaymentEnricher paymentEnricher,
			ApplicationProperties applicationProperties, UserService userService, PaymentValidator paymentValidator,
			PaymentRepository paymentRepository, CollectionProducer producer) {
		this.apportionerService = apportionerService;
		this.paymentEnricher = paymentEnricher;
		this.applicationProperties = applicationProperties;
		this.userService = userService;
		this.paymentValidator = paymentValidator;
		this.paymentRepository = paymentRepository;
		this.producer = producer;
	}

	/**
	 * Fetch all receipts matching the given criteria, enrich receipts with
	 * instruments
	 *
	 * @param requestInfo           Request info of the search
	 * @param paymentSearchCriteria Criteria against which search has to be
	 *                              performed
	 * @return List of matching receipts
	 */
	public List<Payment> getPayments(RequestInfo requestInfo, PaymentSearchCriteria paymentSearchCriteria,
			String moduleName) {
		
	    

		paymentValidator.validateAndUpdateSearchRequestFromConfig(paymentSearchCriteria, requestInfo, moduleName);
		if (applicationProperties.isPaymentsSearchPaginationEnabled()) {
			paymentSearchCriteria
					.setOffset(isNull(paymentSearchCriteria.getOffset()) ? 0 : paymentSearchCriteria.getOffset());
			paymentSearchCriteria.setLimit(
					isNull(paymentSearchCriteria.getLimit()) ? applicationProperties.getReceiptsSearchDefaultLimit()
							: paymentSearchCriteria.getLimit());
		} else {
			paymentSearchCriteria.setOffset(0);
			paymentSearchCriteria.setLimit(applicationProperties.getReceiptsSearchDefaultLimit());
		}
		/*
		 * if (requestInfo.getUserInfo().getType().equals("CITIZEN")) { List<String>
		 * payerIds = new ArrayList<>();
		 * payerIds.add(requestInfo.getUserInfo().getUuid());
		 * paymentSearchCriteria.setPayerIds(payerIds); }
		 */
		List<Payment> payments = paymentRepository.fetchPayments(paymentSearchCriteria);
		
		
		if (payments != null && !payments.isEmpty()) {
			Collections.sort(payments.get(0).getPaymentDetails().get(0).getBill().getBillDetails(),
					(b1, b2) -> b2.getFromPeriod().compareTo(b1.getFromPeriod()));
		}
		
		
		if ((null != paymentSearchCriteria.getBusinessService() ||null != paymentSearchCriteria.getReceiptNumbers())  && payments != null && !payments.isEmpty())
		{
			String businessservice =null;
			
			
			if (null != paymentSearchCriteria.getReceiptNumbers()) 
				
			{
				String receiptnumber = null;
				Iterator<String> iterate = paymentSearchCriteria.getReceiptNumbers().iterator();
				while (iterate.hasNext()) {
					receiptnumber = iterate.next();
				}
				String receipts[] = receiptnumber.split("/");

				String businessservices[] = receipts[0].split("_");
				businessservice=businessservices[0];
				if (businessservices[0].equals("WS") || businessservices[0].equals("SW")) 
				{
					List<String> consumercode = paymentRepository.fetchConsumerCodeByReceiptNumber(receiptnumber);

					// Create a Set to hold the application number
					Set<String> applicationNumbers = new HashSet<>();
					/* w/s receipt bug fixing for ledger id and other things-- Abhishek (ticket number-- Defect #24)  */
					if (consumercode.get(0).contains("WS_AP") || consumercode.get(0).contains("SW_AP"))
					{
						
					    applicationNumbers.add(consumercode.get(0)); // Add the string to the Set
					    log.info("final consumercode "+applicationNumbers);
					    paymentSearchCriteria.setApplicationNo(applicationNumbers); // Pass the Set to the method
					}
					else
					{
					    applicationNumbers.add(consumercode.get(0)); // Add the string to the Set

						paymentSearchCriteria.setConsumerCodes(applicationNumbers);
					}
				}
				
			}
				
			
			else
			businessservice = paymentSearchCriteria.getBusinessService();
			
			if (businessservice.equals("WS")|| businessservice.equals("SW") || businessservice.equals("WS.ONE_TIME_FEE") || businessservice.equals("SW.ONE_TIME_FEE")) 
			{
				
				if ((businessservice.equals("WS.ONE_TIME_FEE")||businessservice.equals("SW.ONE_TIME_FEE")) && paymentSearchCriteria.getConsumerCodes()!=null  && paymentSearchCriteria.getConsumerCodes()!=null )
				{
					paymentSearchCriteria.setConsumerCodes(null);
				}
				
			
			
				
				String meterMake = null;
				String avarageMeterReading = null;
				String initialMeterReading = null;
				String ledgerId = null;
				String conncat = null;
				String landArea = null;
				String meterId = null;
				String meterInstallationDate = null;
				String roadType = null;
				String roadLength = null;
				String groups = null;
				String usageCategory = null;
				String address = null;
				List<String> ownerName = new ArrayList<>(); // for Multiple Owners
				List<String> mobileNumber = new ArrayList<>(); // for Multiple owners
				
			     
				 // WS Object
				
				Object Curl_details = paymentRepository.Curl_WS(
					    requestInfo,
					    paymentSearchCriteria.getConsumerCodes(),
					    paymentSearchCriteria.getApplicationNo(),
					    paymentSearchCriteria.getTenantId(),
					    businessservice
					);
				

				if (Curl_details instanceof List) {
				    List<Map<String, Object>> detailsList = (List<Map<String, Object>>) Curl_details;

				    if (!detailsList.isEmpty()) {
				        Map<String, Object> firstEntry = detailsList.get(0); // Get the first object

				        // Extract additionalDetails
				        Map<String, Object> additionalDetails = (Map<String, Object>) firstEntry.get("additionalDetails");

				        if (additionalDetails != null) {
				            meterMake = String.valueOf(additionalDetails.get("meterMake"));
				            avarageMeterReading = additionalDetails.get("avarageMeterReading") != null ? (additionalDetails.get("avarageMeterReading")).toString() : "0" ;
				            initialMeterReading = additionalDetails.get("initialMeterReading") != null ? additionalDetails.get("initialMeterReading").toString() : "0" ; // use ? for null condition
				            ledgerId = String.valueOf(additionalDetails.get("ledgerId"));
				            conncat = String.valueOf(additionalDetails.get("connectionCategory"));
				            groups = String.valueOf(additionalDetails.get("groups"));
				        }

				        // Extract Road Cutting Info
				        List<Map<String, Object>> roadCuttingInfo = (List<Map<String, Object>>) firstEntry.get("roadCuttingInfo");

				        // Extract Meter Details
				        meterId = String.valueOf(firstEntry.get("meterId"));
				        meterInstallationDate = String.valueOf(firstEntry.get("meterInstallationDate"));

				        if (roadCuttingInfo != null && !roadCuttingInfo.isEmpty()) {
				            Map<String, Object> firstRoadInfo = roadCuttingInfo.get(0); // Get the first map from the list

				            // Extract Road Type and Length safely
				            roadType = firstRoadInfo.get("roadType") != null ? firstRoadInfo.get("roadType").toString() : null;
				            roadLength = firstRoadInfo.get("roadCuttingArea") != null ? firstRoadInfo.get("roadCuttingArea").toString() : null;
				        }   
			        }
				}

				
				if (Curl_details != null && Curl_details instanceof List) {
				    List<Map<String, Object>> detailsList = (List<Map<String, Object>>) Curl_details;
				    for (Map<String, Object> item : detailsList) {
				    	
				        if ("Active".equals(item.get("status"))) {
				        	propertyId = (String) item.get("propertyId"); 
				        }
				    }
				}
				
				Object curlDetailProperty = paymentRepository.Curl_Property(
					    requestInfo,
					    paymentSearchCriteria.getTenantId(),
					    propertyId 
					);
				
				if (curlDetailProperty instanceof List) {
				    List<Map<String, Object>> tempList = (List<Map<String, Object>>) curlDetailProperty;
				    if (!tempList.isEmpty()) {
				        Map<String, Object> temporaryList = tempList.get(0);

				        
				        List<Map<String, Object>> ownersList = (List<Map<String, Object>>) temporaryList.get("owners");
				        
				        
				        Map<String, Object> addressMap = (Map<String, Object>) temporaryList.get("address");
				        
				        String doorNo = addressMap.get("doorNo") != null ? addressMap.get("doorNo").toString() : "";
				        String buildingName = addressMap.get("buildingName") != null ? addressMap.get("buildingName").toString() : "";
				        String street = addressMap.get("street") != null ? addressMap.get("street").toString() : "";
				        String landmark = addressMap.get("landmark") != null ? addressMap.get("landmark").toString() : "" ;
				        String region = addressMap.get("region") != null ? addressMap.get("region").toString() : "";
				        String city = addressMap.get("city") != null ? addressMap.get("city").toString() : "";
				        String district = addressMap.get("district") != null ? addressMap.get("district").toString() : "";
				        String state = addressMap.get("state") != null ? addressMap.get("state").toString() : "";
				        String pincode = addressMap.get("pincode") != null ? addressMap.get("pincode").toString() : "";
				        
				        address = doorNo + ", " + buildingName + ", " + street + ", " + landmark + ", " + region + ", " 
		                        + city + ", " + district + ", " + state + ", " + pincode;
				        
				        
			        	usageCategory = String.valueOf(tempList.get(0).get("usageCategory"));
			        	landArea = String.valueOf(tempList.get(0).get("landArea"));	   
				        	
			        	if (ownersList != null && !ownersList.isEmpty()) {
			        	    for (Map<String, Object> owner : ownersList ) {
			        	    	String mobileNo = (String) owner.get("mobileNumber");
			        	        String name = (String) owner.get("name");
			        	        if (name != null) {
			        	            ownerName.add(name);
			        	            mobileNumber.add(mobileNo);
			        	        }
			        	    }
			        	}
			        }     
				    	
				    // Set values
				    payments.get(0).setUsageCategory(usageCategory);
				    payments.get(0).setOwnername(ownerName);
				    payments.get(0).setOwnerNumber(mobileNumber);
				    payments.get(0).setAddress(address);
				    payments.get(0).setLandarea(landArea);
				    payments.get(0).setPropertyId(propertyId);
				    
				    payments.get(0).setMeterMake(meterMake);
			        payments.get(0).setAvarageMeterReading(avarageMeterReading);
			        payments.get(0).setInitialMeterReading(initialMeterReading);
			        payments.get(0).setLedgerId(ledgerId);
			        payments.get(0).setGroupId(groups);
			        payments.get(0).setConnectionCategory(conncat);
			        payments.get(0).setRoadtype(roadType);
			        payments.get(0).setRoadlength(roadLength);
			        
			        payments.get(0).setMeterinstallationDate(meterInstallationDate);
			        payments.get(0).setMeterId(meterId);
				    } 
				
							
			}
		}
		return payments;
	}

	public Long getpaymentcountForBusiness(String tenantId, String businessService) {

		return paymentRepository.getPaymentsCount(tenantId, businessService);
	}

	@Transactional
	public Payment updatePaymentForFilestore(Payment payment) {

		paymentRepository.updateFileStoreIdToNull(payment);
		return payment;
	}

	/**
	 * Handles creation of a receipt, including multi-service, involves the
	 * following steps, - Enrich receipt from billing service using bill id -
	 * Validate the receipt object - Enrich receipt with receipt numbers, coll type
	 * etc - Apportion paid amount - Persist the receipt object - Create instrument
	 *
	 * @param paymentRequest payment request for which receipt has to be created
	 * @return Created receipt
	 */
	@Transactional
	public Payment createPayment(PaymentRequest paymentRequest) {

		paymentEnricher.enrichPaymentPreValidate(paymentRequest, false);
		paymentValidator.validatePaymentForCreate(paymentRequest);
		paymentEnricher.enrichPaymentPostValidate(paymentRequest);

		Payment payment = paymentRequest.getPayment();
		Map<String, Bill> billIdToApportionedBill = apportionerService.apportionBill(paymentRequest);
		paymentEnricher.enrichAdvanceTaxHead(new LinkedList<>(billIdToApportionedBill.values()));
		setApportionedBillsToPayment(billIdToApportionedBill, payment);

		String payerId = createUser(paymentRequest);
		if (!StringUtils.isEmpty(payerId))
			payment.setPayerId(payerId);
		paymentRepository.savePayment(payment);

		producer.producer(applicationProperties.getCreatePaymentTopicName(), paymentRequest);

		return payment;
	}

	private void setPropertyData(String receiptnumber, List<Payment> payments, String businessservice) {
		List<String> consumercode = paymentRepository.fetchConsumerCodeByReceiptNumber(receiptnumber);
		String connectionno = consumercode.get(0);
		PropertyDetail status = paymentRepository.fetchPropertyDetail(connectionno, businessservice);

		if (status != null) {
			HashMap<String, String> additionalDetail = new HashMap<>();

			if (!StringUtils.isEmpty(status.getOldConnectionNo())) {
				additionalDetail.put("oldConnectionno", status.getOldConnectionNo());
			}

			if (!StringUtils.isEmpty(status.getPlotSize())) {
				additionalDetail.put("landArea", status.getPlotSize());
			}

			if (!StringUtils.isEmpty(status.getUsageCategory())) {
				additionalDetail.put("usageCategory", status.getUsageCategory());
				payments.get(0).setUsageCategory(status.getUsageCategory());
			}

			if (!StringUtils.isEmpty(status.getPropertyId())) {
				payments.get(0).setPropertyId(status.getPropertyId());
			}

			if (!StringUtils.isEmpty(status.getAddress())) {
				payments.get(0).setAddress(status.getAddress());
			}

			if (!StringUtils.isEmpty(status.getMeterDetails())) {
				payments.get(0).setMeterinstallationDate(status.getMeterDetails());
			}

			if (!StringUtils.isEmpty(status.getMeterId())) {
				payments.get(0).setMeterId(status.getMeterId());
			}

			if (!StringUtils.isEmpty(status.getAverageMeterReading())) {
				payments.get(0).setAvarageMeterReading(status.getAverageMeterReading());
			}

			if (!StringUtils.isEmpty(status.getInitialMeterReading())) {
				payments.get(0).setInitialMeterReading(status.getInitialMeterReading());
			}

			if (!StringUtils.isEmpty(status.getMeterMake())) {
				payments.get(0).setMeterMake(status.getMeterMake());
			}

			payments.get(0).setPropertyDetail(additionalDetail);
		}
	}

	/**
	 * If Citizen is paying, the id of the logged in user becomes payer id. If
	 * Employee is paying, 1. the id of the owner of the bill will be attached as
	 * payer id. 2. In case the bill is for a misc payment, payer id is empty.
	 * 
	 * @param paymentRequest
	 * @return
	 */
	public String createUser(PaymentRequest paymentRequest) {

		String id = null;
		if (paymentRequest.getRequestInfo().getUserInfo().getType().equals("CITIZEN")) {
			id = paymentRequest.getRequestInfo().getUserInfo().getUuid();
		} else {
			if (applicationProperties.getIsUserCreateEnabled()) {
				Payment payment = paymentRequest.getPayment();
				Map<String, String> res = userService.getUser(paymentRequest.getRequestInfo(),
						payment.getMobileNumber(), payment.getTenantId());
				if (CollectionUtils.isEmpty(res.keySet())) {
					id = userService.createUser(paymentRequest);
				} else {
					id = res.get("id");
				}
			}
		}
		return id;
	}

	private void setApportionedBillsToPayment(Map<String, Bill> billIdToApportionedBill, Payment payment) {
		Map<String, String> errorMap = new HashMap<>();
		payment.getPaymentDetails().forEach(paymentDetail -> {
			if (billIdToApportionedBill.get(paymentDetail.getBillId()) != null)
				paymentDetail.setBill(billIdToApportionedBill.get(paymentDetail.getBillId()));
			else
				errorMap.put("APPORTIONING_ERROR",
						"The bill id: " + paymentDetail.getBillId() + " not present in apportion response");
		});
		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	@Transactional
	public List<Payment> updatePayment(PaymentRequest paymentRequest) {

		List<Payment> validatedPayments = paymentValidator.validateAndEnrichPaymentsForUpdate(
				Collections.singletonList(paymentRequest.getPayment()), paymentRequest.getRequestInfo());

		paymentRepository.updatePayment(validatedPayments);
		producer.producer(applicationProperties.getUpdatePaymentTopicName(),
				new PaymentRequest(paymentRequest.getRequestInfo(), paymentRequest.getPayment()));

		return validatedPayments;
	}

	/**
	 * Used by payment gateway to validate provisional receipts of the payment
	 * 
	 * @param paymentRequest
	 * @return
	 */
	@Transactional
	public Payment vaidateProvisonalPayment(PaymentRequest paymentRequest) {
		paymentEnricher.enrichPaymentPreValidate(paymentRequest, false);
		paymentValidator.validatePaymentForCreate(paymentRequest);

		return paymentRequest.getPayment();
	}

//    @Transactional
//    public Payment updatePaymentForFilestore(Payment payment) {
//
//       paymentRepository.updateFileStoreIdToNull(payment);
//        return payment;
//    }

	public List<Payment> plainSearch(PaymentSearchCriteria paymentSearchCriteria) {
		PaymentSearchCriteria searchCriteria = new PaymentSearchCriteria();

		log.info("plainSearch Service BusinessServices" + paymentSearchCriteria.getBusinessServices()
				+ "plainSearch Service Date " + paymentSearchCriteria.getFromDate() + " to "
				+ paymentSearchCriteria.getToDate() + "Teant IT " + paymentSearchCriteria.getTenantId()
				+ " \"plainSearch Service BusinessServices\"+paymentSearchCriteria.getBusinessService():"
				+ paymentSearchCriteria.getBusinessService());

		if (applicationProperties.isPaymentsSearchPaginationEnabled()) {
			searchCriteria.setOffset(isNull(paymentSearchCriteria.getOffset()) ? 0 : paymentSearchCriteria.getOffset());
			searchCriteria.setLimit(
					isNull(paymentSearchCriteria.getLimit()) ? applicationProperties.getReceiptsSearchDefaultLimit()
							: paymentSearchCriteria.getLimit());
		} else {
			searchCriteria.setOffset(0);
			searchCriteria.setLimit(applicationProperties.getReceiptsSearchDefaultLimit());
		}

		if (paymentSearchCriteria.getTenantId() != null) {
			searchCriteria.setTenantId(paymentSearchCriteria.getTenantId());
		}

		if (paymentSearchCriteria.getBusinessServices() != null) {
			log.info("in PaymentService.java paymentSearchCriteria.getBusinessServices(): "
					+ paymentSearchCriteria.getBusinessServices());
			searchCriteria.setBusinessServices(paymentSearchCriteria.getBusinessServices());
		}

		if (paymentSearchCriteria.getBusinessService() != null) {
			log.info("in PaymentService.java paymentSearchCriteria.getBusinessService(): "
					+ paymentSearchCriteria.getBusinessService());
			searchCriteria.setBusinessService(paymentSearchCriteria.getBusinessService());
		}

		if ((paymentSearchCriteria.getFromDate() != null && paymentSearchCriteria.getFromDate() > 0)
				&& (paymentSearchCriteria.getToDate() != null && paymentSearchCriteria.getToDate() > 0)) {
			searchCriteria.setToDate(paymentSearchCriteria.getToDate());
			searchCriteria.setFromDate(paymentSearchCriteria.getFromDate());

		}

		List<String> ids = paymentRepository.fetchPaymentIds(searchCriteria);
		if (ids.isEmpty())
			return Collections.emptyList();

		PaymentSearchCriteria criteria = PaymentSearchCriteria.builder().ids(new HashSet<String>(ids)).build();
		return paymentRepository.fetchPaymentsForPlainSearch(criteria);
	}

	@Transactional
	public Payment createPaymentForWSMigration(PaymentRequest paymentRequest) {

		paymentEnricher.enrichPaymentPreValidate(paymentRequest, true);
		paymentValidator.validatePaymentForCreateWSMigration(paymentRequest);
		paymentEnricher.enrichPaymentPostValidate(paymentRequest);

		Payment payment = paymentRequest.getPayment();
		Map<String, Bill> billIdToApportionedBill = apportionerService.apportionBill(paymentRequest);
		paymentEnricher.enrichAdvanceTaxHead(new LinkedList<>(billIdToApportionedBill.values()));
		setApportionedBillsToPayment(billIdToApportionedBill, payment);

		String payerId = createUser(paymentRequest);
		if (!StringUtils.isEmpty(payerId))
			payment.setPayerId(payerId);
		paymentRepository.savePayment(payment);

		// producer.producer(applicationProperties.getCreatePaymentTopicName(),
		// paymentRequest);

		return payment;
	}
	
}