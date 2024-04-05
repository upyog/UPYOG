/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.demand.service;

import static org.egov.demand.util.Constants.BUSINESS_SERVICE_URL_PARAMETER;
import static org.egov.demand.util.Constants.CONSUMERCODES_REPLACE_TEXT;
import static org.egov.demand.util.Constants.TENANTID_REPLACE_TEXT;
import static org.egov.demand.util.Constants.URL_NOT_CONFIGURED_FOR_DEMAND_UPDATE_KEY;
import static org.egov.demand.util.Constants.URL_NOT_CONFIGURED_FOR_DEMAND_UPDATE_MSG;
import static org.egov.demand.util.Constants.URL_NOT_CONFIGURED_REPLACE_TEXT;
import static org.egov.demand.util.Constants.URL_PARAMS_FOR_SERVICE_BASED_DEMAND_APIS;
import static org.egov.demand.util.Constants.URL_PARAM_SEPERATOR;
import static org.egov.demand.util.Constants.URL_PARAMS_SEPARATER;
import static org.egov.demand.util.Constants.QUARTERLY;
import static org.egov.demand.util.Constants.Q1;
import static org.egov.demand.util.Constants.Q2;
import static org.egov.demand.util.Constants.Q3;
import static org.egov.demand.util.Constants.Q4;
import static org.egov.demand.util.Constants.HALFYEARLY;
import static org.egov.demand.util.Constants.H1;
import static org.egov.demand.util.Constants.H2;
import static org.egov.demand.util.Constants.YEARLY;
import static org.egov.demand.util.Constants.YR;


