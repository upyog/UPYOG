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
import static org.egov.demand.util.Constants.Q1FlatDays;
import static org.egov.demand.util.Constants.Q2FlatDays;
import static org.egov.demand.util.Constants.Q3FlatDays;
import static org.egov.demand.util.Constants.H1FlatDays;
import static org.egov.demand.util.Constants.InterestPrecentage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.egov.common.contract.request.PlainAccessRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.demand.config.ApplicationProperties;
import org.egov.demand.model.AssessmentResponseV2;
import org.egov.demand.model.AssessmentV2;
import org.egov.demand.model.AssessmentV2.ModeOfPayment;
import org.egov.demand.model.Bill;
import org.egov.demand.model.BillAccountDetailV2;
import org.egov.demand.model.BillDetailV2;
import org.egov.demand.model.BillSearchCriteria;
import org.egov.demand.model.BillV2;
import org.egov.demand.model.BillV2.BillStatus;
import org.egov.demand.model.BusinessServiceDetail;
import org.egov.demand.model.CollectionPaymentCriteriaRequest;
import org.egov.demand.model.Demand;
import org.egov.demand.model.DemandCriteria;
import org.egov.demand.model.DemandDetail;
import org.egov.demand.model.GenerateBillCriteria;
import org.egov.demand.model.InterestAndPenalty;
import org.egov.demand.model.ModeOfPaymentDetails;
import org.egov.demand.model.ModeOfPaymentDetails.TxnPeriodEnum;
import org.egov.demand.model.TaxAndPayment;
import org.egov.demand.model.TaxHeadMaster;
import org.egov.demand.model.TaxHeadMasterCriteria;
import org.egov.demand.model.Transaction;
import org.egov.demand.model.Transaction.TxnStatusEnum;
import org.egov.demand.model.TransactionCreateResponse;
import org.egov.demand.model.TransactionRequest;
import org.egov.demand.model.TransactionResponse;
import org.egov.demand.model.TransactionUser;
import org.egov.demand.model.UpdateBillCriteria;
import org.egov.demand.model.UpdateBillRequest;
import org.egov.demand.producer.Producer;
import org.egov.demand.repository.BillRepositoryV2;
import org.egov.demand.repository.IdGenRepo;
import org.egov.demand.repository.ServiceRequestRepository;
import org.egov.demand.repository.querybuilder.BillQueryBuilder;
import org.egov.demand.util.Util;
import org.egov.demand.web.contract.BillRequestV2;
import org.egov.demand.web.contract.BillResponseV2;
import org.egov.demand.web.contract.BusinessServiceDetailCriteria;
import org.egov.demand.web.contract.DemandRequest;
import org.egov.demand.web.contract.Payment;
import org.egov.demand.web.contract.PaymentResponse;
import org.egov.demand.web.contract.PaymentSearchCriteria;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.core.status.Status;
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

	@Autowired
	private BillQueryBuilder billQueryBuilder;
	@Autowired
	private JdbcTemplate jdbcTemplate;

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

			throw new CustomException("EG_BS_CANCEL_BILL_ERROR",
					"Only one consumer code can be provided in the Cancel request");
		} else {
			int result = billRepository.updateBillStatus(cancelBillCriteria);
			sendNotificationForBillCancellation(updateBillRequest.getRequestInfo(), cancelBillCriteria);
			return result;
		}
	}

	private void sendNotificationForBillCancellation(RequestInfo requestInfo, UpdateBillCriteria cancelBillCriteria) {
		Set<String> consumerCodes = cancelBillCriteria.getConsumerCodes();
		if (CollectionUtils.isEmpty(consumerCodes))
			return;

		List<BillV2> bills = billRepository
				.findBill(BillSearchCriteria.builder().service(cancelBillCriteria.getBusinessService())
						.tenantId(cancelBillCriteria.getTenantId()).consumerCode(consumerCodes).build());

		if (CollectionUtils.isEmpty(bills))
			return;

		BillRequestV2 req = BillRequestV2.builder().bills(bills).requestInfo(requestInfo).build();
		producer.push(billCancelTopic, req);

	}

	/**
	 * Fetches the bill for given parameters
	 * 
	 * Searches the respective bill if nothing found then generates bill for the
	 * same criteria if bill found then checks the validity of the bill return the
	 * bill if valid else update the demands belonging to the bill then generate a
	 * new bill
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
		
		if(!CollectionUtils.isEmpty(bills))
		{
			bills.stream().forEach(b->b.getBillDetails().forEach(bd->
			{
				Map<String, Object> additionalDetails = mapper.convertValue(bd.getAdditionalDetails(), Map.class);
				List<ModeOfPaymentDetails> modeOfPaymentDetails=mapper.convertValue(additionalDetails.get("paymentModeDetails"), List.class);
				bd.setModeOfPaymentDetails(modeOfPaymentDetails);
			}
					));
		}

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
			billCriteria.getConsumerCode()
			.addAll(demands.stream().map(Demand::getConsumerCode).collect(Collectors.toSet()));
		}

		log.debug("fetchBill--------going to generate new bill-------------------");
		Map<String, BillV2> consumerCodeAndBillMap = bills.stream()
				.collect(Collectors.toMap(BillV2::getConsumerCode, Function.identity()));
		billCriteria.getConsumerCode().addAll(consumerCodeAndBillMap.keySet());
		/*
		 * Collecting the businessService code and the list of consumer codes for those
		 * service codes whose demands needs to be updated.
		 * 
		 * grouping by service code and collecting the list of consumerCodes against the
		 * service code
		 */
		List<String> cosnumerCodesNotFoundInBill = new ArrayList<>(billCriteria.getConsumerCode());
		Set<String> cosnumerCodesToBeExpired = new HashSet<>();
		List<BillV2> billsToBeReturned = new ArrayList<>();
		Boolean isBillExpired = false;
		Map<String, BillV2> oldBillToBeExpired=new HashMap<>();

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
			else {
				if(bill.getBillDetails().get(0).isPreviousYearAssesment())
					oldBillToBeExpired.put(bill.getId(), bill);
				cosnumerCodesToBeExpired.add(bill.getConsumerCode());

			}

			cosnumerCodesNotFoundInBill.remove(entry.getKey());
			isBillExpired = false;
		}

		/*
		 * If none of the billDetails in the bills needs to be updated then return the
		 * search result
		 */
		if (CollectionUtils.isEmpty(cosnumerCodesToBeExpired) && CollectionUtils.isEmpty(cosnumerCodesNotFoundInBill)) {
			// BillSearchCriteria bscr = billCriteria.toBillSearchCriteria();
			if (billCriteria.getAllBill())
				return res;
			else {
				List<BillV2> billToBeReturned = res.getBill();

				billToBeReturned = billToBeReturned.stream().filter(x -> x.getStatus().equals(BillStatus.ACTIVE))
						.collect(Collectors.toList());
				res.setBill(null);
				res.setBill(billToBeReturned);
				List<BillDetailV2> updatedbills = null;
				if(billToBeReturned!=null && !billToBeReturned.isEmpty())
				{
					if(billsToBeReturned.get(0).getBillDetails().get(0).getPaymentPeriod().equalsIgnoreCase("YR"))
						return res;

					else if(!billToBeReturned.get(0).getBillDetails().get(0).isPreviousYearAssesment() && billToBeReturned.get(0).getBillDetails().get(0).getAdjusmentfromdate()!=null) {
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
						BigDecimal totalAmount;
						String date = billToBeReturned.get(0).getBillDetails().get(0).getAdjusmentfromdate();
						LocalDate endDate = LocalDate.now();
						date = date.replace("-"," ");
						LocalDate startDate = LocalDate.parse(date, dtf);
						BigDecimal daysdiff=new BigDecimal(ChronoUnit.DAYS.between(startDate, endDate));
						BigDecimal interestonamount=billToBeReturned.get(0).getBillDetails().get(0).getTotalAmountForIntCal();
						BigDecimal extraNoOfDays=daysdiff.subtract(new BigDecimal(billToBeReturned.get(0).getBillDetails().get(0).getInterestfornoofdays()));
						BigDecimal interestamount=interestonamount.multiply(extraNoOfDays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));

						if(extraNoOfDays.compareTo(new BigDecimal(0))>0 && interestamount.compareTo(new BigDecimal(0))>0) {
							totalAmount=interestamount.add(billToBeReturned.get(0).getBillDetails().get(0).getAmount()).add(new BigDecimal(10));
							totalAmount= totalAmount.setScale(0,RoundingMode.HALF_UP);
							extraNoOfDays = extraNoOfDays.add(new BigDecimal(billToBeReturned.get(0).getBillDetails().get(0).getInterestfornoofdays()));

							List<Object> preparedStmtList = new ArrayList<>();
							preparedStmtList.add(interestamount);
							preparedStmtList.add(extraNoOfDays);
							preparedStmtList.add(totalAmount);
							preparedStmtList.add(billToBeReturned.get(0).getBillDetails().get(0).getId());

							jdbcTemplate.update(BillQueryBuilder.BILL_DETAIL_UPDATE_BASE_QUERY, preparedStmtList.toArray());
							updatedbills=billToBeReturned.get(0).getBillDetails();
							updatedbills.get(0).setAmount(totalAmount);
							updatedbills.get(0).setInterestfornoofdays(Integer.parseInt(extraNoOfDays.toString()));
							updatedbills.get(0).setInterestonamount(interestonamount);
							billToBeReturned.get(0).setTotalAmount(totalAmount);
							billToBeReturned.get(0).setBillDetails(updatedbills);

							res.setBill(billToBeReturned);							
							return res;
						}
					}
				}
				return res;
			}
		}

		else {

			billCriteria.getConsumerCode().retainAll(cosnumerCodesToBeExpired);
			billCriteria.getConsumerCode().addAll(cosnumerCodesNotFoundInBill);
			updateDemandsForexpiredBillDetails(billCriteria.getBusinessService(), billCriteria.getConsumerCode(),
					billCriteria.getTenantId(), requestInfoWrapper);

			billRepository.updateBillStatus(UpdateBillCriteria.builder().statusToBeUpdated(BillStatus.EXPIRED)
					.businessService(billCriteria.getBusinessService()).consumerCodes(cosnumerCodesToBeExpired)
					.tenantId(billCriteria.getTenantId()).build());

			for(String s: cosnumerCodesToBeExpired) {
				BillV2 b =   consumerCodeAndBillMap.get(s);
				if(oldBillToBeExpired.containsKey(b.getId())) {

					DemandCriteria OlddemandCriteria = DemandCriteria.builder()
							.businessService(billCriteria.getBusinessService())
							.tenantId(billCriteria.getTenantId())
							.consumerCode(Stream.of(b.getConsumerCode()).collect(Collectors.toSet()))
							.isPaymentCompleted(false).receiptRequired(false).demandId(Stream.of(b.getBillDetails().get(0).getDemandId()).collect(Collectors.toSet())).build();


					DemandCriteria demandCriteriaNewDemand = DemandCriteria.builder()
							.status(org.egov.demand.model.Demand.StatusEnum.ACTIVE.toString())
							.businessService(billCriteria.getBusinessService())
							.tenantId(billCriteria.getTenantId())
							.consumerCode(Stream.of(b.getConsumerCode()).collect(Collectors.toSet()))
							.isPaymentCompleted(false).receiptRequired(false).build();

					/* Fetching demands for the given bill search criteria */
					List<Demand> demandsOld = demandService.getDemands(OlddemandCriteria, requestInfo);
					List<Demand> demandsnew = demandService.getDemands(demandCriteriaNewDemand, requestInfo);

					if(demandsnew.get(0).getTaxPeriodFrom()<=demandsOld.get(0).getTaxPeriodFrom()) {
						billCriteria.getConsumerCode().remove(s);
					}

				}
			}

			BillResponseV2 finalResponse =null;
			if(null!=billCriteria.getConsumerCode() && !billCriteria.getConsumerCode().isEmpty()) {
				finalResponse = generateBill(billCriteria, requestInfo);
				if(null!=finalResponse)
					billsToBeReturned.addAll(finalResponse.getBill());
			}

			// gen bill returns immutable empty list incase of zero bills
			for(Map.Entry<String, BillV2> b: oldBillToBeExpired.entrySet()) {
				billsToBeReturned.add(b.getValue());
			}

			if(null==finalResponse)
				finalResponse = new BillResponseV2();
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
	private void updateDemandsForexpiredBillDetails(String businessService, Set<String> consumerCodesTobeUpdated,
			String tenantId, RequestInfoWrapper requestInfoWrapper) {

		Map<String, String> serviceUrlMap = appProps.getBusinessCodeAndDemandUpdateUrlMap();

		String url = serviceUrlMap.get(businessService);
		if (StringUtils.isEmpty(url)) {

			log.info(URL_NOT_CONFIGURED_FOR_DEMAND_UPDATE_KEY,
					URL_NOT_CONFIGURED_FOR_DEMAND_UPDATE_MSG.replace(URL_NOT_CONFIGURED_REPLACE_TEXT, businessService));
			return;
		}

		StringBuilder completeUrl = new StringBuilder(url).append(URL_PARAMS_FOR_SERVICE_BASED_DEMAND_APIS
				.replace(TENANTID_REPLACE_TEXT, tenantId).replace(CONSUMERCODES_REPLACE_TEXT,
						consumerCodesTobeUpdated.toString().replace("[", "").replace("]", "")));

		completeUrl.append(URL_PARAM_SEPERATOR).append(BUSINESS_SERVICE_URL_PARAMETER).append(businessService);
		log.info("the url : " + completeUrl);
		restRepository.fetchResult(completeUrl.toString(), requestInfoWrapper);
	}

	/**
	 * Searches the bills from DB for given criteria and enriches them with
	 * TaxAndPayments array
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

	public BillResponseV2 noDues(BillSearchCriteria billCriteria, RequestInfo requestInfo) {

		List<BillV2> bills = billRepository.findBill(billCriteria);
		boolean finalbill = false;
		for (BillV2 billv2 : bills) {
			if (billv2.getStatus().equals(BillStatus.ACTIVE)) {
				Long date = billv2.getBillDate();
				Date expiry = new Date((date));
				SimpleDateFormat date_format = new SimpleDateFormat("yyyy-mm-dd");
				String date_string = date_format.format(expiry);
				BigDecimal ammount = BigDecimal.ZERO;
				for (BillDetailV2 billdeatilv2 : billv2.getBillDetails()) {
					ammount = ammount.add(billdeatilv2.getAmount());
				}
				throw new CustomException("BILL_FOUND",
						"Please pay your bills for year " + date_string.split("-")[0] + " and ammount " + ammount + "");
			} else if (billv2.getStatus().equals(BillStatus.PAID)) {
				for (BillDetailV2 billdeatilv2 : billv2.getBillDetails()) {
					if (billdeatilv2.getPaymentPeriod().equalsIgnoreCase("Q4")) {
						finalbill = true;
					} else if (billdeatilv2.getPaymentPeriod().equalsIgnoreCase("H2")) {
						finalbill = true;
					}
				}
				if (!finalbill)
					throw new CustomException("BILL_FOUND",
							"Please pay your Quaterly or Halfyearly bills for this year");
			}
		}

		return BillResponseV2.builder().resposneInfo(responseFactory.getResponseInfo(requestInfo, HttpStatus.OK))
				.build();
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
				.businessService(billCriteria.getBusinessService()).mobileNumber(billCriteria.getMobileNumber())
				.tenantId(billCriteria.getTenantId()).email(billCriteria.getEmail()).consumerCode(consumerCodes)
				.isPaymentCompleted(false).receiptRequired(false).demandId(demandIds).build();

		/* Fetching demands for the given bill search criteria */
		List<Demand> demands = demandService.getDemands(demandCriteria, requestInfo);
		List<Demand> udpdatedDemands = new ArrayList<>();
		/*
		 * for(Demand d:demands) {
		 * 
		 * Date startDate = new Date(d.getTaxPeriodFrom()); Date endDate = new
		 * Date(d.getTaxPeriodTo()); Calendar c = Calendar.getInstance();
		 * c.setTime(startDate); Integer assesmentDoneForYearStart =
		 * c.get(Calendar.YEAR); c = Calendar.getInstance(); c.setTime(endDate); Integer
		 * assesmentDoneForYearEnd = c.get(Calendar.YEAR);
		 * if(Long.parseLong(appProps.getFinYearStart().toString())==
		 * assesmentDoneForYearStart &&
		 * Long.parseLong(appProps.getFinYearEnd().toString())==assesmentDoneForYearEnd)
		 * {
		 * 
		 * udpdatedDemands.add(d); } }
		 */
		List<BillV2> bills;

		if (!demands.isEmpty() )
			bills = prepareBill(demands, requestInfo, billCriteria.getModeOfPayment());
		else
			return getBillResponse(Collections.emptyList());

		BillRequestV2 billRequest = BillRequestV2.builder().bills(bills).requestInfo(requestInfo).build();
		// kafkaTemplate.send(notifTopicName, null, billRequest);
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
		PlainAccessRequest plainAccessRequest = PlainAccessRequest.builder().plainRequestFields(plainRequestFieldsList)
				.recordId(uuid).build();
		requestInfo.setPlainAccessRequest(plainAccessRequest);

		UserSearchRequest userSearchRequest = UserSearchRequest.builder()
				.uuid(Stream.of(uuid).collect(Collectors.toSet())).requestInfo(requestInfo).build();
		String userUri = appProps.getUserServiceHostName().concat(appProps.getUserServiceSearchPath());
		List<User> payer = mapper
				.convertValue(restRepository.fetchResult(userUri, userSearchRequest), UserResponse.class).getUser();

		requestInfo.setPlainAccessRequest(apiPlainAccessRequest);

		return payer.get(0);
	}

	/**
	 * Prepares the bill object from the list of given demands
	 * 
	 * @param demands     demands for which bill should be generated
	 * @param requestInfo
	 * @param string
	 * @return
	 */
	private List<BillV2> prepareBill(List<Demand> demands, RequestInfo requestInfo, String modeOfPayment) {

		System.out.println("modeOfPayment::" + modeOfPayment);
		List<BillV2> bills = new ArrayList<>();
		User payer = null != demands.get(0).getPayer() ? demands.get(0).getPayer() : new User();
		if (payer.getUuid() != null)
			payer = getUnmaskedUser(requestInfo, payer.getUuid());

		Map<String, List<Demand>> tenatIdDemandsList = demands.stream()
				.collect(Collectors.groupingBy(Demand::getTenantId));
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
			 * Grouping the demands by their consumer code and generating a bill for each
			 * consumer code
			 */
			Map<String, List<Demand>> consumerCodeAndDemandsMap = demandForOneTenant.stream()
					.collect(Collectors.groupingBy(Demand::getConsumerCode));

			for (Entry<String, List<Demand>> consumerCodeAndDemands : consumerCodeAndDemandsMap.entrySet()) {

				BigDecimal billAmount = BigDecimal.ZERO;
				List<BillDetailV2> billDetails = new ArrayList<>();

				String consumerCode = consumerCodeAndDemands.getKey();
				BigDecimal minimumAmtPayableForBill = BigDecimal.ZERO;
				List<Demand> demandsForSingleCode = consumerCodeAndDemands.getValue();
				BusinessServiceDetail business = businessMap.get(demandsForSingleCode.get(0).getBusinessService());

				String billId = UUID.randomUUID().toString();
				String billNumber = getBillNumbers(requestInfo, tenantId,
						demandForOneTenant.get(0).getBusinessService(), 1).get(0);

				for (Demand demand : demandsForSingleCode) {

					minimumAmtPayableForBill = minimumAmtPayableForBill.add(demand.getMinimumAmountPayable());
					String billDetailId = UUID.randomUUID().toString();
					BillDetailV2 billDetail = getBillDetailForDemand(demand, taxHeadMap, billDetailId, requestInfo,
							modeOfPayment);
					billDetail.setBillId(billId);
					billDetail.setId(billDetailId);
					billDetails.add(billDetail);
					billAmount = billAmount.add(billDetail.getAmount());
				}

				if (billAmount.compareTo(BigDecimal.ZERO) >= 0) {

					BillV2 bill = BillV2.builder().auditDetails(util.getAuditDetail(requestInfo))
							.payerAddress(payer.getPermanentAddress()).mobileNumber(payer.getMobileNumber())
							.billDate(System.currentTimeMillis()).businessService(business.getCode())
							.payerName(payer.getName()).consumerCode(consumerCode).status(BillStatus.ACTIVE)
							.billDetails(billDetails).totalAmount(billAmount).userId(payer.getUuid())
							.billNumber(billNumber).tenantId(tenantId).id(billId).build();

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
			billNumberFormat = billNumberFormat.replace(appProps.getTenantIdReplaceString(),
					"_".concat(tenantId.split("\\.")[1]));
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
	private BillDetailV2 getBillDetailForDemand(Demand demand, Map<String, TaxHeadMaster> taxHeadMap,
			String billDetailId, RequestInfo requestInfo, String PaymentType) {
		String modeOfpayment = null;
		Long startPeriod = demand.getTaxPeriodFrom();
		Long endPeriod = demand.getTaxPeriodTo();
		String tenantId = demand.getTenantId();
		String propertyId = demand.getConsumerCode();

		ModeOfPaymentDetails mpdObj = null;
		List<ModeOfPaymentDetails> mpdList = null;
		BigDecimal totalAmountForDemand = BigDecimal.ZERO;
		BigDecimal pastDue = BigDecimal.ZERO;
		boolean billsFound = false;

		/*
		 * Map to store the bill account detail object with TaxHead code To accommodate
		 * conversion of multiple DemandDetails with same tax head code to single
		 * BillAccountDetail
		 */
		Map<String, BillAccountDetailV2> taxCodeAccountdetailMap = new HashMap<>();

		for (DemandDetail demandDetail : demand.getDemandDetails()) {

			TaxHeadMaster taxHead = taxHeadMap.get(demandDetail.getTaxHeadMasterCode());
			BigDecimal amountForAccDeatil = demandDetail.getTaxAmount();// .subtract(demandDetail.getCollectionAmount());

			addOrUpdateBillAccDetailInTaxCodeAccDetailMap(taxCodeAccountdetailMap, demandDetail, taxHead, billDetailId);

			/* Total tax and collection for the whole demand/bill-detail */
			totalAmountForDemand = totalAmountForDemand.add(amountForAccDeatil);
			if (taxHead.getCode().equalsIgnoreCase("PT_PASTDUE_CARRYFORWARD"))
				pastDue = pastDue.add(demandDetail.getTaxAmount());
		}
		// totalAmountForDemand = BigDecimal.ZERO;
		totalAmountForDemand = totalAmountForDemand.setScale(0, RoundingMode.HALF_UP);
		if (totalAmountForDemand.compareTo(new BigDecimal(0)) == 0) {

			return BillDetailV2.builder().billAccountDetails(new ArrayList<>(taxCodeAccountdetailMap.values()))
					.amount(totalAmountForDemand).expiryDate(endPeriod).demandId(demand.getId()).fromPeriod(startPeriod)
					.toPeriod(endPeriod).tenantId(tenantId).paymentPeriod("YR").modeOfPaymentDetails(mpdList)
					.additionalDetails(demand.getAdditionalDetails()).build();
		}

		// PAST DUE CALCULATION
		totalAmountForDemand = totalAmountForDemand.subtract(pastDue);

		// Getting Financial year from Demand
		Date startDate = new Date(startPeriod);
		Date endDate = new Date(endPeriod);
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		StringBuilder financialYearFromDemand = new StringBuilder();

		// Assesment Done for year
		Integer assesmentDoneForYearStart = c.get(Calendar.YEAR);

		financialYearFromDemand.append(c.get(Calendar.YEAR));
		financialYearFromDemand.append("-");
		c = Calendar.getInstance();
		c.setTime(endDate);
		Integer assesmentDoneForYearEnd = c.get(Calendar.YEAR);
		// current Date
		Date currentDate = new Date(System.currentTimeMillis());
		Calendar crd = Calendar.getInstance();
		crd.setTime(currentDate);
		Integer cuurentMonth = crd.get(Calendar.MONTH) + 1;
		//cuurentMonth =7;
		// currentYear reference the year for which assesment is done to changed to
		// different variable
		Integer currentyear = c.get(Calendar.YEAR);

		Integer currentYearOfAssesment = crd.get(Calendar.YEAR);
		// get current financial year
		currentyear = assesmentDoneForYearStart;
		boolean previousYear = false;

		if (appProps.getFinYearStart() > assesmentDoneForYearStart
				&& appProps.getFinYearEnd() > assesmentDoneForYearEnd) {
			cuurentMonth = 3;
			previousYear = true;

		}
		//For Testing to be removed
		//previousYear=false;
		//cuurentMonth=8;
		Integer nextYear = assesmentDoneForYearEnd;

		Integer b = c.get(Calendar.YEAR);
		financialYearFromDemand.append(b.toString().substring(2, 4));

		String assesmentUrl = new StringBuilder().append(appProps.getAssessmentServiceHost())
				.append(appProps.getAssessmentSearchEndpoint()).append(URL_PARAMS_SEPARATER).append("propertyIds=")
				.append(propertyId).append(URL_PARAM_SEPERATOR).append("tenantId=").append(tenantId).toString();

		String transactiontUrl = new StringBuilder().append(appProps.getPgSeriviceHost())
				.append(appProps.getPgSeriviceEndpoint()).append(URL_PARAMS_SEPARATER).append("billId=").toString();

		/*
		 * StringBuilder collectionServiceUrl = new
		 * StringBuilder().append(appProps.getCollectionSeriviceHost()).append(appProps.
		 * getCollectionSeriviceSearch()) .append(URL_PARAMS_SEPARATER)
		 * .append("tenantId=").append(tenantId)
		 * .append(URL_PARAM_SEPERATOR).append("billIds=");
		 */

		TransactionRequest transactionRequest = null;
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		AssessmentResponseV2 assessmentResponseV2 = restTemplate.postForObject(assesmentUrl, requestInfoWrapper,
				AssessmentResponseV2.class);
		AssessmentV2 assessmentV2 = null;
		if (!assessmentResponseV2.getAssessments().isEmpty()) {
			assessmentV2 = assessmentResponseV2.getAssessments().stream()
					.filter(t -> t.getFinancialYear().equalsIgnoreCase(financialYearFromDemand.toString()))
					.collect(Collectors.toList()).get(0);
			if (null == assessmentV2.getModeOfPayment() || StringUtils.isEmpty(assessmentV2.getModeOfPayment())) {
				throw new CustomException("INVALID_MODE_OF_PAYMENT", "Mode Of Payment Not found for the assesment");

			}

		}

		System.out.println("modeOfpayment:" + PaymentType);
		String Paymentmode;
		if (assessmentV2 != null)
			Paymentmode = assessmentV2.getModeOfPayment().toString();
		else
			Paymentmode = PaymentType;

		Set<Integer> q1 = new HashSet<>(Arrays.asList(4, 5, 6));
		Set<Integer> q2 = new HashSet<>(Arrays.asList(7, 8, 9));
		Set<Integer> q3 = new HashSet<>(Arrays.asList(10, 11, 12));
		Set<Integer> q4 = new HashSet<>(Arrays.asList(1, 2, 3));

		Set<Integer> h1 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8, 9));
		Set<Integer> h2 = new HashSet<>(Arrays.asList(10, 11, 12, 1, 2, 3));

		String paymentPeriod = null;
		String expiryDate = null;
		// BigDecimal newTotalAmountForModeOfPayment = new BigDecimal(0);

		BillSearchCriteria billCriteria = new BillSearchCriteria();
		billCriteria.setTenantId(demand.getTenantId());
		billCriteria.setDemandId(demand.getId());
		// billCriteria.setRetrieveOldest(true);
		List<Transaction> transactionForQuaterBills = new ArrayList<>();
		List<Transaction> transactionForHalfYearBills = new ArrayList<>();
		Map<String, String> quaterCheckMap = null;
		Map<String, BigDecimal> failedtransactionMapForQuater = new HashMap<>();
		Map<String, BigDecimal> failedtransactionMapForHalfYearly = new HashMap<>();

		Map<String, BigDecimal> successtransactionMapForQuater = new HashMap<>();
		Map<String, BigDecimal> successtransactionMapForHalfyearly = new HashMap<>();

		Map<String, BigDecimal> alltransactionForQuater = new HashMap<>();
		Map<String, BigDecimal> alltransactionForHalfYearly = new HashMap<>();

		BigDecimal amountforquaterly = totalAmountForDemand.divide(new BigDecimal(4));
		amountforquaterly = amountforquaterly.setScale(0, RoundingMode.HALF_UP);
		BigDecimal ammountforhalfyearly = totalAmountForDemand.divide(new BigDecimal(2));
		ammountforhalfyearly = ammountforhalfyearly.setScale(0, RoundingMode.HALF_UP);
		billCriteria.setSearchAllForDemand(true);
		List<BillV2> bills = billRepository.findBill(billCriteria);
		BigDecimal failedBillAmount = new BigDecimal(0);
		BigDecimal successBillAmount = new BigDecimal(0);
		BigDecimal paidBillAmount = BigDecimal.ZERO;
		BigDecimal count = BigDecimal.ZERO;
		BigDecimal zeroAmount = BigDecimal.ZERO;
		BigDecimal interestPercentOntaxAmount = BigDecimal.ZERO;
		BigDecimal divideValue = new BigDecimal(100);
		BigDecimal dayDifference = BigDecimal.ZERO;
		List<String> quaterly = new ArrayList<String>();
		List<String> halfyearly = new ArrayList<String>();
		InterestAndPenalty inp = null;
		BigDecimal advancedBillAmount = demand.getAdvanceAmount();// Will Be coming from Demand
		if(advancedBillAmount==null)
			advancedBillAmount=BigDecimal.ZERO;
		mpdList = new ArrayList<>();
		if (null != bills) {
			// bills =
			// bills.stream().sorted((x,y)->y.getAuditDetails().getCreatedTime().compareTo(x.getAuditDetails().getCreatedTime())).collect(Collectors.toList());
			quaterCheckMap = new HashMap<>();
			billsFound = true;
			mpdList = new ArrayList<>();
			for (BillV2 bl : bills) {
				StringBuilder collectionServiceUrl = new StringBuilder().append(appProps.getCollectionSeriviceHost())
						.append(appProps.getCollectionSeriviceSearch()).append(URL_PARAMS_SEPARATER).append("tenantId=")
						.append(tenantId).append(URL_PARAM_SEPERATOR).append("billIds=");
				transactionRequest = new TransactionRequest();
				transactionRequest.setRequestInfo(requestInfo);
				Transaction tr = new Transaction();
				tr.setBillId(bl.getId());
				transactionRequest.setTransaction(tr);

				CollectionPaymentCriteriaRequest pr = new CollectionPaymentCriteriaRequest();
				pr.setRequestInfo(requestInfo);
				Set<String> ids = new HashSet<>();
				PaymentSearchCriteria payment = new PaymentSearchCriteria();
				pr.setPayment(payment);
				ids.add(bl.getId());
				pr.getPayment().setBillIds(ids);

				String collectUrl = collectionServiceUrl.append(bl.getId()).toString();
				PaymentResponse pay = restTemplate.postForObject(collectUrl, pr, PaymentResponse.class);

				TransactionResponse tres = null;// restTemplate.postForObject(transactiontUrl+bl.getId(),
				// transactionRequest, TransactionResponse.class);
				List<Transaction> failedtransactions = null;// tres.getTransactions().stream().filter(t->t.getTxnStatus().equals(Transaction.TxnStatusEnum.FAILURE)||
				// t.getTxnStatus().equals(Transaction.TxnStatusEnum.PENDING)).collect(Collectors.toList());
				List<Transaction> successTransactions = null;// tres.getTransactions().stream().filter(t->t.getTxnStatus().equals(Transaction.TxnStatusEnum.SUCCESS)).collect(Collectors.toList());
				List<Transaction> alltrnasactions = null;// tres.getTransactions();

				// success
				if (null != pay && pay.getPayments() != null && !pay.getPayments().isEmpty()) {
					/*
					 * if(assessmentV2.getModeOfPayment().toString().equalsIgnoreCase(QUARTERLY)) {
					 * successtransactionMapForQuater.put(bl.getBillDetails().get(0).
					 * getPaymentPeriod(), pay.getPayments().get(0).getTotalAmountPaid()); }
					 * if(assessmentV2.getModeOfPayment().toString().equalsIgnoreCase(HALFYEARLY)) {
					 * successtransactionMapForHalfyearly.put(bl.getBillDetails().get(0).
					 * getPaymentPeriod(), pay.getPayments().get(0).getTotalAmountPaid()); }
					 */
					// zeroAmount is added for summing up paid amount to exact bill amount in a
					// situation where bill amount could be zero
					// or less than amountforquaterly so that after adding amountforquaterly we will
					// get the paid amount
					count = bl.getBillDetails().get(0).getAmount();

					if (Paymentmode.equalsIgnoreCase("QUARTERLY")) {
						if (count.compareTo(amountforquaterly) < 0)
							zeroAmount = amountforquaterly;
						else if (count.compareTo(amountforquaterly) >= 0)
							paidBillAmount = paidBillAmount.add(count);
						quaterly.add(bl.getBillDetails().get(0).getPaymentPeriod());
					} else if (Paymentmode.equalsIgnoreCase("HALFYEARLY")) {
						if (count.compareTo(ammountforhalfyearly) < 0)
							zeroAmount = zeroAmount.add(ammountforhalfyearly);
						else if (count.compareTo(ammountforhalfyearly) >= 0)
							paidBillAmount = paidBillAmount.add(count);
						halfyearly.add(bl.getBillDetails().get(0).getPaymentPeriod());
					}

					// else if(count.compareTo(amountforquaterly)==0)

					successBillAmount = pay.getPayments().get(0).getTotalAmountPaid();
					advancedBillAmount = successBillAmount.subtract(bl.getBillDetails().get(0).getAmount());
					if (advancedBillAmount.compareTo(BigDecimal.ZERO) == 0)
						advancedBillAmount = demand.getAdvanceAmount();
					if(advancedBillAmount==null)
						advancedBillAmount=BigDecimal.ZERO;
					paidBillAmount = paidBillAmount.add(zeroAmount);

					if (bl.getBillDetails().get(0).getPaymentPeriod().equalsIgnoreCase("Q1")) {
						String expiryDateQ1 = "30-06-" + currentyear;
						String startDateQ1 = "01-04-" + currentyear;
						mpdObj = new ModeOfPaymentDetails();
						mpdObj = getModeOfPaymentDetails(pay.getPayments().get(0).getTotalAmountPaid(), startDateQ1,
								expiryDateQ1, ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), pastDue);
						mpdObj.setPeriod(TxnPeriodEnum.QUARTER_1);
						mpdObj.setRemaingAdvance(advancedBillAmount);
						mpdList.add(mpdObj);
					} else if (bl.getBillDetails().get(0).getPaymentPeriod().equalsIgnoreCase("Q2")) {
						String expiryDateQ2 = "30-09-" + currentyear;
						String startDateQ2 = "01-07-" + currentyear;
						List<TxnPeriodEnum> periodEnums=new ArrayList<ModeOfPaymentDetails.TxnPeriodEnum>();
						periodEnums.add(TxnPeriodEnum.QUARTER_1);
						mpdObj = new ModeOfPaymentDetails();
						mpdObj = getModeOfPaymentDetails(pay.getPayments().get(0).getTotalAmountPaid(), startDateQ2,
								expiryDateQ2, ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
						mpdObj.setPeriod(TxnPeriodEnum.QUARTER_2);
						mpdObj.setAdjustedPeriods(periodEnums);
						mpdObj.setRemaingAdvance(advancedBillAmount);
						mpdList.add(mpdObj);
					} else if (bl.getBillDetails().get(0).getPaymentPeriod().equalsIgnoreCase("Q3")) {
						String startDateQ3 = "01-10-" + currentyear;
						String expiryDateQ3 = "31-12-" + currentyear;
						List<TxnPeriodEnum> periodEnums=new ArrayList<ModeOfPaymentDetails.TxnPeriodEnum>();
						periodEnums.add(TxnPeriodEnum.QUARTER_1);
						periodEnums.add(TxnPeriodEnum.QUARTER_2);
						mpdObj = new ModeOfPaymentDetails();
						mpdObj = getModeOfPaymentDetails(pay.getPayments().get(0).getTotalAmountPaid(), startDateQ3,
								expiryDateQ3, ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
						mpdObj.setPeriod(TxnPeriodEnum.QUARTER_3);
						mpdObj.setAdjustedPeriods(periodEnums);
						mpdObj.setRemaingAdvance(advancedBillAmount);
						mpdList.add(mpdObj);
					} else if (bl.getBillDetails().get(0).getPaymentPeriod().equalsIgnoreCase("H1")) {
						String startDateh1 = "01-04-" + currentyear;
						String expiryDateh1 = "30-09-" + currentyear;
						mpdObj = new ModeOfPaymentDetails();
						mpdObj = getModeOfPaymentDetails(pay.getPayments().get(0).getTotalAmountPaid(), startDateh1,
								expiryDateh1, ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
						mpdObj.setPeriod(TxnPeriodEnum.HALF_YEAR_1);
						mpdObj.setRemaingAdvance(advancedBillAmount);
						mpdList.add(mpdObj);
					}
				}


			}

		}
		// Bi totalFailedSuccess = failedBillAmount.add(successBillAmount);)

		switch (Paymentmode) {
		case QUARTERLY:
			// calcualtion of all quaterly ammount
			BigDecimal amountwithpastdue = BigDecimal.ZERO;
			BigDecimal allquaterammount = BigDecimal.ZERO;
			BigDecimal quaterlyammount = BigDecimal.ZERO;
			/*
			 * for(BigDecimal failedquatervalue:failedtransactionMapForQuater.values()) {
			 * failedquaterammount=failedquaterammount.add(failedquatervalue); }
			 * 
			 * for(BigDecimal allquatervalue:alltransactionForQuater.values()) {
			 * allquaterammount=allquaterammount.add(allquatervalue); }
			 * 
			 * allquaterammount=allquaterammount.add(failedquaterammount);
			 */

			if (q1.contains(cuurentMonth)) {
				paymentPeriod = Q1;
				expiryDate = "30-06-" + currentyear;
				// newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new
				// BigDecimal(4));
				totalAmountForDemand = amountforquaterly.add(pastDue);

				if (advancedBillAmount.compareTo(totalAmountForDemand) > 0) {
					advancedBillAmount = advancedBillAmount.subtract(totalAmountForDemand);
					totalAmountForDemand = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-04-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), pastDue);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_1);
					mpdObj.setPastAmount(pastDue);
					mpdList.add(mpdObj);
				} else if (totalAmountForDemand.compareTo(advancedBillAmount) > 0) {
					totalAmountForDemand = totalAmountForDemand.subtract(advancedBillAmount);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-04-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_PENDING.toString(), pastDue);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_1);
					mpdObj.setPastAmount(pastDue);
					mpdList.add(mpdObj);
				} else if (totalAmountForDemand.compareTo(advancedBillAmount) == 0) {
					totalAmountForDemand = new BigDecimal(0);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-04-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), pastDue);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_1);
					mpdObj.setPastAmount(pastDue);
					mpdList.add(mpdObj);
				}

				inp = getInterestPenalty( BigDecimal.ZERO,  null, null,null, null , BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);

			} else if (q2.contains(cuurentMonth)) {

				BigDecimal totalAMountForInterest = BigDecimal.ZERO;
				String calculationFinalDateForInterest=null;
				BigDecimal noFODays =  BigDecimal.ZERO;
				String firstDayAfterexpiryDateQ2 = "01-07-" + currentyear;
				BigDecimal totalInterestAmunt = BigDecimal.ZERO;
				Map<String, BigDecimal> interestMap = new HashMap<>();
				BigDecimal totalAmountForInterestCal=BigDecimal.ZERO;
				BigDecimal adjustedQ1Amount=BigDecimal.ZERO;

				if (!quaterly.contains("Q1")) {
					if(advancedBillAmount.compareTo(amountforquaterly)>0)
					{
						advancedBillAmount=advancedBillAmount.subtract(amountforquaterly);
						adjustedQ1Amount=BigDecimal.ZERO;
					}
					else if(advancedBillAmount.compareTo(amountforquaterly)<0)
					{
						adjustedQ1Amount=amountforquaterly.subtract(advancedBillAmount);
						advancedBillAmount=BigDecimal.ZERO;
					}
					else if(advancedBillAmount.compareTo(amountforquaterly)==0)
					{
						adjustedQ1Amount=BigDecimal.ZERO;
						advancedBillAmount=BigDecimal.ZERO;
					}

					calculationFinalDateForInterest = currentDateWithAssesmentYear(currentyear.toString());
					noFODays = getDateDifference(firstDayAfterexpiryDateQ2,currentDateWithAssesmentYear(currentyear.toString()));
					totalAMountForInterest = totalAMountForInterest.add(adjustedQ1Amount).multiply(noFODays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));
					totalAMountForInterest=totalAMountForInterest.setScale(2,2);
					totalAmountForInterestCal=adjustedQ1Amount;
					interestMap.put("Q1",totalAMountForInterest );

					amountwithpastdue = adjustedQ1Amount.add(pastDue);
					String expiryDateQ1 = "30-06-" + currentyear;
					String startDateQ1 = "01-04-" + currentyear;
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(amountwithpastdue, startDateQ1, expiryDateQ1,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_FAILED.toString(), pastDue);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_1);
					mpdObj.setPastAmount(pastDue);
					mpdObj.setInterestAmount(totalAMountForInterest);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				}

				paymentPeriod = Q2;
				expiryDate = "30-09-" + currentyear;
				String firstDayAfterexpiryDateQ1 = "01-07-" + currentyear;

				// 150
				// newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new
				// BigDecimal(4)).setScale(0,RoundingMode.HALF_UP);
				// 750
				quaterlyammount = ammountForTransactionperiod(Q1, amountforquaterly);
				// 750
				quaterlyammount = quaterlyammount.add(pastDue);
				if (quaterlyammount.compareTo(paidBillAmount) == 0) {
					quaterlyammount = BigDecimal.ZERO;
				} else if (quaterlyammount.compareTo(paidBillAmount) > 0) {
					quaterlyammount = quaterlyammount.subtract(paidBillAmount);
				}


				for(Map.Entry<String, BigDecimal> intmap : interestMap.entrySet()) {
					totalInterestAmunt= totalInterestAmunt.add(intmap.getValue());
				}

				totalInterestAmunt = 	totalInterestAmunt.setScale(0, RoundingMode.HALF_UP);
				//totalAmountForDemand = amountforquaterly.add(quaterlyammount).add(totalInterestAmunt);
				totalAmountForDemand = amountforquaterly.add(amountwithpastdue).add(totalInterestAmunt);
				inp = getInterestPenalty( totalInterestAmunt,  firstDayAfterexpiryDateQ1, financialYearFromDemand.toString() ,"Q2", "Q1" , new BigDecimal(InterestPrecentage),noFODays,totalAmountForInterestCal,previousYear);


				if (advancedBillAmount.compareTo(totalAmountForDemand) > 0) {
					advancedBillAmount = advancedBillAmount.subtract(totalAmountForDemand);
					totalAmountForDemand = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-07-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_2);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);

				} else if (totalAmountForDemand.compareTo(advancedBillAmount) > 0) {
					totalAmountForDemand = totalAmountForDemand.subtract(advancedBillAmount);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-07-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_PENDING.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_2);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				} else if (totalAmountForDemand.compareTo(advancedBillAmount) == 0) {
					totalAmountForDemand = new BigDecimal(0);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-07-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_2);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				}

			} else if (q3.contains(cuurentMonth)) {

				BigDecimal totalAMountForInterest = BigDecimal.ZERO;
				String calculationFinalDateForInterest=null;
				BigDecimal noFODays =  BigDecimal.ZERO;
				String firstDayAfterexpiryDateQ2 = "01-10-" + currentyear;
				BigDecimal totalInterestAmunt = BigDecimal.ZERO;
				BigDecimal totalAmountForInterestCal = BigDecimal.ZERO;
				inp = new InterestAndPenalty();
				Map<String, BigDecimal> interestMap = new HashMap<>();
				BigDecimal adjustedQ1Amount=BigDecimal.ZERO;
				BigDecimal adjustedQ2Amount=BigDecimal.ZERO;

				if (!quaterly.contains("Q2")) {
					if(!quaterly.contains("Q1")) {

						if(advancedBillAmount.compareTo(amountforquaterly)>0)
						{
							advancedBillAmount=advancedBillAmount.subtract(amountforquaterly);
							adjustedQ1Amount=BigDecimal.ZERO;
						}
						else if(advancedBillAmount.compareTo(amountforquaterly)<0)
						{
							adjustedQ1Amount=amountforquaterly.subtract(advancedBillAmount);
							advancedBillAmount=BigDecimal.ZERO;
						}
						else if(advancedBillAmount.compareTo(amountforquaterly)==0)
						{
							adjustedQ1Amount=BigDecimal.ZERO;
							advancedBillAmount=BigDecimal.ZERO;
						}

						totalAMountForInterest = totalAMountForInterest.add(adjustedQ1Amount).multiply(new BigDecimal(Q1FlatDays)).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));
						totalAMountForInterest=totalAMountForInterest.setScale(2,2);
						interestMap.put("Q1",totalAMountForInterest );
						totalAMountForInterest = BigDecimal.ZERO;

						amountwithpastdue = adjustedQ1Amount.add(pastDue);
						//90*150*0.15% add
						String expiryDateQ1 = "30-06-" + currentyear;
						String startDateQ1 = "01-04-" + currentyear;
						mpdObj = new ModeOfPaymentDetails();
						mpdObj = getModeOfPaymentDetails(amountwithpastdue, startDateQ1, expiryDateQ1,
								ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_FAILED.toString(), pastDue);
						mpdObj.setPeriod(TxnPeriodEnum.QUARTER_1);
						mpdObj.setPastAmount(pastDue);
						mpdObj.setInterestAmount(adjustedQ1Amount.multiply(new BigDecimal(Q1FlatDays)).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100))).setScale(2,2));
						mpdObj.setRemaingAdvance(advancedBillAmount);
						mpdList.add(mpdObj);
					}

					if(advancedBillAmount.compareTo(amountforquaterly)>0)
					{
						advancedBillAmount=advancedBillAmount.subtract(amountforquaterly);
						adjustedQ2Amount=BigDecimal.ZERO;
					}
					else if(advancedBillAmount.compareTo(amountforquaterly)<0)
					{
						adjustedQ2Amount=amountforquaterly.subtract(advancedBillAmount);
						advancedBillAmount=BigDecimal.ZERO;
					}
					else if(advancedBillAmount.compareTo(amountforquaterly)==0)
					{
						adjustedQ2Amount=BigDecimal.ZERO;
						advancedBillAmount=BigDecimal.ZERO;
					}

					calculationFinalDateForInterest = currentDateWithAssesmentYear(currentyear.toString());
					noFODays = 	getDateDifference(firstDayAfterexpiryDateQ2,currentDateWithAssesmentYear(currentyear.toString()));
					totalAMountForInterest = totalAMountForInterest.add(adjustedQ2Amount).multiply(noFODays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));
					totalAMountForInterest=totalAMountForInterest.setScale(2,2);
					interestMap.put("Q2",totalAMountForInterest );
					totalAmountForInterestCal = adjustedQ2Amount;

					String expiryDateQ2 = "30-09-" + currentyear;
					String startDateQ2 = "01-07-" + currentyear;
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(adjustedQ2Amount, startDateQ2, expiryDateQ2,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_FAILED.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_2);
					mpdObj.setInterestAmount(adjustedQ2Amount.multiply(noFODays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100))).setScale(2,2));
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);

				}

				paymentPeriod = Q3;
				expiryDate = "31-12-" + currentyear;
				quaterlyammount = ammountForTransactionperiod(Q2, amountforquaterly);
				quaterlyammount = quaterlyammount.add(pastDue);

				if (quaterlyammount.compareTo(paidBillAmount) == 0) {
					quaterlyammount = BigDecimal.ZERO;
				} else if (quaterlyammount.compareTo(paidBillAmount) > 0) {
					quaterlyammount = quaterlyammount.subtract(paidBillAmount);
				}
				//totalAMountForInterest = BigDecimal.ZERO;
				for(Map.Entry<String, BigDecimal> intmap : interestMap.entrySet()) {
					totalInterestAmunt= totalInterestAmunt.add(intmap.getValue());
				}

				totalInterestAmunt = 	totalInterestAmunt.setScale(0, RoundingMode.HALF_UP);
				//totalAmountForDemand = amountforquaterly.add(quaterlyammount).add(totalInterestAmunt);
				//Calculation is done interest 
				totalAmountForDemand = amountforquaterly.add(amountwithpastdue).add(adjustedQ2Amount).add(totalInterestAmunt);
				inp = getInterestPenalty( totalInterestAmunt,  firstDayAfterexpiryDateQ2, financialYearFromDemand.toString() ,"Q3", "Q2" , new BigDecimal(InterestPrecentage),noFODays,totalAmountForInterestCal,previousYear);


				if (advancedBillAmount.compareTo(totalAmountForDemand) > 0) {
					advancedBillAmount = advancedBillAmount.subtract(totalAmountForDemand);
					totalAmountForDemand = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-10-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_3);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);

				} else if (totalAmountForDemand.compareTo(advancedBillAmount) > 0) {
					totalAmountForDemand = totalAmountForDemand.subtract(advancedBillAmount);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-10-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_PENDING.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_3);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				} else if (totalAmountForDemand.compareTo(advancedBillAmount) == 0) {
					totalAmountForDemand = new BigDecimal(0);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-10-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_3);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				}

			} else if (q4.contains(cuurentMonth)) {

				BigDecimal totalAMountForInterest = BigDecimal.ZERO;
				String calculationFinalDateForInterest=null;
				BigDecimal noFODays =  BigDecimal.ZERO;
				String firstDayAfterexpiryDateQ3 = "01-01-" + nextYear;
				BigDecimal totalInterestAmunt = BigDecimal.ZERO;
				Map<String, BigDecimal> interestMap = new HashMap<>();
				BigDecimal totalAmountForInterestCal = BigDecimal.ZERO;
				BigDecimal adjustedQ1Amount=BigDecimal.ZERO;
				BigDecimal adjustedQ2Amount=BigDecimal.ZERO;
				BigDecimal adjustedQ3Amount=BigDecimal.ZERO;

				if(previousYear)
				{
					try {
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
						String pastStartDate = dateFormat.format(startPeriod);
						String pastEndDate = dateFormat.format(endPeriod);
						currentyear=Integer.parseInt(pastStartDate.split("/")[2]);
						nextYear=Integer.parseInt(pastEndDate.split("/")[2]);
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Exception is::"+e);
					}
				}

				if (!quaterly.contains("Q3")) {
					if(!quaterly.contains("Q2")) {
						if(!quaterly.contains("Q1")) {

							if(advancedBillAmount.compareTo(amountforquaterly)>0)
							{
								advancedBillAmount=advancedBillAmount.subtract(amountforquaterly);
								adjustedQ1Amount=BigDecimal.ZERO;
							}
							else if(advancedBillAmount.compareTo(amountforquaterly)<0)
							{
								adjustedQ1Amount=amountforquaterly.subtract(advancedBillAmount);
								advancedBillAmount=BigDecimal.ZERO;
							}
							else if(advancedBillAmount.compareTo(amountforquaterly)==0)
							{
								adjustedQ1Amount=BigDecimal.ZERO;
								advancedBillAmount=BigDecimal.ZERO;
							}

							totalAMountForInterest = totalAMountForInterest.add(adjustedQ1Amount).multiply(new BigDecimal(Q1FlatDays)).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));
							totalAMountForInterest=totalAMountForInterest.setScale(2,2);
							interestMap.put("Q1",totalAMountForInterest );
							totalAMountForInterest = BigDecimal.ZERO;

							amountwithpastdue = adjustedQ1Amount.add(pastDue);
							//90*150*0.15% add
							String expiryDateQ1 = "30-06-" + currentyear;
							String startDateQ1 = "01-04-" + currentyear;
							mpdObj = new ModeOfPaymentDetails();
							mpdObj = getModeOfPaymentDetails(amountwithpastdue, startDateQ1, expiryDateQ1,
									ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_FAILED.toString(), pastDue);
							mpdObj.setPeriod(TxnPeriodEnum.QUARTER_1);
							mpdObj.setPastAmount(pastDue);
							mpdObj.setInterestAmount(adjustedQ1Amount.multiply(new BigDecimal(Q1FlatDays)).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100))).setScale(2,2));
							mpdObj.setRemaingAdvance(advancedBillAmount);
							mpdList.add(mpdObj);
						}

						if(advancedBillAmount.compareTo(amountforquaterly)>0)
						{
							advancedBillAmount=advancedBillAmount.subtract(amountforquaterly);
							adjustedQ2Amount=BigDecimal.ZERO;
						}
						else if(advancedBillAmount.compareTo(amountforquaterly)<0)
						{
							adjustedQ2Amount=amountforquaterly.subtract(advancedBillAmount);
							advancedBillAmount=BigDecimal.ZERO;
						}
						else if(advancedBillAmount.compareTo(amountforquaterly)==0)
						{
							adjustedQ2Amount=BigDecimal.ZERO;
							advancedBillAmount=BigDecimal.ZERO;
						}

						totalAMountForInterest = totalAMountForInterest.add(adjustedQ2Amount).multiply(new BigDecimal(Q2FlatDays)).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));
						totalAMountForInterest=totalAMountForInterest.setScale(2,2);
						interestMap.put("Q2",totalAMountForInterest );
						totalAMountForInterest = BigDecimal.ZERO;

						String expiryDateQ2 = "30-09-" + currentyear;
						String startDateQ2 = "01-07-" + currentyear;
						mpdObj = new ModeOfPaymentDetails();
						mpdObj = getModeOfPaymentDetails(amountforquaterly, startDateQ2, expiryDateQ2,
								ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_FAILED.toString(), BigDecimal.ZERO);
						mpdObj.setPeriod(TxnPeriodEnum.QUARTER_2);
						mpdObj.setInterestAmount(adjustedQ2Amount.multiply(new BigDecimal(Q2FlatDays)).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100))).setScale(2,2));				
						mpdObj.setRemaingAdvance(advancedBillAmount);
						mpdList.add(mpdObj);
					}

					if(advancedBillAmount.compareTo(amountforquaterly)>0)
					{
						advancedBillAmount=advancedBillAmount.subtract(amountforquaterly);
						adjustedQ3Amount=BigDecimal.ZERO;
					}
					else if(advancedBillAmount.compareTo(amountforquaterly)<0)
					{
						adjustedQ3Amount=amountforquaterly.subtract(advancedBillAmount);
						advancedBillAmount=BigDecimal.ZERO;
					}
					else if(advancedBillAmount.compareTo(amountforquaterly)==0)
					{
						adjustedQ3Amount=BigDecimal.ZERO;
						advancedBillAmount=BigDecimal.ZERO;
					}

					calculationFinalDateForInterest = currentDateWithAssesmentYear(currentyear.toString());
					//"01-03-2025"
					noFODays = 	getDateDifference(firstDayAfterexpiryDateQ3,currentDateWithAssesmentYear(currentyear.toString()));
					if(previousYear) {
						noFODays=new BigDecimal(Q3FlatDays);
						totalAMountForInterest = totalAMountForInterest.add(adjustedQ3Amount).multiply(noFODays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));
						totalAMountForInterest=totalAMountForInterest.setScale(2,2);
						totalAmountForInterestCal=adjustedQ3Amount;

						String startDateQ3 = "01-10-" + currentyear;
						String expiryDateQ3 = "31-12-" + currentyear;
						mpdObj = new ModeOfPaymentDetails();
						mpdObj = getModeOfPaymentDetails(amountforquaterly, startDateQ3, expiryDateQ3,
								ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_FAILED.toString(), BigDecimal.ZERO);
						mpdObj.setPeriod(TxnPeriodEnum.QUARTER_3);
						mpdObj.setInterestAmount(adjustedQ3Amount.multiply(noFODays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100))).setScale(2,2));
						mpdObj.setRemaingAdvance(advancedBillAmount);
						mpdList.add(mpdObj);

					}

					else
						totalAMountForInterest = totalAMountForInterest.add(adjustedQ3Amount).multiply(noFODays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));
					totalAMountForInterest=totalAMountForInterest.setScale(2,2);
					interestMap.put("Q3",totalAMountForInterest );
					totalAmountForInterestCal=adjustedQ3Amount;

					String startDateQ3 = "01-10-" + currentyear;
					String expiryDateQ3 = "31-12-" + currentyear;
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(amountforquaterly, startDateQ3, expiryDateQ3,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_FAILED.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_3);
					mpdObj.setInterestAmount(adjustedQ3Amount.multiply(noFODays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100))).setScale(2,2));
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				}
				paymentPeriod = Q4;
				expiryDate = "31-03-" + nextYear;

				//String firstDayAfterexpiryDateQ3 = "01-01-" + nextYear;
				// newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new
				// BigDecimal(4));

				// 450
				quaterlyammount = ammountForTransactionperiod(Q3, amountforquaterly);
				// 1050
				quaterlyammount = quaterlyammount.add(pastDue);

				if (quaterlyammount.compareTo(paidBillAmount) == 0) {
					quaterlyammount = BigDecimal.ZERO;
				}

				else if (quaterlyammount.compareTo(paidBillAmount) > 0) {
					quaterlyammount = quaterlyammount.subtract(paidBillAmount);
				}

				for(Map.Entry<String, BigDecimal> intmap : interestMap.entrySet()) {
					totalInterestAmunt= totalInterestAmunt.add(intmap.getValue());
				}

				totalInterestAmunt = 	totalInterestAmunt.setScale(0, RoundingMode.HALF_UP);
				//totalAmountForDemand = amountforquaterly.add(quaterlyammount).add(totalInterestAmunt);
				totalAmountForDemand = amountforquaterly.add(amountwithpastdue).add(adjustedQ2Amount).add(adjustedQ3Amount).add(totalInterestAmunt);
				inp = getInterestPenalty( totalInterestAmunt,  firstDayAfterexpiryDateQ3, financialYearFromDemand.toString() ,"Q4", "Q3" , new BigDecimal(InterestPrecentage),noFODays,totalAmountForInterestCal,previousYear);

				if (advancedBillAmount.compareTo(totalAmountForDemand) > 0) {
					advancedBillAmount = advancedBillAmount.subtract(totalAmountForDemand);
					totalAmountForDemand = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-01-" + nextYear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_4);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				} else if (totalAmountForDemand.compareTo(advancedBillAmount) > 0) {
					totalAmountForDemand = totalAmountForDemand.subtract(advancedBillAmount);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-01-" + nextYear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_PENDING.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_4);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				} else if (totalAmountForDemand.compareTo(advancedBillAmount) == 0) {
					totalAmountForDemand = new BigDecimal(0);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-01-" + nextYear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.QUARTER_4);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				}

			}

			break;

		case HALFYEARLY:
			// calcualtion of all halfyearly ammount
			BigDecimal halfyearlyammount = BigDecimal.ZERO;
			BigDecimal amountwithpastduehalf = BigDecimal.ZERO;

			if (h1.contains(cuurentMonth)) {
				paymentPeriod = H1;
				expiryDate = "30-09-" + currentyear;

				// newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new
				// BigDecimal(2));
				totalAmountForDemand = ammountforhalfyearly.add(pastDue);

				if (advancedBillAmount.compareTo(totalAmountForDemand) > 0) {
					advancedBillAmount = advancedBillAmount.subtract(totalAmountForDemand);
					totalAmountForDemand = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-04-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), pastDue);
					mpdObj.setPeriod(TxnPeriodEnum.HALF_YEAR_1);
					mpdList.add(mpdObj);

				} else if (totalAmountForDemand.compareTo(advancedBillAmount) > 0) {
					totalAmountForDemand = totalAmountForDemand.subtract(advancedBillAmount);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-04-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_PENDING.toString(), pastDue);
					mpdObj.setPeriod(TxnPeriodEnum.HALF_YEAR_1);
					mpdList.add(mpdObj);
				} else if (totalAmountForDemand.compareTo(advancedBillAmount) == 0) {
					totalAmountForDemand = new BigDecimal(0);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, "01-04-" + currentyear, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), pastDue);
					mpdObj.setPeriod(TxnPeriodEnum.HALF_YEAR_1);
					mpdList.add(mpdObj);
				}

				inp = getInterestPenalty( BigDecimal.ZERO,  null, null,null, null , BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);

			} else if (h2.contains(cuurentMonth)) {

				BigDecimal totalAMountForInterest = BigDecimal.ZERO;
				String calculationFinalDateForInterest=null;
				BigDecimal noFODays =  BigDecimal.ZERO;
				String firstDayAfterexpiryDateQ2 = "01-07-" + currentyear;
				BigDecimal totalInterestAmunt = BigDecimal.ZERO;
				Map<String, BigDecimal> interestMap = new HashMap<>();
				BigDecimal totalAmountForInterestCal=BigDecimal.ZERO;
				BigDecimal adjustedH1Amount=BigDecimal.ZERO;

				if(previousYear)
				{
					try {
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
						String pastStartDate = dateFormat.format(startPeriod);
						String pastEndDate = dateFormat.format(endPeriod);
						currentyear=Integer.parseInt(pastStartDate.split("/")[2]);
						nextYear=Integer.parseInt(pastEndDate.split("/")[2]);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				
				if (!halfyearly.contains("H1")) {
					if(advancedBillAmount.compareTo(ammountforhalfyearly)>0)
					{
						advancedBillAmount=advancedBillAmount.subtract(ammountforhalfyearly);
						adjustedH1Amount=BigDecimal.ZERO;
					}
					else if(advancedBillAmount.compareTo(ammountforhalfyearly)<0)
					{
						adjustedH1Amount=ammountforhalfyearly.subtract(advancedBillAmount);
						advancedBillAmount=BigDecimal.ZERO;
					}
					else if(advancedBillAmount.compareTo(ammountforhalfyearly)==0)
					{
						adjustedH1Amount=BigDecimal.ZERO;
						advancedBillAmount=BigDecimal.ZERO;
					}

					calculationFinalDateForInterest = currentDateWithAssesmentYear(currentyear.toString());
					noFODays = getDateDifference(firstDayAfterexpiryDateQ2,currentDateWithAssesmentYear(currentyear.toString()));
					if(previousYear)
						noFODays=new BigDecimal(H1FlatDays);
					totalAMountForInterest = totalAMountForInterest.add(adjustedH1Amount).multiply(noFODays).multiply(new BigDecimal(InterestPrecentage).divide(new BigDecimal(100)));
					totalAMountForInterest=totalAMountForInterest.setScale(2,2);
					totalAmountForInterestCal=adjustedH1Amount;
					interestMap.put("H1",totalAMountForInterest );

					amountwithpastduehalf = adjustedH1Amount.add(pastDue);
					String startDateh1 = "01-04-" + currentyear;
					String expiryDateh1 = "30-09-" + currentyear;
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(amountwithpastduehalf, startDateh1, expiryDateh1,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_FAILED.toString(), pastDue);
					mpdObj.setPeriod(TxnPeriodEnum.HALF_YEAR_1);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				}
				paymentPeriod = H2;
				String startDateh2 = "01-10-" + currentyear;
				expiryDate = "31-03-" + nextYear;
				String firstDayAfterexpiryDateH1 = "01-10-" + currentyear;

				// newTotalAmountForModeOfPayment = totalAmountForDemand.divide(new
				// BigDecimal(2));
				halfyearlyammount = ammountForTransactionperiod(H1, ammountforhalfyearly);
				halfyearlyammount = halfyearlyammount.add(pastDue);
				if (halfyearlyammount.compareTo(paidBillAmount) == 0) {
					halfyearlyammount = BigDecimal.ZERO;

				} else if (halfyearlyammount.compareTo(paidBillAmount) > 0) {
					halfyearlyammount = halfyearlyammount.subtract(paidBillAmount);
				}

				for(Map.Entry<String, BigDecimal> intmap : interestMap.entrySet()) {
					totalInterestAmunt= totalInterestAmunt.add(intmap.getValue());
				}

				totalInterestAmunt = 	totalInterestAmunt.setScale(0, RoundingMode.HALF_UP);
				//totalAmountForDemand = ammountforhalfyearly.add(halfyearlyammount).add(totalInterestAmunt);
				totalAmountForDemand = ammountforhalfyearly.add(amountwithpastduehalf).add(totalInterestAmunt);
				inp = getInterestPenalty( totalInterestAmunt,  firstDayAfterexpiryDateH1, financialYearFromDemand.toString() ,"H2", "H1" , new BigDecimal(InterestPrecentage),noFODays,totalAmountForInterestCal,previousYear);

				System.out.println("advancedBillAmount::"+advancedBillAmount);
				System.out.println("ammountforhalfyearly::"+ammountforhalfyearly);
				if (advancedBillAmount.compareTo(totalAmountForDemand) > 0) {
					advancedBillAmount = advancedBillAmount.subtract(totalAmountForDemand);
					totalAmountForDemand = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, startDateh2, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.HALF_YEAR_2);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);

				} else if (totalAmountForDemand.compareTo(advancedBillAmount) > 0) {
					totalAmountForDemand = totalAmountForDemand.subtract(advancedBillAmount);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, startDateh2, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_PENDING.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.HALF_YEAR_2);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				} else if (totalAmountForDemand.compareTo(advancedBillAmount) == 0) {
					totalAmountForDemand = new BigDecimal(0);
					advancedBillAmount = new BigDecimal(0);
					mpdObj = new ModeOfPaymentDetails();
					mpdObj = getModeOfPaymentDetails(totalAmountForDemand, startDateh2, expiryDate,
							ModeOfPaymentDetails.TxnStatusEnum.PAID.toString(), BigDecimal.ZERO);
					mpdObj.setPeriod(TxnPeriodEnum.HALF_YEAR_2);
					mpdObj.setRemaingAdvance(advancedBillAmount);
					mpdList.add(mpdObj);
				}

			}

			break;

		case YEARLY:
			paymentPeriod = YR;
			String startDateh1 = "01-04-" + currentyear;
			expiryDate = "31-03-" + nextYear;

			totalAmountForDemand = totalAmountForDemand.add(pastDue);
			if (advancedBillAmount.compareTo(totalAmountForDemand) > 0) {
				totalAmountForDemand = new BigDecimal(0);
				advancedBillAmount = advancedBillAmount.subtract(totalAmountForDemand);

			} else if (totalAmountForDemand.compareTo(advancedBillAmount) > 0) {
				totalAmountForDemand = totalAmountForDemand.subtract(advancedBillAmount);
				advancedBillAmount = new BigDecimal(0);
			} else if (totalAmountForDemand.compareTo(advancedBillAmount) == 0) {
				totalAmountForDemand = new BigDecimal(0);
				advancedBillAmount = new BigDecimal(0);
			}

			mpdObj = new ModeOfPaymentDetails();
			mpdObj = getModeOfPaymentDetails(totalAmountForDemand, startDateh1, expiryDate,
					ModeOfPaymentDetails.TxnStatusEnum.PAYMENT_PENDING.toString(), pastDue);
			mpdObj.setPeriod(TxnPeriodEnum.YEARLY);
			mpdObj.setRemaingAdvance(advancedBillAmount);
			mpdList.add(mpdObj);

			inp = getInterestPenalty( BigDecimal.ZERO,  null, null,null, null , BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,false);


			break;
		default:
			break;
		}
		DemandRequest dmr = new DemandRequest();

		demand.setAdvanceAmount(advancedBillAmount);
		dmr.setRequestInfo(requestInfo);

		List<Demand> demandTobeupdateForAdvance = new ArrayList<>();
		demandTobeupdateForAdvance.add(demand);
		dmr.setDemands(demandTobeupdateForAdvance);
		demandService.updateAsync(dmr, null);

		// Long billExpiryDate = getExpiryDateForDemand(demand);
		Long billExpiryDate = getDateInMilisec(expiryDate, false);

		totalAmountForDemand = roundOfDecimals(totalAmountForDemand);

		mpdList = mpdList.stream().sorted((x, y) -> x.getPeriod().compareTo(y.getPeriod()))
				.collect(Collectors.toList());
		Map<String, Object> additionalDetails = mapper.convertValue(demand.getAdditionalDetails(), Map.class);
		// JsonNode additionalDetails =
		// mapper.convertValue(demand.getAdditionalDetails(),JsonNode.class);
		additionalDetails.put("paymentModeDetails", mpdList);
		// totalAmountForDemand = totalAmountForDemand.add(new
		// BigDecimal(200));//advanced testing
		return BillDetailV2.builder().billAccountDetails(new ArrayList<>(taxCodeAccountdetailMap.values()))
				.amount(totalAmountForDemand).expiryDate(billExpiryDate).demandId(demand.getId())
				.fromPeriod(startPeriod).toPeriod(endPeriod).tenantId(tenantId).paymentPeriod(paymentPeriod)
				.modeOfPaymentDetails(mpdList).additionalDetails(additionalDetails)
				.interestfornoofdays(inp.getInterestfornoofdays())
				.adjusmentfromdate(inp.getAdjusmentfromdate())
				.interestcalculatedsession(inp.getInterestcalculatedsession())
				.interestonamount(inp.getInterestonamount())
				.interestpercentage(inp.getInterestpercentage())
				.assesmentyear(inp.getAssesmentyear())
				.adjustedtosession(inp.getAdjustedtosession())
				.totalAmountForIntCal(inp.getTotalAMountForInterest())
				.previousYearAssesment(inp.isPreviousYear())
				.build();

	}

	private BigDecimal getDateDifference(String startDate, String endDate) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate firstDate = LocalDate.parse(startDate, dtf);
		LocalDate secondDate = LocalDate.parse(endDate, dtf);
		BigDecimal daysdiff = new BigDecimal(ChronoUnit.DAYS.between(firstDate, secondDate));
		return daysdiff;
	}

	private String currentDateWithAssesmentYear(String year) {
		Date currentDate = new Date(System.currentTimeMillis());
		Calendar crd = Calendar.getInstance();
		String updatedDate =null;
		crd.setTime(currentDate);
		Integer date = crd.get(Calendar.DAY_OF_MONTH);
		Integer cuurentMonth = crd.get(Calendar.MONTH) + 1;
		if(cuurentMonth>9)
			updatedDate = date.toString()+"-"  + cuurentMonth.toString() + "-" + year;
		else
			updatedDate = date.toString() + "-0" + cuurentMonth.toString() + "-" + year;

		if(date<10)
			updatedDate = "0"+date.toString()+"-"  + cuurentMonth.toString() + "-" + year;

		return updatedDate;
	}

	private InterestAndPenalty getInterestPenalty( BigDecimal interestonamount, String adjusmentfromdate,
			String assesmentyear, String adjustedtosession, String interestcalculatedsession, BigDecimal interestpercentage,
			BigDecimal interestfornoofdays, BigDecimal totalAMountForInterest,boolean previousYear) {

		InterestAndPenalty inp = new InterestAndPenalty();
		inp.setAdjusmentfromdate(adjusmentfromdate);
		inp.setAdjustedtosession(adjustedtosession);
		inp.setAssesmentyear(assesmentyear);
		inp.setInterestcalculatedsession(interestcalculatedsession);
		inp.setInterestfornoofdays(Integer.parseInt(interestfornoofdays.toString()));
		inp.setInterestonamount(interestonamount);
		inp.setInterestpercentage(interestpercentage);
		inp.setTotalAMountForInterest(totalAMountForInterest);
		inp.setPreviousYear(previousYear);
		return inp;

	}

	private Long getDateInMilisec(String date, boolean start) {
		Calendar datexp = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		try {
			java.util.Date parsedDate = dateFormat.parse(date);
			datexp.setTime(parsedDate);
			if (start)
				datexp.set(datexp.get(Calendar.YEAR), datexp.get(Calendar.MONTH), datexp.get(Calendar.DATE), 00, 00,
						00);
			else
				datexp.set(datexp.get(Calendar.YEAR), datexp.get(Calendar.MONTH), datexp.get(Calendar.DATE), 23, 59,
						59);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return datexp.getTimeInMillis();
	}

	private ModeOfPaymentDetails getModeOfPaymentDetails(BigDecimal amount, String startDate, String endDate,
			String status, BigDecimal pastDue) {
		ModeOfPaymentDetails mpdObj = new ModeOfPaymentDetails();
		mpdObj.setAmount(amount);
		mpdObj.setFromPeriod(getDateInMilisec(startDate, true));
		mpdObj.setToPeriod(getDateInMilisec(endDate, false));
		mpdObj.setStatus(ModeOfPaymentDetails.TxnStatusEnum.valueOf(status));
		mpdObj.setPastAmount(pastDue);
		return mpdObj;
	}

	private BigDecimal ammountForTransactionperiod(String paymentPeriod, BigDecimal amountforquater) {
		// TODO Auto-generated method stub
		String num = paymentPeriod.substring(1);
		BigDecimal ammount = amountforquater.multiply(new BigDecimal(num));
		return ammount;
	}

	/**
	 * @param demand
	 * 
	 * @return expiryDate
	 */

	public BigDecimal roundOfDecimals(BigDecimal totalAmount) {
		BigDecimal roundOffPos = BigDecimal.ZERO;
		BigDecimal roundOffNeg = BigDecimal.ZERO;

		BigDecimal roundOffAmount = totalAmount.setScale(2, 2);
		BigDecimal reminder = roundOffAmount.remainder(BigDecimal.ONE);

		if (reminder.doubleValue() >= 0.5) {
			roundOffPos = roundOffPos.add(BigDecimal.ONE.subtract(reminder));
			totalAmount = totalAmount.add(roundOffPos);
			totalAmount = totalAmount.setScale(2, 2);

		}

		else if (reminder.doubleValue() < 0.5) {
			roundOffNeg = roundOffNeg.add(reminder).negate();
			totalAmount = totalAmount.add(roundOffNeg);
			totalAmount = totalAmount.setScale(2, 2);
		}
		return totalAmount;

	}

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

			BillAccountDetailV2 accountDetail = BillAccountDetailV2.builder().demandDetailId(demandDetail.getId())
					.tenantId(demandDetail.getTenantId()).id(UUID.randomUUID().toString())
					.adjustedAmount(BigDecimal.ZERO).taxHeadCode(taxHead.getCode()).amount(newAmountForAccDeatil)
					.order(taxHead.getOrder()).billDetailId(billDetailId).build();

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
	private Map<String, BusinessServiceDetail> getBusinessService(Set<String> businessService, String tenantId,
			RequestInfo requestInfo) {
		List<BusinessServiceDetail> businessServiceDetails = businessServDetailService.searchBusinessServiceDetails(
				BusinessServiceDetailCriteria.builder().businessService(businessService).tenantId(tenantId).build(),
				requestInfo).getBusinessServiceDetails();
		return businessServiceDetails.stream()
				.collect(Collectors.toMap(BusinessServiceDetail::getCode, Function.identity()));
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

		if (!CollectionUtils.isEmpty(billRequest.getBills()))
			billRepository.saveBill(billRequest);

		// Update Bill Status to Paid for bill with amount 0
		List<BillV2> filteredBills = new ArrayList<>();

		for (BillV2 b : billRequest.getBills()) {
			if (b.getBillDetails().get(0).getAmount().compareTo(new BigDecimal(0)) == 0) {
				filteredBills.add(b);
			}
		}
		String transactiontUrl = new StringBuilder().append(appProps.getPgSeriviceHost())
				.append(appProps.getPgCreateEndpoint()).append(URL_PARAMS_SEPARATER).append("tenantId=")
				.append(billRequest.getRequestInfo().getUserInfo().getTenantId()).toString();

		StringBuilder url = new StringBuilder(
				"http://mnptapp.manipurpropertytax.org/digit-ui/citizen/payment/success/PT/");

		// To Be Updated to Topic based request
		TransactionRequest transactionRequest = null;
		TransactionCreateResponse resp = null;
		List<TaxAndPayment> taxAndPayment = null;
		TaxAndPayment tp = null;
		TransactionUser user = null;
		for (BillV2 b : filteredBills) {
			transactionRequest = new TransactionRequest();
			transactionRequest.setRequestInfo(billRequest.getRequestInfo());
			Transaction tr = new Transaction();
			taxAndPayment = new ArrayList<>();
			tp = new TaxAndPayment();
			tr.setBillId(b.getId());
			tr.setTenantId(b.getTenantId());
			tr.setGateway("PAYGOV");
			tr.setTxnAmount(b.getBillDetails().get(0).getAmount().toString());
			url.append(b.getConsumerCode()).append("/").append(b.getTenantId()).append("?").append("propertyId=")
			.append(b.getConsumerCode());
			tr.setCallbackUrl(url.toString());
			user = new TransactionUser();
			user.setName(billRequest.getRequestInfo().getUserInfo().getName());
			user.setMobileNumber(billRequest.getRequestInfo().getUserInfo().getMobileNumber());
			user.setTenantId(billRequest.getRequestInfo().getUserInfo().getTenantId());

			tr.setUser(user);
			tr.setConsumerCode(b.getConsumerCode());
			tr.setProductInfo("Common Payment");
			tr.setModule("PT");

			tp.setTaxAmount(b.getBillDetails().get(0).getAmount());
			tp.setAmountPaid(b.getBillDetails().get(0).getAmount());
			tp.setBillId(b.getId());
			taxAndPayment.add(tp);
			tr.setTaxAndPayments(taxAndPayment);
			tr.setTxnStatus(TxnStatusEnum.SUCCESS);
			transactionRequest.setTransaction(tr);
			resp = restTemplate.postForObject(transactiontUrl, transactionRequest, TransactionCreateResponse.class);
		}

		/*
		 * String transactiontUrl = new
		 * StringBuilder().append(appProps.getPgSeriviceHost()).append(appProps.
		 * getPgSeriviceEndpoint())
		 * .append(URL_PARAMS_SEPARATER).append("billId=").toString();
		 */

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
