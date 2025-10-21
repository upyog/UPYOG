package org.egov.pt.calculator.util;

import static org.egov.pt.calculator.util.CalculatorConstants.ALLOWED_RECEIPT_STATUS;
import static org.egov.pt.calculator.util.CalculatorConstants.ASSESSMENTNUMBER_FIELD_SEARCH;
import static org.egov.pt.calculator.util.CalculatorConstants.BUSINESSSERVICE_FIELD_FOR_SEARCH_URL;
import static org.egov.pt.calculator.util.CalculatorConstants.CONSUMER_CODE_SEARCH_FIELD_NAME;
import static org.egov.pt.calculator.util.CalculatorConstants.DEMAND_END_DATE_PARAM;
import static org.egov.pt.calculator.util.CalculatorConstants.DEMAND_ID_SEARCH_FIELD_NAME;
import static org.egov.pt.calculator.util.CalculatorConstants.DEMAND_START_DATE_PARAM;
import static org.egov.pt.calculator.util.CalculatorConstants.DEMAND_STATUS_ACTIVE;
import static org.egov.pt.calculator.util.CalculatorConstants.DEMAND_STATUS_PARAM;
import static org.egov.pt.calculator.util.CalculatorConstants.PROPERTY_TAX_SERVICE_CODE;
import static org.egov.pt.calculator.util.CalculatorConstants.PT_ADVANCE_CARRYFORWARD;
import static org.egov.pt.calculator.util.CalculatorConstants.RECEIPT_END_DATE_PARAM;
import static org.egov.pt.calculator.util.CalculatorConstants.RECEIPT_START_DATE_PARAM;
import static org.egov.pt.calculator.util.CalculatorConstants.SEPARATER;
import static org.egov.pt.calculator.util.CalculatorConstants.SERVICE_FIELD_FOR_SEARCH_URL;
import static org.egov.pt.calculator.util.CalculatorConstants.SERVICE_FIELD_VALUE_PT;
import static org.egov.pt.calculator.util.CalculatorConstants.STATUS_FIELD_FOR_SEARCH_URL;
import static org.egov.pt.calculator.util.CalculatorConstants.TAXES_TO_BE_CONSIDERD;
import static org.egov.pt.calculator.util.CalculatorConstants.TENANT_ID_FIELD_FOR_SEARCH_URL;
import static org.egov.pt.calculator.util.CalculatorConstants.URL_PARAMS_SEPARATER;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pt.calculator.repository.Repository;
import org.egov.pt.calculator.service.PaymentService;
import org.egov.pt.calculator.web.models.Assessment;
import org.egov.pt.calculator.web.models.BillSearchCriteria;
import org.egov.pt.calculator.web.models.CalculationCriteria;
import org.egov.pt.calculator.web.models.CalculationReq;
import org.egov.pt.calculator.web.models.DemandDetailAndCollection;
import org.egov.pt.calculator.web.models.DemandSearchCriteria;
import org.egov.pt.calculator.web.models.GetBillCriteria;
import org.egov.pt.calculator.web.models.ReceiptSearchCriteria;
import org.egov.pt.calculator.web.models.collections.Payment;
import org.egov.pt.calculator.web.models.collections.PaymentDetail;
import org.egov.pt.calculator.web.models.demand.*;
import org.egov.pt.calculator.web.models.demand.Bill.BillStatusEnum;
import org.egov.pt.calculator.web.models.property.AuditDetails;
import org.egov.pt.calculator.web.models.property.Notice;
import org.egov.pt.calculator.web.models.property.NoticeResponse;
import org.egov.pt.calculator.web.models.property.NoticeType;
import org.egov.pt.calculator.web.models.property.OwnerInfo;
import org.egov.pt.calculator.web.models.property.Property;
import org.egov.pt.calculator.web.models.property.PropertyRequest;
import org.egov.pt.calculator.web.models.property.PropertyResponse;
import org.egov.pt.calculator.web.models.property.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.aop.config.AdvisorEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Component
@Getter
public class CalculatorUtils {

	@Autowired
	private Configurations configurations;

	@Autowired
	private PaymentService paymentService;

	@Value("${customization.allowdepreciationonnoreceipts:false}")
	Boolean allowDepreciationsOnNoReceipts;

	@Autowired
	private Repository repository;

	@Autowired
	private ObjectMapper mapper;

	private Map<String, Integer> taxHeadApportionPriorityMap;

	private static String timeZone;

	@Value("${id.timezone}")
	public  void setTimeZone(String zone){
		CalculatorUtils.timeZone = zone;
	}

	public Map<String, Integer> getTaxHeadApportionPriorityMap() {

		if (null == taxHeadApportionPriorityMap) {
			Map<String, Integer> map = new HashMap<>();
			map.put(CalculatorConstants.PT_TAX, 3);
			map.put(CalculatorConstants.PT_TIME_PENALTY, 1);
			map.put(CalculatorConstants.PT_FIRE_CESS, 2);
			map.put(CalculatorConstants.PT_TIME_INTEREST, 0);
			map.put(CalculatorConstants.MAX_PRIORITY_VALUE, 100);
		}
		return taxHeadApportionPriorityMap;
	}

	/**
	 * Prepares and returns Mdms search request with financial master criteria
	 *
	 * @param requestInfo
	 * @param assesmentYear
	 * @return
	 */
	public MdmsCriteriaReq getFinancialYearRequest(RequestInfo requestInfo, String assesmentYear, String tenantId) {

		MasterDetail mstrDetail = MasterDetail.builder().name(CalculatorConstants.FINANCIAL_YEAR_MASTER)
				.filter("[?(@." + CalculatorConstants.FINANCIAL_YEAR_RANGE_FEILD_NAME + " IN [" + assesmentYear + "])]")
				.build();
		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(CalculatorConstants.FINANCIAL_MODULE)
				.masterDetails(Arrays.asList(mstrDetail)).build();
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId(tenantId)
				.build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}