import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.egov.common.contract.request.PlainAccessRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.demand.config.ApplicationProperties;
import org.egov.demand.model.AssessmentResponseV2;
import org.egov.demand.model.AssessmentV2;
import org.egov.demand.model.BillAccountDetailV2;
import org.egov.demand.model.BillDetailV2;
import org.egov.demand.model.BillSearchCriteria;
import org.egov.demand.model.BillV2;
import org.egov.demand.model.BillV2.BillStatus;
import org.egov.demand.model.BusinessServiceDetail;
import org.egov.demand.model.Demand;
import org.egov.demand.model.DemandCriteria;
import org.egov.demand.model.DemandDetail;
import org.egov.demand.model.GenerateBillCriteria;
import org.egov.demand.model.TaxHeadMaster;
import org.egov.demand.model.TaxHeadMasterCriteria;
import org.egov.demand.model.Transaction;
import org.egov.demand.model.TransactionRequest;
import org.egov.demand.model.TransactionResponse;
import org.egov.demand.model.UpdateBillCriteria;
import org.egov.demand.model.UpdateBillRequest;
import org.egov.demand.producer.Producer;
import org.egov.demand.repository.BillRepositoryV2;
import org.egov.demand.repository.IdGenRepo;
import org.egov.demand.repository.ServiceRequestRepository;
import org.egov.demand.util.Util;
import org.egov.demand.web.contract.BillRequestV2;
import org.egov.demand.web.contract.BillResponseV2;
import org.egov.demand.web.contract.BusinessServiceDetailCriteria;
import org.egov.demand.web.contract.RequestInfoWrapper;
import org.egov.demand.web.contract.User;
import org.egov.demand.web.contract.UserResponse;
import org.egov.demand.web.contract.UserSearchRequest;
import org.egov.demand.web.contract.factory.ResponseFactory;
import org.egov.demand.web.validator.BillValidator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillServicev2 {

	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	private ResponseFactory responseFactory;

	@Autowired
	private ApplicationProperties appProps;

	@Autowired
	private BillRepositoryV2 billRepository;

	@Autowired
	private DemandService demandService;

	@Autowired
	private BusinessServDetailService businessServDetailService;

	@Autowired
	private TaxHeadMasterService taxHeadService;

	@Autowired
	private Util util;

	@Autowired
	private ServiceRequestRepository restRepository;

	@Autowired
	private IdGenRepo idGenRepo;

	@Autowired
	private BillValidator billValidator;

	@Autowired
	private Producer producer;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${kafka.topics.cancel.bill.topic.name}")
	private String billCancelTopic;

	@Value("${kafka.topics.billgen.topic.name}")
	private String notifTopicName;

	private static List<String> ownerPlainRequestFieldsList;

	/**
	 * Cancell bill operation can be carried by this method, based on consumerCodes
	 * and businessService.
	 * 
	 * Only ACTIVE bills will be cancelled as of now
	 * 
	 * @param cancelBillCriteria
	 * @param requestInfoWrapper
	 */
	public Integer cancelBill(UpdateBillRequest updateBillRequest) {

		UpdateBillCriteria cancelBillCriteria = updateBillRequest.getUpdateBillCriteria();
		billValidator.validateBillSearchRequest(cancelBillCriteria);
		Set<String> consumerCodes = cancelBillCriteria.getConsumerCodes();
		cancelBillCriteria.setStatusToBeUpdated(BillStatus.CANCELLED);

		if (!CollectionUtils.isEmpty(consumerCodes) && consumerCodes.size() > 1) {

			throw new CustomException("EG_BS_CANCEL_BILL_ERROR", "Only one consumer code can be provided in the Cancel request");
		} else {
			int result = billRepository.updateBillStatus(cancelBillCriteria);
			sendNotificationForBillCancellation(updateBillRequest.getRequestInfo(), cancelBillCriteria);
			return result;
		}
	}

	private void sendNotificationForBillCancellation(RequestInfo requestInfo, UpdateBillCriteria cancelBillCriteria) {
		Set<String> consumerCodes = cancelBillCriteria.getConsumerCodes();
		if(CollectionUtils.isEmpty(consumerCodes))
			return;

		List<BillV2> bills =  billRepository.findBill(BillSearchCriteria.builder()
				.service(cancelBillCriteria.getBusinessService())
				.tenantId(cancelBillCriteria.getTenantId())
				.consumerCode(consumerCodes)
				.build());

		if (CollectionUtils.isEmpty(bills))
			return;

		BillRequestV2 req = BillRequestV2.builder().bills(bills).requestInfo(requestInfo).build();
		producer.push(billCancelTopic, req);

	}

	/**
	 * Fetches the bill for given parameters
	 * 
	 * Searches the respective bill
	 * if nothing found then generates bill for the same criteria
	 * if bill found then checks the validity of the bill
	 * 	return the bill if valid
	 * else update the demands belonging to the bill then generate a new bill
	 * 
	 * @param moduleCode
	 * @param consumerCodes
	 * @return
	 */
	public BillResponseV2 fetchBill(GenerateBillCriteria billCriteria, RequestInfoWrapper requestInfoWrapper) {

		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		billValidator.validateBillGenRequest(billCriteria, requestInfo);
		if (CollectionUtils.isEmpty(billCriteria.getConsumerCode()))
			billCriteria.setConsumerCode(new HashSet<>());
		BillResponseV2 res = searchBill(billCriteria.toBillSearchCriteria(), requestInfo);
		List<BillV2> bills = res.getBill();



		/* 
		 * If no existing bills found then Generate new bill 
		 */
		if (CollectionUtils.isEmpty(bills))
			return generateBill(billCriteria, requestInfo);

		/*
		 * Adding consumer-codes of unbilled demands to generate criteria
		 */
		if (!(StringUtils.isEmpty(billCriteria.getMobileNumber()) && StringUtils.isEmpty(billCriteria.getEmail()))) {

			List<Demand> demands = demandService.getDemands(billCriteria.toDemandCriteria(), requestInfo);
			billCriteria.getConsumerCode().addAll(
					demands.stream().map(Demand::getConsumerCode).collect(Collectors.toSet()));
		}

		log.debug("fetchBill--------going to generate new bill-------------------");
		Map<String, BillV2> consumerCodeAndBillMap = bills.stream().collect(Collectors.toMap(BillV2::getConsumerCode, Function.identity()));
		billCriteria.getConsumerCode().addAll(consumerCodeAndBillMap.keySet());
		/*
		 * Collecting the businessService code and the list of consumer codes for those service codes 
		 * whose demands needs to be updated.
		 * 
		 * grouping by service code and collecting the list of 
		 * consumerCodes against the service code
		 */
		List<String> cosnumerCodesNotFoundInBill = new ArrayList<>(billCriteria.getConsumerCode());
		Set<String> cosnumerCodesToBeExpired = new HashSet<>();
		List<BillV2> billsToBeReturned = new ArrayList<>();
		Boolean isBillExpired = false;

		for (Entry<String, BillV2> entry : consumerCodeAndBillMap.entrySet()) {
			BillV2 bill = entry.getValue();
			for (BillDetailV2 billDetail : bill.getBillDetails()) {
				if (billDetail.getExpiryDate().compareTo(System.currentTimeMillis()) < 0) {
					isBillExpired = true;
					break;
				}
			}
			if (!isBillExpired)
				billsToBeReturned.add(bill);
			else
				cosnumerCodesToBeExpired.add(bill.getConsumerCode());
			cosnumerCodesNotFoundInBill.remove(entry.getKey());
			isBillExpired = false;
		}

		/*
		 * If none of the billDetails in the bills needs to be updated then return the search result
		 */
		if(CollectionUtils.isEmpty(cosnumerCodesToBeExpired) && CollectionUtils.isEmpty(cosnumerCodesNotFoundInBill))
			return res;
		else {

			billCriteria.getConsumerCode().retainAll(cosnumerCodesToBeExpired);
			billCriteria.getConsumerCode().addAll(cosnumerCodesNotFoundInBill);
			updateDemandsForexpiredBillDetails(billCriteria.getBusinessService(), billCriteria.getConsumerCode(), billCriteria.getTenantId(), requestInfoWrapper);

			billRepository.updateBillStatus(
					UpdateBillCriteria.builder()
					.statusToBeUpdated(BillStatus.EXPIRED)
					.businessService(billCriteria.getBusinessService())
					.consumerCodes(cosnumerCodesToBeExpired)
					.tenantId(billCriteria.getTenantId())
					.build()
					);
			BillResponseV2 finalResponse = generateBill(billCriteria, requestInfo);
			// gen bill returns immutable empty list incase of zero bills
			billsToBeReturned.addAll(finalResponse.getBill());
			finalResponse.setBill(billsToBeReturned);
			return finalResponse;
		}
	}

	/**
	 * To make calls to respective service which updates the demands belonging to
	 * the arguments passed
	 * 
	 * @param serviceAndConsumerCodeListMap
	 * @param tenantId
	 */
	private void updateDemandsForexpiredBillDetails(String businessService, Set<String> consumerCodesTobeUpdated, String tenantId, RequestInfoWrapper requestInfoWrapper) {

		Map<String, String> serviceUrlMap = appProps.getBusinessCodeAndDemandUpdateUrlMap();


		String url = serviceUrlMap.get(businessService);
		if (StringUtils.isEmpty(url)) {

			log.info(URL_NOT_CONFIGURED_FOR_DEMAND_UPDATE_KEY, URL_NOT_CONFIGURED_FOR_DEMAND_UPDATE_MSG
					.replace(URL_NOT_CONFIGURED_REPLACE_TEXT, businessService));
			return;
		}

		StringBuilder completeUrl = new StringBuilder(url)
				.append(URL_PARAMS_FOR_SERVICE_BASED_DEMAND_APIS.replace(TENANTID_REPLACE_TEXT, tenantId).replace(
						CONSUMERCODES_REPLACE_TEXT, consumerCodesTobeUpdated.toString().replace("[", "").replace("]", "")));

		completeUrl.append(URL_PARAM_SEPERATOR).append(BUSINESS_SERVICE_URL_PARAMETER).append(businessService);
		log.info("the url : " + completeUrl);
		restRepository.fetchResult(completeUrl.toString(), requestInfoWrapper);
	}


	/**
	 * Searches the bills from DB for given criteria and enriches them with TaxAndPayments array
	 * 
	 * @param billCriteria
	 * @param requestInfo
	 * @return
	 */
	public BillResponseV2 searchBill(BillSearchCriteria billCriteria, RequestInfo requestInfo) {

		List<BillV2> bills = billRepository.findBill(billCriteria);

		return BillResponseV2.builder().resposneInfo(responseFactory.getResponseInfo(requestInfo, HttpStatus.OK))
				.bill(bills).build();
	}

	/**
	 * Generate bill based on the given criteria
	 * 
	 * @param billCriteria
	 * @param requestInfo
	 * @return
	 */
	public BillResponseV2 generateBill(GenerateBillCriteria billCriteria, RequestInfo requestInfo) {

		Set<String> demandIds = new HashSet<>();
		Set<String> consumerCodes = new HashSet<>();

		if (billCriteria.getDemandId() != null)
			demandIds.add(billCriteria.getDemandId());

		if (billCriteria.getConsumerCode() != null)
			consumerCodes.addAll(billCriteria.getConsumerCode());

		DemandCriteria demandCriteria = DemandCriteria.builder()
				.status(org.egov.demand.model.Demand.StatusEnum.ACTIVE.toString())
				.businessService(billCriteria.getBusinessService())
				.mobileNumber(billCriteria.getMobileNumber())
				.tenantId(billCriteria.getTenantId())
				.email(billCriteria.getEmail())
				.consumerCode(consumerCodes)
				.isPaymentCompleted(false)
				.receiptRequired(false)
				.demandId(demandIds)
				.build();


		/* Fetching demands for the given bill search criteria */
		List<Demand> demands = demandService.getDemands(demandCriteria, requestInfo);

		List<BillV2> bills;

		
		if (!demands.isEmpty())
			bills = prepareBill(demands, requestInfo,billCriteria.getModeOfPayment());
		else
			return getBillResponse(Collections.emptyList());

		BillRequestV2 billRequest = BillRequestV2.builder().bills(bills).requestInfo(requestInfo).build();
		//kafkaTemplate.send(notifTopicName, null, billRequest);
		return create(billRequest);
	}

	/**
	 * method to get user unmasked
	 * 
	 * @param requestInfo
	 * @param uuid
	 * @return user
	 */
	private User getUnmaskedUser(RequestInfo requestInfo, String uuid) {

		PlainAccessRequest apiPlainAccessRequest = requestInfo.getPlainAccessRequest();
		List<String> plainRequestFieldsList = getOwnerFieldsPlainAccessList();
		PlainAccessRequest plainAccessRequest = PlainAccessRequest.builder()
				.plainRequestFields(plainRequestFieldsList)
				.recordId(uuid)
				.build();
		requestInfo.setPlainAccessRequest(plainAccessRequest);

		UserSearchRequest  userSearchRequest= UserSearchRequest.builder()
				.uuid(Stream.of(uuid).collect(Collectors.toSet()))
				.requestInfo(requestInfo)
				.build();
		String userUri = appProps.getUserServiceHostName()
				.concat(appProps.getUserServiceSearchPath());
		List<User> payer = mapper.convertValue(restRepository.fetchResult(userUri, userSearchRequest),
				UserResponse.class).getUser();

		requestInfo.setPlainAccessRequest(apiPlainAccessRequest);

		return payer.get(0);
	}

	/**
	 * Prepares the bill object from the list of given demands
	 * 
	 * @param demands demands for which bill should be generated
	 * @param requestInfo 
	 * @param string 
	 * @return
	 */
	private List<BillV2> prepareBill(List<Demand> demands, RequestInfo requestInfo, String modeOfPayment) {

		System.out.println("modeOfPayment::"+modeOfPayment);
		List<BillV2> bills = new ArrayList<>();
		User payer = null != demands.get(0).getPayer() ? demands.get(0).getPayer() : new User();
		if (payer.getUuid() != null)
			payer = getUnmaskedUser(requestInfo, payer.getUuid());

		Map<String, List<Demand>> tenatIdDemandsList = demands.stream().collect(Collectors.groupingBy(Demand::getTenantId));
		for (Entry<String, List<Demand>> demandTenantEntry : tenatIdDemandsList.entrySet()) {

			/*
			 * Fetching Required master data
			 */
			String tenantId = demandTenantEntry.getKey();
			List<Demand> demandForOneTenant = demandTenantEntry.getValue();
			Set<String> businessCodes = new HashSet<>();
			Set<String> taxHeadCodes = new HashSet<>();

			for (Demand demand : demandForOneTenant) {

				businessCodes.add(demand.getBusinessService());
				demand.getDemandDetails().forEach(detail -> taxHeadCodes.add(detail.getTaxHeadMasterCode()));
			}

			Map<String, TaxHeadMaster> taxHeadMap = getTaxHeadMaster(taxHeadCodes, tenantId, requestInfo);
			Map<String, BusinessServiceDetail> businessMap = getBusinessService(businessCodes, tenantId, requestInfo);


			/*
			 * Grouping the demands by their consumer code and generating a bill for each consumer code
			 */
			Map<String, List<Demand>> consumerCodeAndDemandsMap = demandForOneTenant.stream().collect(Collectors.groupingBy(Demand::getConsumerCode));

			for (Entry<String, List<Demand>> consumerCodeAndDemands : consumerCodeAndDemandsMap.entrySet()) {

				BigDecimal billAmount = BigDecimal.ZERO;
				List<BillDetailV2> billDetails = new ArrayList<>();

				String consumerCode = consumerCodeAndDemands.getKey();
				BigDecimal minimumAmtPayableForBill = BigDecimal.ZERO;
				List<Demand> demandsForSingleCode = consumerCodeAndDemands.getValue();
				BusinessServiceDetail business = businessMap.get(demandsForSingleCode.get(0).getBusinessService());

				String billId = UUID.randomUUID().toString();
				String billNumber = getBillNumbers(requestInfo, tenantId, demandForOneTenant.get(0).getBusinessService(), 1).get(0);

				for (Demand demand : demandsForSingleCode) {

					minimumAmtPayableForBill = minimumAmtPayableForBill.add(demand.getMinimumAmountPayable());
					String billDetailId = UUID.randomUUID().toString();
					BillDetailV2 billDetail = getBillDetailForDemand(demand, taxHeadMap, billDetailId,requestInfo,modeOfPayment);
					billDetail.setBillId(billId);
					billDetail.setId(billDetailId);
					billDetails.add(billDetail);
					billAmount = billAmount.add(billDetail.getAmount());
				}

				if (billAmount.compareTo(BigDecimal.ZERO) >= 0) {

					BillV2 bill = BillV2.builder()
							.auditDetails(util.getAuditDetail(requestInfo))
							.payerAddress(payer.getPermanentAddress())
							.mobileNumber(payer.getMobileNumber())
							.billDate(System.currentTimeMillis())
							.businessService(business.getCode())
							.payerName(payer.getName())
							.consumerCode(consumerCode)
							.status(BillStatus.ACTIVE)
							.billDetails(billDetails)
							.totalAmount(billAmount)
							.userId(payer.getUuid())
							.billNumber(billNumber)
							.tenantId(tenantId)
							.id(billId)
							.build();

					bills.add(bill);
				}
			}

		}
		return bills;
	}


	private List<String> getBillNumbers(RequestInfo requestInfo, String tenantId, String module, int count) {

		String billNumberFormat = appProps.getBillNumberFormat();
		billNumberFormat = billNumberFormat.replace(appProps.getModuleReplaceStirng(), module);

		if (appProps.getIsTenantLevelBillNumberingEnabled())
			billNumberFormat = billNumberFormat.replace(appProps.getTenantIdReplaceString(), "_".concat(tenantId.split("\\.")[1]));
		else
			billNumberFormat = billNumberFormat.replace(appProps.getTenantIdReplaceString(), "");

		return idGenRepo.getId(requestInfo, tenantId, "billnumberid", billNumberFormat, count);
	}


	/**
	 * Method to create BillDetail object from demand
	 *  
	 * @param demand
	 * @param taxHeadMap
	 * @param requestInfo 
	 * @param modeOfPayment2 
	 * @param businessDetailMap
	 * @return
	 */
	private BillDetailV2 getBillDetailForDemand(Demand demand, Map<String, TaxHeadMaster> taxHeadMap, String billDetailId, RequestInfo requestInfo, String PaymentType) {
		String modeOfpayment = null;
		Long startPeriod = demand.getTaxPeriodFrom();
		Long endPeriod = demand.getTaxPeriodTo();
		String tenantId = demand.getTenantId();
		String propertyId=demand.getConsumerCode();

		BigDecimal totalAmountForDemand = BigDecimal.ZERO;
		boolean billsFound=false;

		System.out.println("");
		/*
		 * Map to store the bill account detail object with TaxHead code
		 * To accommodate conversion of multiple DemandDetails with same tax head code to single BillAccountDetail
		 */
		Map<String, BillAccountDetailV2> taxCodeAccountdetailMap = new HashMap<>();

		for(DemandDetail demandDetail : demand.getDemandDetails()) {

			TaxHeadMaster taxHead = taxHeadMap.get(demandDetail.getTaxHeadMasterCode());
			BigDecimal amountForAccDeatil = demandDetail.getTaxAmount().subtract(demandDetail.getCollectionAmount());

			addOrUpdateBillAccDetailInTaxCodeAccDetailMap(taxCodeAccountdetailMap, demandDetail, taxHead, billDetailId);

			/* Total tax and collection for the whole demand/bill-detail */
			totalAmountForDemand = totalAmountForDemand.add(amountForAccDeatil);
		}

		//Getting Financial year from Demand
		Date startDate = new Date(startPeriod);
		Date endDate = new Date(endPeriod);
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		StringBuilder financialYearFromDemand = new StringBuilder();
		financialYearFromDemand.append(c.get(Calendar.YEAR));
		financialYearFromDemand.append("-");
		c = Calendar.getInstance();
		c.setTime(endDate);
		//current Date

		Date currentDate = new Date(System.currentTimeMillis());
		Calendar crd = Calendar.getInstance();
		crd.setTime(currentDate);
		Integer cuurentMonth = crd.get(Calendar.MONTH)+4;
		Integer currentyear =  crd.get(Calendar.YEAR);

		Integer b= crd.get(Calendar.YEAR);
		financialYearFromDemand.append(b.toString().substring(2,4));



		String assesmentUrl = new StringBuilder().append(appProps.getAssessmentServiceHost()).append(appProps.getAssessmentSearchEndpoint())
				.append(URL_PARAMS_SEPARATER).append("propertyIds=").append(propertyId).append(URL_PARAM_SEPERATOR)
				.append("tenantId=").append(tenantId).toString();

		String transactiontUrl = new StringBuilder().append(appProps.getPgSeriviceHost()).append(appProps.getPgSeriviceEndpoint())
				.append(URL_PARAMS_SEPARATER).append("billId=").toString();

		TransactionRequest transactionRequest = null;
		AssessmentResponseV2 assessmentResponseV2=restTemplate.postForObject(assesmentUrl, requestInfo, AssessmentResponseV2.class);
		AssessmentV2 assessmentV2=null;
		if(!assessmentResponseV2.getAssessments().isEmpty())
		{
			assessmentV2=assessmentResponseV2.getAssessments().stream().filter(t->t.getFinancialYear().equalsIgnoreCase(financialYearFromDemand.toString())).collect(Collectors.toList()).get(0);
			if(null==assessmentV2.getModeOfPayment()) {
				throw new CustomException("INVALID_MODE_OF_PAYMENT", "Mode Of Payment Not found for the assesment");

			}

		}


		Set<Integer> q1 = new HashSet<>(Arrays.asList(4,5,6));
		Set<Integer> q2 = new HashSet<>(Arrays.asList(7,8,9));
		Set<Integer> q3 = new HashSet<>(Arrays.asList(10,11,12));
		Set<Integer> q4 = new HashSet<>(Arrays.asList(1,2,3));

		Set<Integer> h1 = new HashSet<>(Arrays.asList(4,5,6,7,8,9));
		Set<Integer> h2 = new HashSet<>(Arrays.asList(10,11,12,1,2,3));


		String paymentPeriod = null;
		String expiryDate = null;
		BigDecimal newTotalAmountForModeOfPayment = new BigDecimal(0);

		BillSearchCriteria billCriteria = new BillSearchCriteria();
		billCriteria.setTenantId(demand.getTenantId());
		billCriteria.setDemandId(demand.getId());
		List<Transaction> transactionForQuaterBills = new ArrayList<>();
		List<Transaction> transactionForHalfYearBills = new ArrayList<>();
		Map<String,String> quaterCheckMap = null;
		Map<String, BigDecimal> failedtransactionMapForQuater = new HashMap<>();
		Map<String, BigDecimal> failedtransactionMapForHalfYearly = new HashMap<>();

		Map<String, BigDecimal> alltransactionForQuater = new HashMap<>();
		Map<String, BigDecimal> alltransactionForHalfYearly = new HashMap<>();

		BigDecimal amountforquaterly=totalAmountForDemand.divide(new BigDecimal(4));
		BigDecimal ammountforhalfyearly=totalAmountForDemand.divide(new BigDecimal(2));

		List<BillV2> bills = billRepository.findBill(billCriteria);
		if(null!=bills) {
			quaterCheckMap = new HashMap<>();
			billsFound =true;
			for(BillV2 bl:bills) {
				transactionRequest = new TransactionRequest();
				transactionRequest.setRequestInfo(requestInfo);
				Transaction tr = new Transaction();
				tr.setBillId(bl.getId());
				transactionRequest.setTransaction(tr);
				TransactionResponse tres = restTemplate.postForObject(transactiontUrl+bl.getId(), transactionRequest, TransactionResponse.class);
				List<Transaction> failedtransactions=tres.getTransactions().stream().filter(t->t.getTxnStatus().equals(Transaction.TxnStatusEnum.FAILURE)).collect(Collectors.toList());
				List<Transaction> alltrnasactions=tres.getTransactions();
				if(null!=failedtransactions) {
					if(assessmentV2.getModeOfPayment().toString().equalsIgnoreCase(QUARTERLY)) {
						failedtransactionMapForQuater.put(bl.getBillDetails().get(0).getPaymentPeriod(), new BigDecimal(failedtransactions.get(0).getTxnAmount()));
					}
					if(assessmentV2.getModeOfPayment().toString().equalsIgnoreCase(HALFYEARLY)) {
						failedtransactionMapForHalfYearly.put(bl.getBillDetails().get(0).getPaymentPeriod(), new BigDecimal(failedtransactions.get(0).getTxnAmount()));
					}

				}
				if(alltrnasactions==null)
				{
					if(assessmentV2.getModeOfPayment().toString().equalsIgnoreCase(QUARTERLY)) {
						alltransactionForQuater.put(bl.getBillDetails().get(0).getPaymentPeriod(), bl.getBillDetails().get(0).getAmount());
					}
					if(assessmentV2.getModeOfPayment().toString().equalsIgnoreCase(HALFYEARLY)) {
						alltransactionForHalfYearly.put(bl.getBillDetails().get(0).getPaymentPeriod(), bl.getBillDetails().get(0).getAmount());
					}
				}
			}

		}

		System.out.println("modeOfpayment:"+PaymentType);
		String Paymentmode;
		if(assessmentV2!=null)
			Paymentmode=assessmentV2.getModeOfPayment().toString();
		else
			Paymentmode=PaymentType;
		switch (Paymentmode) {
		case QUARTERLY:
			//calcualtion of all quaterly ammount
			BigDecimal failedquaterammount=BigDecimal.ZERO;
			BigDecimal allquaterammount=BigDecimal.ZERO;
			BigDecimal quaterlyammount=BigDecimal.ZERO;
			for(BigDecimal failedquatervalue:failedtransactionMapForQuater.values())
			{
				failedquaterammount=failedquaterammount.add(failedquatervalue);
			}

			for(BigDecimal allquatervalue:alltransactionForQuater.values())
			{
				allquaterammount=allquaterammount.add(allquatervalue);
			}

			allquaterammount=allquaterammount.add(failedquaterammount);

			if(q1.contains(cuurentMonth)) {
				paymentPeriod=Q1;
				expiryDate="30-06-"+currentyear;
				newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new BigDecimal(4));
				totalAmountForDemand = newTotalAmountForModeOfPayment.add(allquaterammount);
			}
			else if(q2.contains(cuurentMonth))
			{
				paymentPeriod=Q2;
				expiryDate="30-09-"+currentyear;
				newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new BigDecimal(4));
				if(!failedtransactionMapForQuater.containsKey(Q1) && !alltransactionForQuater.containsKey(Q1))
					quaterlyammount=ammountForTransactionperiod(Q1,amountforquaterly);
				allquaterammount=allquaterammount.add(quaterlyammount);
				totalAmountForDemand = newTotalAmountForModeOfPayment.add(allquaterammount);

			}
			else if(q3.contains(cuurentMonth))
			{
				paymentPeriod=Q3;
				expiryDate="31-12-"+currentyear;
				newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new BigDecimal(4));

				if(!failedtransactionMapForQuater.containsKey(Q1) && !alltransactionForQuater.containsKey(Q1))
					quaterlyammount=ammountForTransactionperiod(Q1,amountforquaterly);

				if(!failedtransactionMapForQuater.containsKey(Q2) && !alltransactionForQuater.containsKey(Q2))
					quaterlyammount=ammountForTransactionperiod(Q2,amountforquaterly);

				allquaterammount=allquaterammount.add(quaterlyammount);

				totalAmountForDemand = newTotalAmountForModeOfPayment.add(allquaterammount);

			}
			else if(q4.contains(cuurentMonth))
			{
				paymentPeriod=Q4;
				expiryDate="31-03-"+currentyear;
				newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new BigDecimal(4));
				if(!failedtransactionMapForQuater.containsKey(Q1) && !alltransactionForQuater.containsKey(Q1))
					quaterlyammount=ammountForTransactionperiod(Q1,amountforquaterly);

				if(!failedtransactionMapForQuater.containsKey(Q2) && !alltransactionForQuater.containsKey(Q2))
					quaterlyammount=ammountForTransactionperiod(Q2,amountforquaterly);

				if(!failedtransactionMapForQuater.containsKey(Q3) && !alltransactionForQuater.containsKey(Q3))
					quaterlyammount=ammountForTransactionperiod(Q3,amountforquaterly);

				allquaterammount=allquaterammount.add(quaterlyammount);

				totalAmountForDemand = newTotalAmountForModeOfPayment.add(allquaterammount);

			}

			break;

		case HALFYEARLY:
			//calcualtion of all halfyearly ammount
			BigDecimal failedhalfyearammount=BigDecimal.ZERO;
			BigDecimal allhalfyearammount=BigDecimal.ZERO;
			BigDecimal halfyearlyammount=BigDecimal.ZERO;
			for(BigDecimal failedhalfyearvalue:failedtransactionMapForHalfYearly.values())
			{
				failedhalfyearammount=failedhalfyearammount.add(failedhalfyearvalue);
			}

			for(BigDecimal allhalfyearvalue:alltransactionForHalfYearly.values())
			{
				allhalfyearammount=allhalfyearammount.add(allhalfyearvalue);
			}

			allhalfyearammount=allhalfyearammount.add(failedhalfyearammount);

			if(h1.contains(cuurentMonth)) {
				paymentPeriod=H1;
				expiryDate="30-09-"+currentyear;

				newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new BigDecimal(2));
				totalAmountForDemand = newTotalAmountForModeOfPayment.add(allhalfyearammount);
			}
			else if(h2.contains(cuurentMonth))
			{
				paymentPeriod=H2;
				expiryDate="31-12-"+currentyear;

				newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new BigDecimal(2));
				if(!failedtransactionMapForHalfYearly.containsKey(H1) && !alltransactionForHalfYearly.containsKey(H1))
					halfyearlyammount=ammountForTransactionperiod(H1,ammountforhalfyearly);
				allhalfyearammount=allhalfyearammount.add(halfyearlyammount);
				totalAmountForDemand = newTotalAmountForModeOfPayment.add(allhalfyearammount);
			}

			break;

		case YEARLY:
			paymentPeriod=YR;
			expiryDate="31-12-"+currentyear;
			break;
		default:
			break;
		}

		Calendar datexp = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		try {
			java.util.Date parsedDate =  dateFormat.parse(expiryDate);
			datexp.setTime(parsedDate);
			datexp.set(datexp.get(Calendar.YEAR), datexp.get(Calendar.MONTH), datexp.get(Calendar.DATE), 23, 59, 59);
		} catch (ParseException e) {

			e.printStackTrace();
		}

		//Long billExpiryDate = getExpiryDateForDemand(demand);
		Long billExpiryDate = datexp.getTimeInMillis();

		return BillDetailV2.builder()
				.billAccountDetails(new ArrayList<>(taxCodeAccountdetailMap.values()))
				.amount(totalAmountForDemand)
				.expiryDate(billExpiryDate)
				.demandId(demand.getId())
				.fromPeriod(startPeriod)
				.toPeriod(endPeriod)
				.tenantId(tenantId)
				.paymentPeriod(paymentPeriod)
				.additionalDetails(demand.getAdditionalDetails())
				.build();
	}

	private BigDecimal ammountForTransactionperiod(String paymentPeriod, BigDecimal amountforquater) {
		// TODO Auto-generated method stub
		String num=paymentPeriod.substring(1);
		BigDecimal ammount=amountforquater.multiply(new BigDecimal(num));
		return ammount;
	}

	/**
	 * @param demand
	 * 
	 * @return expiryDate
	 */
	private Long getExpiryDateForDemand(Demand demand) {

		Long billExpiryPeriod = demand.getBillExpiryTime();
		Long fixedBillExpiryDate = demand.getFixedBillExpiryDate();
		Calendar cal = Calendar.getInstance();

		if (!ObjectUtils.isEmpty(fixedBillExpiryDate) && fixedBillExpiryDate > cal.getTimeInMillis()) {
			cal.setTimeInMillis(fixedBillExpiryDate);
		} else if (!ObjectUtils.isEmpty(billExpiryPeriod) && 0 < billExpiryPeriod) {
			cal.setTimeInMillis(cal.getTimeInMillis() + billExpiryPeriod);
		}

		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
		return cal.getTimeInMillis();
	}

	/**
	 * creates/ updates bill-account details based on the tax-head code in
	 * taxCodeAccDetailMap
	 * 
	 * @param startPeriod
	 * @param endPeriod
	 * @param tenantId
	 * @param taxCodeAccDetailMap
	 * @param demandDetail
	 * @param taxHead
	 * @param amountForAccDeatil
	 */
	private void addOrUpdateBillAccDetailInTaxCodeAccDetailMap(Map<String, BillAccountDetailV2> taxCodeAccDetailMap,
			DemandDetail demandDetail, TaxHeadMaster taxHead, String billDetailId) {

		BigDecimal newAmountForAccDeatil = demandDetail.getTaxAmount().subtract(demandDetail.getCollectionAmount());
		/*
		 * BAD - BillAccountDetail
		 * 
		 * To handle repeating tax-head codes in demand
		 * 
		 * And merge them in to single BAD
		 * 
		 * if taxHeadCode found in map then add the amount to existing BAD
		 * 
		 * else create and add a new BAD
		 */
		if (taxCodeAccDetailMap.containsKey(taxHead.getCode())) {

			BillAccountDetailV2 existingAccDetail = taxCodeAccDetailMap.get(taxHead.getCode());
			BigDecimal existingAmtForAccDetail = existingAccDetail.getAmount();
			existingAccDetail.setAmount(existingAmtForAccDetail.add(newAmountForAccDeatil));

		} else {

			BillAccountDetailV2 accountDetail = BillAccountDetailV2.builder()
					.demandDetailId(demandDetail.getId())
					.tenantId(demandDetail.getTenantId())
					.id(UUID.randomUUID().toString())
					.adjustedAmount(BigDecimal.ZERO)
					.taxHeadCode(taxHead.getCode())
					.amount(newAmountForAccDeatil)
					.order(taxHead.getOrder())
					.billDetailId(billDetailId)
					.build();

			taxCodeAccDetailMap.put(taxHead.getCode(), accountDetail);
		}
	}

	/**
	 * Fetches the tax-head master data for the given tax-head codes
	 * 
	 * @param demands  list of demands for which tax-heads needs to searched
	 * @param tenantId tenant-id of the request
	 * @param info     RequestInfo object
	 * @return returns a map of tax-head code as key and tax-head object as value
	 */
	private Map<String, TaxHeadMaster> getTaxHeadMaster(Set<String> taxHeadCodes, String tenantId, RequestInfo info) {

		TaxHeadMasterCriteria taxHeadCriteria = TaxHeadMasterCriteria.builder().tenantId(tenantId).code(taxHeadCodes)
				.build();
		List<TaxHeadMaster> taxHeads = taxHeadService.getTaxHeads(taxHeadCriteria, info).getTaxHeadMasters();

		if (taxHeads.isEmpty())
			throw new CustomException("EG_BS_TAXHEADCODE_EMPTY", "No taxhead masters found for the given codes");

		return taxHeads.stream().collect(Collectors.toMap(TaxHeadMaster::getCode, Function.identity()));
	}


	/**
	 * To Fetch the businessServiceDetail master based on the business codes
	 * 
	 * @param businessService
	 * @param tenantId
	 * @param requestInfo
	 * @return returns a map with business code and businessDetail object
	 */
	private Map<String, BusinessServiceDetail> getBusinessService(Set<String> businessService, String tenantId, RequestInfo requestInfo) {
		List<BusinessServiceDetail> businessServiceDetails = businessServDetailService.searchBusinessServiceDetails(BusinessServiceDetailCriteria.builder().businessService(businessService).tenantId(tenantId).build(), requestInfo)
				.getBusinessServiceDetails();
		return businessServiceDetails.stream().collect(Collectors.toMap(BusinessServiceDetail::getCode, Function.identity()));
	}

	public BillResponseV2 getBillResponse(List<BillV2> bills) {
		BillResponseV2 billResponse = new BillResponseV2();
		billResponse.setBill(bills);
		return billResponse;
	}

	/**
	 * Publishes the bill request to kafka topic and returns bill response
	 * 
	 * @param billRequest
	 * @return billResponse object containing bills from the request
	 */
	public BillResponseV2 sendBillToKafka(BillRequestV2 billRequest) {

		try {
			kafkaTemplate.send(appProps.getCreateBillTopic(), appProps.getCreateBillTopicKey(), billRequest);
		} catch (Exception e) {
			log.debug("BillService createAsync:" + e);
			throw new CustomException("EGBS_BILL_SAVE_ERROR", e.getMessage());

		}
		return getBillResponse(billRequest.getBills());
	}

	public BillResponseV2 create(BillRequestV2 billRequest) {

		//if (!CollectionUtils.isEmpty(billRequest.getBills()))
		//billRepository.saveBill(billRequest);
		return getBillResponse(billRequest.getBills());
	}

	public static List<String> getOwnerFieldsPlainAccessList() {

		if (ownerPlainRequestFieldsList == null) {

			ownerPlainRequestFieldsList = new ArrayList<>();
			ownerPlainRequestFieldsList.add("mobileNumber");
			ownerPlainRequestFieldsList.add("guardian");
			ownerPlainRequestFieldsList.add("fatherOrHusbandName");
			ownerPlainRequestFieldsList.add("correspondenceAddress");
			ownerPlainRequestFieldsList.add("userName");
			ownerPlainRequestFieldsList.add("name");
			ownerPlainRequestFieldsList.add("gender");
			ownerPlainRequestFieldsList.add("permanentAddress");
		}
		return ownerPlainRequestFieldsList;
	}

}