	/**
	 * Prepares and returns Mdms search request with financial master criteria
	 *
	 * @param requestInfo
	 * @param assesmentYears
	 * @return
	 */
	public MdmsCriteriaReq getFinancialYearRequest(RequestInfo requestInfo, Set<String> assesmentYears, String tenantId) {

		String assessmentYearStr = StringUtils.join(assesmentYears, ",");
		MasterDetail mstrDetail = MasterDetail.builder().name(CalculatorConstants.FINANCIAL_YEAR_MASTER)
				.filter("[?(@." + CalculatorConstants.FINANCIAL_YEAR_RANGE_FEILD_NAME + " IN [" + assessmentYearStr + "]" +
						" && @.module== '" + SERVICE_FIELD_VALUE_PT + "')]")
				.build();
		ModuleDetail moduleDetail = ModuleDetail.builder().moduleName(CalculatorConstants.FINANCIAL_MODULE)
				.masterDetails(Arrays.asList(mstrDetail)).build();
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId(tenantId)
				.build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}

	/**
	 * Methods provides all the usage category master for property tax module
	 */
	public MdmsCriteriaReq getPropertyModuleRequest(RequestInfo requestInfo, String tenantId) {

		List<MasterDetail> details = new ArrayList<>();

		details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_MAJOR_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_MINOR_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_SUB_MINOR_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_DETAIL_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.OWNER_TYPE_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.REBATE_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.PENANLTY_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.FIRE_CESS_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.CANCER_CESS_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.INTEREST_MASTER).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.ROAD_TYPE_RATES).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.STRUCTURE_TYPE_RATES).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.AGE_OF_PROPERTY_RATES).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.SPECIAL_EXCEMPTION).build());
		details.add(MasterDetail.builder().name(CalculatorConstants.USAGE_OWNER_CATEGORY_RATES).build());
		ModuleDetail mdDtl = ModuleDetail.builder().masterDetails(details)
				.moduleName(CalculatorConstants.PROPERTY_TAX_MODULE).build();
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(mdDtl)).tenantId(tenantId)
				.build();
		return MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	}

	/**
	 * Returns the url for mdms search endpoint
	 *
	 * @return
	 */
	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(configurations.getMdmsHost()).append(configurations.getMdmsEndpoint());
	}

	/**
	 * Returns the tax head search Url with tenantId and PropertyTax service name
	 * parameters
	 *
	 * @param tenantId
	 * @return
	 */
	public StringBuilder getTaxHeadSearchUrl(String tenantId) {

		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getTaxheadsSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(SEPARATER).append(SERVICE_FIELD_FOR_SEARCH_URL)
				.append(SERVICE_FIELD_VALUE_PT);
	}

	/**
	 * Returns the tax head search Url with tenantId and PropertyTax service name
	 * parameters
	 *
	 * @param tenantId
	 * @return
	 */
	public StringBuilder getTaxPeriodSearchUrl(String tenantId) {

		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getTaxPeriodSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(SEPARATER).append(SERVICE_FIELD_FOR_SEARCH_URL)
				.append(SERVICE_FIELD_VALUE_PT);
	}

	/**
	 * Returns the Receipt search Url with tenantId and cosumerCode service name
	 * parameters
	 *
	 * @param tenantId
	 * @return
	 */
	public StringBuilder getReceiptSearchUrl(String tenantId, List<String> consumerCodes) {

		return new StringBuilder().append(configurations.getCollectionServiceHost())
				.append(configurations.getReceiptSearchEndpoint()).append(CalculatorConstants.URL_PARAMS_SEPARATER)
				.append(CalculatorConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(CalculatorConstants.SEPARATER).append(CalculatorConstants.CONSUMER_CODE_SEARCH_FIELD_NAME)
				.append(consumerCodes.toString().replace("[", "").replace("]", ""))
				.append(CalculatorConstants.SEPARATER).append(STATUS_FIELD_FOR_SEARCH_URL)
				.append(ALLOWED_RECEIPT_STATUS);
	}


	/**
	 * Returns the Receipt search Url with tenantId, cosumerCode,service name and tax period
	 * parameters
	 *
	 * @param criteria
	 * @return
	 */
	public StringBuilder getReceiptSearchUrl(ReceiptSearchCriteria criteria) {


		return new StringBuilder().append(configurations.getCollectionServiceHost())
				.append(configurations.getReceiptSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(criteria.getTenantId())
				.append(SEPARATER).append(CONSUMER_CODE_SEARCH_FIELD_NAME)
				.append(criteria.getPropertyId())
				.append(SEPARATER).append(RECEIPT_START_DATE_PARAM)
				.append(criteria.getFromDate())
				.append(SEPARATER).append(RECEIPT_END_DATE_PARAM)
				.append(criteria.getToDate())
				.append(CalculatorConstants.SEPARATER).append(STATUS_FIELD_FOR_SEARCH_URL)
				.append(ALLOWED_RECEIPT_STATUS);
	}


	/**
	 * method to create demandsearch url with demand criteria
	 *
	 * @param getBillCriteria
	 * @return
	 */
	public StringBuilder getDemandSearchUrl(GetBillCriteria getBillCriteria) {
		StringBuilder builder = new StringBuilder();
		if (CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes())) {
			builder = builder.append(configurations.getBillingServiceHost())
					.append(configurations.getDemandSearchEndPoint()).append(URL_PARAMS_SEPARATER)
					.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
					.append(SEPARATER)
					.append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(getBillCriteria.getPropertyId())
					.append(SEPARATER)
					.append(DEMAND_STATUS_PARAM).append(DEMAND_STATUS_ACTIVE);
		}
		else {

			builder = builder.append(configurations.getBillingServiceHost())
					.append(configurations.getDemandSearchEndPoint()).append(URL_PARAMS_SEPARATER)
					.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(getBillCriteria.getTenantId())
					.append(SEPARATER)
					.append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(StringUtils.join(getBillCriteria.getConsumerCodes(), ","))
					.append(SEPARATER)
					.append(DEMAND_STATUS_PARAM).append(DEMAND_STATUS_ACTIVE);

		}
		if (getBillCriteria.getFromDate() != null && getBillCriteria.getToDate() != null)
			builder = builder.append(DEMAND_START_DATE_PARAM).append(getBillCriteria.getFromDate())
			.append(SEPARATER)
			.append(DEMAND_END_DATE_PARAM).append(getBillCriteria.getToDate())
			.append(SEPARATER);

		return builder;
	}

	/**
	 * method to create demandsearch url with demand criteria
	 *
	 * @param assessment
	 * @return
	 */
	public StringBuilder getDemandSearchUrl(Assessment assessment) {

		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getDemandSearchEndPoint()).append(CalculatorConstants.URL_PARAMS_SEPARATER)
				.append(CalculatorConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(assessment.getTenantId())
				.append(CalculatorConstants.SEPARATER)
				.append(CalculatorConstants.CONSUMER_CODE_SEARCH_FIELD_NAME).append(assessment.getPropertyId() + CalculatorConstants.PT_CONSUMER_CODE_SEPARATOR + assessment.getAssessmentNumber());
	}


	/**
	 * method to create demandsearch url with demand criteria
	 *
	 * @param criteria
	 * @return
	 */
	public StringBuilder getDemandSearchUrl(DemandSearchCriteria criteria) {

		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getDemandSearchEndPoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(criteria.getTenantId())
				.append(SEPARATER)
				.append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(criteria.getPropertyId())
				.append(SEPARATER)
				.append(DEMAND_STATUS_PARAM).append(DEMAND_STATUS_ACTIVE);
	}

	public StringBuilder getBillSearchUrl(BillSearchCriteria criteria) {

		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getBillSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(criteria.getTenantId())
				.append(SEPARATER)
				.append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(criteria.getConsumerCode())
				.append(SEPARATER)
				.append(SERVICE_FIELD_FOR_SEARCH_URL)
				.append(SERVICE_FIELD_VALUE_PT)
				.append(SEPARATER)
				.append(DEMAND_ID_SEARCH_FIELD_NAME).append(criteria.getDemandId())
				.append(SEPARATER)
				.append("searchAllForDemand=true");
	}

	public StringBuilder getCollectionSearchUrl(String tenantId,String billId) {

		return new StringBuilder().append(configurations.getCollectionServiceHost())
				.append(configurations.getPaymentSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(SEPARATER)
				.append("billIds=").append(billId);
	}
	
	public StringBuilder getNoticeSearchUrl(String tenantId,String propertyid) {

		return new StringBuilder().append(configurations.getAssessmentServiceHost())
				.append(configurations.getNoticeSearchEndpoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(SEPARATER)
				.append("propertyIds=").append(propertyid);
	}

	/**
	 * Returns the applicable total tax amount to be paid on a demand
	 *
	 * @param demand
	 * @return
	 */
	public BigDecimal getTaxAmtFromDemandForApplicablesGeneration(Demand demand) {
		BigDecimal taxAmt = BigDecimal.ZERO;
		for (DemandDetail detail : demand.getDemandDetails()) {
			if (CalculatorConstants.TAXES_TO_BE_CONSIDERD.contains(detail.getTaxHeadMasterCode()))
				taxAmt = taxAmt.add(detail.getTaxAmount());
		}
		return taxAmt;
	}

	/**
	 * Returns the total tax amount to be paid on a demand
	 *
	 * @param demand
	 * @return
	 */
	public BigDecimal getTaxAmtFromDemandForAdditonalTaxes(Demand demand) {
		BigDecimal taxAmt = BigDecimal.ZERO;
		for (DemandDetail detail : demand.getDemandDetails()) {
			if (CalculatorConstants.ADDITIONAL_TAXES
					.contains(detail.getTaxHeadMasterCode()))
				taxAmt = taxAmt.add(detail.getTaxAmount());
			else if (CalculatorConstants.ADDITIONAL_DEBITS
					.contains(detail.getTaxHeadMasterCode()))
				taxAmt = taxAmt.subtract(detail.getTaxAmount());
		}
		return taxAmt;
	}

	/**
	 * Returns url for demand update Api
	 *
	 * @return
	 */
	public StringBuilder getUpdateDemandUrl() {
		return new StringBuilder().append(configurations.getBillingServiceHost()).append(configurations.getDemandUpdateEndPoint());
	}

	/**
	 * Returns url for Bill Gen Api
	 *
	 * @param tenantId
	 * @param demandId
	 * @param consumerCode
	 * @return
	 */
	public StringBuilder getBillGenUrl(String tenantId, String demandId, String consumerCode) {
		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getBillGenEndPoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(SEPARATER).append(DEMAND_ID_SEARCH_FIELD_NAME)
				.append(demandId).append(SEPARATER)
				.append(BUSINESSSERVICE_FIELD_FOR_SEARCH_URL)
				.append(PROPERTY_TAX_SERVICE_CODE).append(SEPARATER)
				.append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(consumerCode);
	}

	/**
	 * Returns url for Bill Gen Api
	 *
	 * @param tenantId
	 * @param consumerCode
	 * @return
	 */
	public StringBuilder getBillGenUrl(String tenantId, String consumerCode) {
		return new StringBuilder().append(configurations.getBillingServiceHost())
				.append(configurations.getBillGenEndPoint()).append(URL_PARAMS_SEPARATER)
				.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(SEPARATER)
				.append(BUSINESSSERVICE_FIELD_FOR_SEARCH_URL)
				.append(PROPERTY_TAX_SERVICE_CODE).append(SEPARATER)
				.append(CONSUMER_CODE_SEARCH_FIELD_NAME).append(consumerCode);
	}

	/**
	 * Query to fetch assessments for the given criteria
	 *
	 * @param assessment
	 * @return
	 */
	public String getAssessmentQuery(Assessment assessment, List<Object> preparedStmtList) {

		StringBuilder query = new StringBuilder("SELECT * FROM eg_pt_assessment where tenantId=");

		preparedStmtList.add(assessment.getTenantId());

		if (assessment.getAssessmentNumber() != null) {
			query.append(" AND assessmentNumber=?");
			preparedStmtList.add(assessment.getAssessmentNumber());
		}

		if (assessment.getDemandId() != null) {
			query.append(" AND a1.demandId=?");
			preparedStmtList.add(assessment.getDemandId());
		}

		if (assessment.getPropertyId() != null) {
			query.append(" AND a1.propertyId=?");
			preparedStmtList.add(assessment.getPropertyId());
		}

		query.append(" ORDER BY createdtime");

		return query.toString();
	}

	/**
	 * Query to fetch latest assessment for the given criteria
	 *
	 * @param assessment
	 * @return
	 */
	public String getMaxAssessmentQuery(Assessment assessment, List<Object> preparedStmtList) {

		StringBuilder query = new StringBuilder("SELECT * FROM eg_pt_assessment a1 INNER JOIN "

                + "(select Max(createdtime) as maxtime, propertyid, assessmentyear from eg_pt_assessment group by propertyid, assessmentyear) a2 "

                + "ON a1.createdtime=a2.maxtime and a1.propertyid=a2.propertyid where a1.tenantId=? ");

		preparedStmtList.add(assessment.getTenantId());

		if (assessment.getDemandId() != null) {
			query.append(" AND a1.demandId=?");
			preparedStmtList.add(assessment.getDemandId());
		}

		if (assessment.getPropertyId() != null) {
			query.append(" AND a1.propertyId=?");
			preparedStmtList.add(assessment.getPropertyId());
		}

		if (assessment.getAssessmentYear() != null) {
			query.append(" AND a1.assessmentyear=?");
			preparedStmtList.add(assessment.getAssessmentYear());
		}

		query.append(" AND a1.active IS TRUE");

		return query.toString();
	}

	/**
	 * Returns the insert query for assessment
	 *
	 * @return
	 */
	public String getAssessmentInsertQuery() {
		return CalculatorConstants.QUERY_ASSESSMENT_INSERT;
	}

	/**
	 * Adds up the collection amount from the given demand
	 * and the previous advance carry forward together as new advance carry forward
	 *
	 * @param demand
	 * @param requestInfo 
	 * @return carryForward
	 */
	public BigDecimal getTotalCollectedAmountAndPreviousCarryForward(Demand demand, RequestInfo requestInfo) {

		BigDecimal carryForward = BigDecimal.ZERO;
		BigDecimal singleunitammount=BigDecimal.ZERO;
		BigDecimal paidAmount=BigDecimal.ZERO;
		
		BigDecimal totalpaidAmountFromPayment=BigDecimal.ZERO;
		BigDecimal totalBillAmount=BigDecimal.ZERO;
		BigDecimal totalAmountForDemand = BigDecimal.ZERO;
		Set<String> consumercode=new HashSet<String>();
		consumercode.add(demand.getConsumerCode());
		BigDecimal pastDue= BigDecimal.ZERO;
		BigDecimal totalAfterPastDueDeduct= BigDecimal.ZERO;
		BigDecimal unpaidbillAmount=BigDecimal.ZERO;
		Boolean demandAdjusted = true;
		BigDecimal interestAmount=BigDecimal.ZERO;
		
		for (DemandDetail detail : demand.getDemandDetails()) 
		{
			BigDecimal amountForAccDeatil = detail.getTaxAmount();
			totalAmountForDemand = totalAmountForDemand.add(amountForAccDeatil);
			/*
			 * carryForward = carryForward.add(detail.getCollectionAmount()); if
			 * (detail.getTaxHeadMasterCode().equalsIgnoreCase(PT_ADVANCE_CARRYFORWARD))
			 * carryForward = carryForward.add(detail.getTaxAmount().negate());
			 */ 
			
			if(detail.getTaxHeadMasterCode().equalsIgnoreCase("PT_PASTDUE_CARRYFORWARD"))
				pastDue=pastDue.add(detail.getTaxAmount());
		}
		System.out.println(totalAmountForDemand);
		System.out.println(pastDue);
		
		
		totalAmountForDemand = totalAmountForDemand.setScale(0, RoundingMode.HALF_UP);
		totalAfterPastDueDeduct = totalAmountForDemand.subtract(pastDue);
		carryForward=demand.getAdvanceAmount();
		if(carryForward.compareTo(BigDecimal.ZERO)>0)
			return carryForward;
		else if(totalAmountForDemand.compareTo(BigDecimal.ZERO)==0)
		{
			carryForward=BigDecimal.ZERO;
			return carryForward;
		}

		BigDecimal amountforquaterly=totalAfterPastDueDeduct.divide(new BigDecimal(4));
		amountforquaterly=amountforquaterly.setScale(0, RoundingMode.HALF_UP);
		BigDecimal ammountforhalfyearly=totalAfterPastDueDeduct.divide(new BigDecimal(2));
		ammountforhalfyearly=ammountforhalfyearly.setScale(0, RoundingMode.HALF_UP);

		BillSearchCriteria criteria=new BillSearchCriteria();
		criteria.setTenantId(demand.getTenantId());
		criteria.setConsumerCode(consumercode);
		criteria.setDemandId(demand.getId());
		criteria.setSearchAllForDemand(true);

		BillResponse res = mapper.convertValue(
				repository.fetchResult(getBillSearchUrl(criteria), new RequestInfoWrapper(requestInfo)),
				BillResponse.class);

		if(res!=null)
		{
			for(Bill bill:res.getBill())
			{
				PaymentResponse payment = mapper.convertValue(
						repository.fetchResult(getCollectionSearchUrl(demand.getTenantId(), bill.getId()), new RequestInfoWrapper(requestInfo)),
						PaymentResponse.class);
				if(null!=payment && null!= payment.getPayments() && !payment.getPayments().isEmpty())
				{
					paidAmount=bill.getBillDetails().get(0).getAmount();
					totalpaidAmountFromPayment =totalpaidAmountFromPayment.add (payment.getPayments().get(0).getTotalAmountPaid());
					totalBillAmount = totalBillAmount.add(bill.getBillDetails().get(0).getAmount());
					
				}
			}

		}
		
		List<Bill> sortedBills=res.getBill();
		sortedBills=sortedBills.stream().sorted((x,y)->y.getAuditDetails().getCreatedTime().compareTo(x.getAuditDetails().getCreatedTime())).collect(Collectors.toList());
		sortedBills=sortedBills.stream().filter(x->x.getStatus().equals(BillStatusEnum.ACTIVE) || x.getStatus().equals(BillStatusEnum.EXPIRED)).collect(Collectors.toList());
		
		if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("Q4"))
		{
			interestAmount=amountforquaterly.multiply(new BigDecimal(90)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
		}
		else if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("Q3"))
		{
			interestAmount=amountforquaterly.multiply(new BigDecimal(91)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
			interestAmount=amountforquaterly.multiply(new BigDecimal(90)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
		}
		else if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("Q2"))
		{
			interestAmount=amountforquaterly.multiply(new BigDecimal(92)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
			interestAmount=amountforquaterly.multiply(new BigDecimal(91)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
			interestAmount=amountforquaterly.multiply(new BigDecimal(90)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
		}
		else if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("Q1"))
		{
			interestAmount=amountforquaterly.multiply(new BigDecimal(91)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
			interestAmount=amountforquaterly.multiply(new BigDecimal(92)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
			interestAmount=amountforquaterly.multiply(new BigDecimal(91)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
			interestAmount=amountforquaterly.multiply(new BigDecimal(90)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
		}
		else if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("H2"))
		{
			interestAmount=ammountforhalfyearly.multiply(new BigDecimal(181)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
		}
		else if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("H1"))
		{
			interestAmount=ammountforhalfyearly.multiply(new BigDecimal(182)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
			interestAmount=ammountforhalfyearly.multiply(new BigDecimal(181)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
		}
		
//		if(demandAdjusted) {
//			totalAmountForDemand = totalAmountForDemand.subtract(pastDue);
//		}
		
		
		if(unpaidbillAmount.compareTo(totalAmountForDemand)>0)
			totalAmountForDemand=unpaidbillAmount;
		
		
		carryForward=totalpaidAmountFromPayment.subtract(totalAmountForDemand);
		System.out.println(res);
		return carryForward;
	}
	
	
	public Map<String,BigDecimal> getTotalCollectedAmountAndPreviousCarryForwardMap(Demand demand, RequestInfo requestInfo) {

		BigDecimal carryForward = BigDecimal.ZERO;
		BigDecimal singleunitammount=BigDecimal.ZERO;
		BigDecimal paidAmount=BigDecimal.ZERO;
		Map<String,BigDecimal>collectedMap = new HashMap<String, BigDecimal>();
		BigDecimal totalpaidAmountFromPayment=BigDecimal.ZERO;
		BigDecimal totalBillAmount=BigDecimal.ZERO;
		BigDecimal totalAmountForDemand = BigDecimal.ZERO;
		Set<String> consumercode=new HashSet<String>();
		consumercode.add(demand.getConsumerCode());
		BigDecimal pastDue= BigDecimal.ZERO;
		BigDecimal totalAfterPastDueDeduct= BigDecimal.ZERO;
		BigDecimal unpaidbillAmount=BigDecimal.ZERO;
		BigDecimal interestAmount=BigDecimal.ZERO;
		
		for (DemandDetail detail : demand.getDemandDetails()) 
		{
			BigDecimal amountForAccDeatil = detail.getTaxAmount();
			totalAmountForDemand = totalAmountForDemand.add(amountForAccDeatil);
			/*
			 * carryForward = carryForward.add(detail.getCollectionAmount()); if
			 * (detail.getTaxHeadMasterCode().equalsIgnoreCase(PT_ADVANCE_CARRYFORWARD))
			 * carryForward = carryForward.add(detail.getTaxAmount().negate());
			 */ 
			
			if(detail.getTaxHeadMasterCode().equalsIgnoreCase("PT_PASTDUE_CARRYFORWARD"))
				pastDue=detail.getTaxAmount();
		}
		
		totalAmountForDemand = totalAmountForDemand.setScale(0, RoundingMode.HALF_UP);
		totalAfterPastDueDeduct = totalAmountForDemand.subtract(pastDue);
		carryForward=demand.getAdvanceAmount();
		if(carryForward.compareTo(BigDecimal.ZERO)>0) {
			collectedMap.put("PT_ADVANCE_CARRYFORWARD",carryForward );
			return  collectedMap;
		}
			
		else if(totalAmountForDemand.compareTo(BigDecimal.ZERO)==0)
		{
			collectedMap.put("PT_PASTDUE_CARRYFORWARD",BigDecimal.ZERO );
			return  collectedMap;
		}

		BigDecimal amountforquaterly=totalAfterPastDueDeduct.divide(new BigDecimal(4));
		amountforquaterly=amountforquaterly.setScale(0, RoundingMode.HALF_UP);
		BigDecimal ammountforhalfyearly=totalAfterPastDueDeduct.divide(new BigDecimal(2));
		ammountforhalfyearly=ammountforhalfyearly.setScale(0, RoundingMode.HALF_UP);

		BillSearchCriteria criteria=new BillSearchCriteria();
		criteria.setTenantId(demand.getTenantId());
		criteria.setConsumerCode(consumercode);
		criteria.setDemandId(demand.getId());
		criteria.setSearchAllForDemand(true);

		BillResponse res = mapper.convertValue(
				repository.fetchResult(getBillSearchUrl(criteria), new RequestInfoWrapper(requestInfo)),
				BillResponse.class);
		
		List<Bill> sortedBills=res.getBill();
		sortedBills=sortedBills.stream().sorted((x,y)->y.getAuditDetails().getCreatedTime().compareTo(x.getAuditDetails().getCreatedTime())).collect(Collectors.toList());
		sortedBills=sortedBills.stream().filter(x->x.getStatus().equals(BillStatusEnum.ACTIVE) || x.getStatus().equals(BillStatusEnum.EXPIRED)).collect(Collectors.toList());
		if(res!=null)
		{
			for(Bill bill:res.getBill())
			{
				PaymentResponse payment = mapper.convertValue(
						repository.fetchResult(getCollectionSearchUrl(demand.getTenantId(), bill.getId()), new RequestInfoWrapper(requestInfo)),
						PaymentResponse.class);
				if(null!=payment && null!= payment.getPayments() && !payment.getPayments().isEmpty())
				{
					paidAmount=bill.getBillDetails().get(0).getAmount();
					totalpaidAmountFromPayment =totalpaidAmountFromPayment.add (payment.getPayments().get(0).getTotalAmountPaid());
					totalBillAmount = totalBillAmount.add(bill.getBillDetails().get(0).getAmount());
					
				}
				
			}

		}
		
		if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("Q4"))
		{
			interestAmount=amountforquaterly.multiply(new BigDecimal(90)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
		}
		else if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("Q3"))
		{
			interestAmount=amountforquaterly.multiply(new BigDecimal(91)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
			interestAmount=amountforquaterly.multiply(new BigDecimal(90)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
		}
		else if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("Q2"))
		{
			interestAmount=amountforquaterly.multiply(new BigDecimal(92)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
			interestAmount=amountforquaterly.multiply(new BigDecimal(91)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
			interestAmount=amountforquaterly.multiply(new BigDecimal(90)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
		}
		else if(sortedBills.get(0).getBillDetails().get(0).getPaymentPeriod().equals("Q1"))
		{
			interestAmount=amountforquaterly.multiply(new BigDecimal(91)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(sortedBills.get(0).getBillDetails().get(0).getAmount());
			interestAmount=amountforquaterly.multiply(new BigDecimal(92)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
			interestAmount=amountforquaterly.multiply(new BigDecimal(91)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
			interestAmount=amountforquaterly.multiply(new BigDecimal(90)).multiply(new BigDecimal(0.014).divide(new BigDecimal(100)));
			unpaidbillAmount=interestAmount.add(unpaidbillAmount).add(amountforquaterly);
		}
			
		
		if(unpaidbillAmount.compareTo(totalAmountForDemand)>0)
			totalAmountForDemand=unpaidbillAmount;
		//ADVANCE CASE
		if(totalpaidAmountFromPayment.compareTo(totalAmountForDemand)>0) {
			collectedMap.put("PT_ADVANCE_CARRYFORWARD",totalpaidAmountFromPayment.subtract(totalAmountForDemand));
		}
		else if(totalpaidAmountFromPayment.compareTo(totalAmountForDemand)<=0) {
			carryForward=totalAmountForDemand.subtract(totalpaidAmountFromPayment);
			collectedMap.put("PT_PASTDUE_CARRYFORWARD",carryForward);
		}
		
		
		return collectedMap;
	}

	public AuditDetails getAuditDetails(String by, boolean isCreate) {
		Long time = new Date().getTime();

		if (isCreate)
			return AuditDetails.builder().createdBy(by).createdTime(time).lastModifiedBy(by).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}


	public DemandDetailAndCollection getLatestDemandDetailByTaxHead(String taxHeadCode, List<DemandDetail> demandDetails) {
		List<DemandDetail> details = demandDetails.stream().filter(demandDetail -> demandDetail.getTaxHeadMasterCode().equalsIgnoreCase(taxHeadCode))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(details))
			return null;

		BigDecimal taxAmountForTaxHead = BigDecimal.ZERO;
		BigDecimal collectionAmountForTaxHead = BigDecimal.ZERO;
		DemandDetail latestDemandDetail = null;
		long maxCreatedTime = 0l;

		for (DemandDetail detail : details) {
			taxAmountForTaxHead = taxAmountForTaxHead.add(detail.getTaxAmount());
			collectionAmountForTaxHead = collectionAmountForTaxHead.add(detail.getCollectionAmount());
			if (detail.getAuditDetails().getCreatedTime() > maxCreatedTime) {
				maxCreatedTime = detail.getAuditDetails().getCreatedTime();
				latestDemandDetail = detail;
			}
		}

		return DemandDetailAndCollection.builder()
				.taxHeadCode(taxHeadCode)
				.latestDemandDetail(latestDemandDetail)
				.taxAmountForTaxHead(taxAmountForTaxHead)
				.collectionAmountForTaxHead(collectionAmountForTaxHead)
				.build();

	}


	/**
	 * Returns the applicable total tax amount to be paid after the receipt
	 *
	 * @param receipt
	 * @return
	 */
	/*    public BigDecimal getTaxAmtFromReceiptForApplicablesGeneration(Receipt receipt) {
        BigDecimal taxAmt = BigDecimal.ZERO;
        BigDecimal amtPaid = BigDecimal.ZERO;
        List<BillAccountDetail> billAccountDetails = receipt.getBill().get(0).getBillDetails().get(0).getBillAccountDetails();
        for (BillAccountDetail detail : billAccountDetails) {
            if (TAXES_TO_BE_CONSIDERD.contains(detail.getTaxHeadCode())) {
                taxAmt = taxAmt.add(detail.getAmount());
                amtPaid = amtPaid.add(detail.getAdjustedAmount());
            }
        }
        return taxAmt.subtract(amtPaid);
    }*/


	/**
	 * Returns the applicable total tax amount to be paid after the receipt
	 *
	 * @param payment
	 * @return
	 */
	public BigDecimal getTaxAmtFromPaymentForApplicablesGeneration(Payment payment, TaxPeriod taxPeriod) {
		BigDecimal taxAmt = BigDecimal.ZERO;
		BigDecimal amtPaid = BigDecimal.ZERO;

		List<BillAccountDetail> billAccountDetails = new LinkedList<>();
		if(payment!=null) {
			payment.getPaymentDetails().forEach(paymentDetail -> {
				if (paymentDetail.getBusinessService().equalsIgnoreCase(SERVICE_FIELD_VALUE_PT)) {
					paymentDetail.getBill().getBillDetails().forEach(billDetail -> {
						if (billDetail.getFromPeriod().equals(taxPeriod.getFromDate())
								&& billDetail.getToPeriod().equals(taxPeriod.getToDate())) {
							billAccountDetails.addAll(billDetail.getBillAccountDetails());
						}
					});
				}
			});

			for (BillAccountDetail detail : billAccountDetails) {
				if (TAXES_TO_BE_CONSIDERD.contains(detail.getTaxHeadCode())) {
					taxAmt = taxAmt.add(detail.getAmount());
					amtPaid = amtPaid.add(detail.getAdjustedAmount());
				}
			}
			return taxAmt.subtract(amtPaid);
		}else {
			return BigDecimal.ZERO;
		}
	}


	/**
	 * Returns the current end of the day epoch time for the given epoch time
	 *
	 * @param epoch The epoch time for which end of day time is required
	 * @return End of day epoch time for the given time
	 */
	public static Long getEODEpoch(Long epoch) {
		LocalDate date =
				Instant.ofEpochMilli(epoch).atZone(ZoneId.of(ZoneId.SHORT_IDS.get(timeZone))).toLocalDate();
		LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
		Long eodEpoch = endOfDay.atZone(ZoneId.of(ZoneId.SHORT_IDS.get(timeZone))).toInstant().toEpochMilli();
		return eodEpoch;
	}

	/**
	 * Check if Depreciation is allowed for this Property.
	 * In case there is no receipt the depreciation will be allowed
	 *
	 * @param demand             The demand agianst which receipts have to checked
	 * @param requestInfoWrapper The incoming requestInfo
	 */

	public Boolean isAssessmentDepreciationAllowed(Demand demand, RequestInfoWrapper requestInfoWrapper) {
		boolean isDepreciationAllowed = false;
		if (allowDepreciationsOnNoReceipts) {
			List<Payment> payments = paymentService.getPaymentsFromDemand(demand, requestInfoWrapper);

			if (payments.size() == 0)
				isDepreciationAllowed = true;
		}

		return isDepreciationAllowed;
	}


	/**
	 * @param requestInfo
	 * @param calculationCriteria
	 * @return
	 */
	public Demand getLatestDemandForCurrentFinancialYear(RequestInfo requestInfo, CalculationCriteria calculationCriteria) {

		DemandSearchCriteria criteria = new DemandSearchCriteria();
		//criteria.setFromDate(calculationCriteria.getFromDate());
		//criteria.setToDate(calculationCriteria.getToDate());
		criteria.setTenantId(calculationCriteria.getTenantId());
		criteria.setPropertyId(calculationCriteria.getProperty().getPropertyId());


		DemandResponse res = mapper.convertValue(
				repository.fetchResult(getDemandSearchUrl(criteria), new RequestInfoWrapper(requestInfo)),
				DemandResponse.class);

		if (CollectionUtils.isEmpty(res.getDemands()))
			return null;

		return res.getDemands().get(0);
	}
	
	public BigDecimal getNoticePenaltyAmount(RequestInfo requestInfo, CalculationCriteria calculationCriteria) {

		BigDecimal penalty=BigDecimal.ZERO;
	
		NoticeResponse res = mapper.convertValue(
				repository.fetchResult(getNoticeSearchUrl(calculationCriteria.getTenantId(),calculationCriteria.getProperty().getPropertyId()), new RequestInfoWrapper(requestInfo)),
				NoticeResponse.class);

		if (CollectionUtils.isEmpty(res.getNotice()))
			return penalty;
		
		else
			for (Notice notice : res.getNotice()) {
				if(notice.getNoticeType().equals(NoticeType.NOTICE_FOR_PENALTY))
				penalty=penalty.add(new BigDecimal(notice.getPenaltyAmount()));
			}

		return penalty;
	}


	/**
	 * Creates search query for PT based on tenantId and list of assessment numbers
	 *
	 * @param tenantId          TenantId of the properties
	 * @param assessmentNumbers List of assessmentNumbers to search
	 * @return List of properties
	 */
	public StringBuilder getPTSearchQuery(String tenantId, List<String> assessmentNumbers) {

		StringBuilder url = new StringBuilder(configurations.getPtHost());
		url.append(configurations.getPtSearchEndpoint())
		.append(URL_PARAMS_SEPARATER)
		.append(TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
		.append(SEPARATER)
		.append(ASSESSMENTNUMBER_FIELD_SEARCH)
		.append(StringUtils.join(assessmentNumbers, ","));

		return url;

	}


	/**
	 * Creates CalculationRequest from PropertyRequest
	 *
	 * @param request PropertyRequest for which calculation has to be done
	 * @return Calculation Request based on PropertyRequest
	 */
	public CalculationReq createCalculationReq(PropertyRequest request) {

		String tenantId = request.getProperties().get(0).getTenantId();
		RequestInfo requestInfo = request.getRequestInfo();
		List<CalculationCriteria> calculationCriterias = new LinkedList<>();

		request.getProperties().forEach(property -> {
			CalculationCriteria criteria = new CalculationCriteria();
			criteria.setTenantId(tenantId);
			criteria.setProperty(property);
			calculationCriterias.add(criteria);
		});

		CalculationReq calculationReq = new CalculationReq();
		calculationReq.setRequestInfo(requestInfo);
		calculationReq.setCalculationCriteria(calculationCriterias);

		return calculationReq;
	}


	/**
	 * Call PT-services to get Property object for the given applicationNumber and tenantID
	 * @param requestInfo The RequestInfo of the incoming request
	 * @param applicationNumber The applicationNumber whose Property details has to be fetched
	 * @param tenantId The tenantId of the property
	 * @return The property details for the particular applicationNumber
	 */
	public List<Property> getProperty(RequestInfo requestInfo, String applicationNumber, String tenantId){
		String url = getPropertySearchURL();
		url = url.replace("{1}",tenantId).replace("{2}",applicationNumber);

		Object result =repository.fetchResult(new StringBuilder(url),RequestInfoWrapper.builder().
				requestInfo(requestInfo).build());

		PropertyResponse response =null;
		try {
			response = mapper.convertValue(result,PropertyResponse.class);
		}
		catch (IllegalArgumentException e){
			throw new CustomException("PARSING ERROR","Error while parsing response of TradeLicense Search");
		}

		if(response==null || CollectionUtils.isEmpty(response.getProperties()))
			return null;

		return response.getProperties();
	}

	/**
	 * Creates PT search url based on tenantId and applicationNumber
	 * @return PT search url
	 */
	private String getPropertySearchURL(){
		StringBuilder url = new StringBuilder(configurations.getPtHost());
		url.append(configurations.getPtSearchEndpoint());
		url.append("?");
		url.append("tenantId=");
		url.append("{1}");
		url.append("&");
		url.append("applicationNumber=");
		url.append("{2}");
		return url.toString();
	}

	public User getCommonContractUser(OwnerInfo owner){
		org.egov.common.contract.request.User user = new org.egov.common.contract.request.User();
		user.setTenantId(owner.getTenantId());
		user.setId(owner.getId());
		user.setName(owner.getName());
		user.setType(owner.getType());
		user.setMobileNumber(owner.getMobileNumber());
		user.setEmailId(owner.getEmailId());
		user.setRoles(addRoles(owner.getRoles()));
		user.setUuid(owner.getUuid());

		return user;
	}

	private List<Role> addRoles(List<org.egov.common.contract.request.Role> Roles){
		LinkedList<Role> addroles = new LinkedList<>();
		Roles.forEach(role -> {
			Role addrole = new Role();
			addrole.setId(role.getId());
			addrole.setName(role.getName());
			addrole.setCode(role.getCode());
			addroles.add(addrole);
		});
		return addroles;
	}

	public Boolean isTaxPeriodAvaialble(Payment payment, TaxPeriod taxPeriod) {
		Boolean isTaxPeriodPresent = false;
		if (payment == null)
			return false;
		for (PaymentDetail paymentDetail : payment.getPaymentDetails()) {
			if (paymentDetail.getBusinessService().equalsIgnoreCase(SERVICE_FIELD_VALUE_PT)) {
				for (BillDetail billDetail : paymentDetail.getBill().getBillDetails()) {
					if (billDetail.getFromPeriod().equals(taxPeriod.getFromDate())
							&& billDetail.getToPeriod().equals(taxPeriod.getToDate())) {
						isTaxPeriodPresent = true;
					}
				}
			}
		}
		return isTaxPeriodPresent;
	}
	
	public Boolean isBetweenMonths(LocalDate date) {
		 date = LocalDate.of(2025, 6, 30);
        LocalDate startDate;
        LocalDate endDate;

        if (date.getMonthValue() >= 7) {  // July or later
            startDate = LocalDate.of(date.getYear(), 7, 1);
            endDate = LocalDate.of(date.getYear() + 1, 3, 31);
        } else {  // Before July
            startDate = LocalDate.of(date.getYear() - 1, 7, 1);
            endDate = LocalDate.of(date.getYear(), 3, 31);
        }

        return (!date.isBefore(startDate)) && (!date.isAfter(endDate));
    }

}
