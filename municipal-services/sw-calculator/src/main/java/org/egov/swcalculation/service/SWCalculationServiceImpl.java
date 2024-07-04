package org.egov.swcalculation.service;

import static org.egov.swcalculation.constants.SWCalculationConstant.Assesment_Year;
import static org.egov.swcalculation.constants.SWCalculationConstant.NEW_SEWERAGE_CONNECTION;
import static org.egov.swcalculation.constants.SWCalculationConstant.ONE_TIME_FEE_SERVICE_FIELD;
import static org.egov.swcalculation.constants.SWCalculationConstant.SERVICE_FIELD_VALUE_SW;
import static org.egov.swcalculation.constants.SWCalculationConstant.SW_ADHOC_PENALTY;
import static org.egov.swcalculation.constants.SWCalculationConstant.SW_ADHOC_REBATE;
import static org.egov.swcalculation.constants.SWCalculationConstant.SW_SEWERAGE_CESS_MASTER;
import static org.egov.swcalculation.constants.SWCalculationConstant.SW_TIME_ADHOC_PENALTY;
import static org.egov.swcalculation.constants.SWCalculationConstant.SW_TIME_ADHOC_REBATE;
import static org.egov.swcalculation.web.models.TaxHeadCategory.CHARGES;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swcalculation.config.SWCalculationConfiguration;
import org.egov.swcalculation.constants.SWCalculationConstant;
import org.egov.swcalculation.producer.SWCalculationProducer;
import org.egov.swcalculation.repository.BillGeneratorDao;
import org.egov.swcalculation.repository.SewerageCalculatorDao;
import org.egov.swcalculation.util.CalculatorUtils;
import org.egov.swcalculation.util.SWCalculationUtil;
import org.egov.swcalculation.util.SewerageCessUtil;
import org.egov.swcalculation.web.models.AdhocTaxReq;
import org.egov.swcalculation.web.models.BillGenerationSearchCriteria;
import org.egov.swcalculation.web.models.BillGeneratorReq;
import org.egov.swcalculation.web.models.BillScheduler;
import org.egov.swcalculation.web.models.BillScheduler.StatusEnum;
import org.egov.swcalculation.web.models.BulkBillCriteria;
import org.egov.swcalculation.web.models.Calculation;
import org.egov.swcalculation.web.models.CalculationCriteria;
import org.egov.swcalculation.web.models.CalculationReq;
import org.egov.swcalculation.web.models.Demand;
import org.egov.swcalculation.web.models.DemandDetail;
import org.egov.swcalculation.web.models.Property;
import org.egov.swcalculation.web.models.RequestInfoWrapper;
import org.egov.swcalculation.web.models.SewerageConnection;
import org.egov.swcalculation.web.models.SewerageConnectionRequest;
import org.egov.swcalculation.web.models.SingleDemand;
import org.egov.swcalculation.web.models.TaxHeadCategory;
import org.egov.swcalculation.web.models.TaxHeadEstimate;
import org.egov.swcalculation.web.models.TaxHeadMaster;
import org.egov.tracer.model.CustomException;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.google.common.collect.ImmutableSet;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class SWCalculationServiceImpl implements SWCalculationService {

	@Autowired
	private MasterDataService mDataService;

	@Autowired
	private EstimationService estimationService;

	@Autowired
	private PayService payService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private SewerageCalculatorDao sewerageCalculatorDao;
	
	@Autowired
	private SWCalculationUtil sWCalculationUtil;

	@Autowired
	private CalculatorUtils util;
	
	@Autowired
	private BillGeneratorDao billGeneratorDao;

	@Autowired
	private SWCalculationProducer producer;


	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private SWCalculationConfiguration configs;
	
	@Autowired
	private BillGeneratorService billGeneratorService;

	@Autowired
	private SewerageCessUtil sewerageCessUtil;

	/**
	 * Get CalculationReq and Calculate the Tax Head on Sewerage Charge
	 * @param request  calculation request
	 * @return Returns the list of Calculation objects
	 */
	public List<Calculation> getCalculation(CalculationReq request) {
		List<Calculation> calculations;
		boolean connectionRequest = false;
		Map<String, Object> masterMap;
		if (request.getIsDisconnectionRequest()!= null && request.getIsDisconnectionRequest()) {
			// Calculate and create demand for connection
			connectionRequest = request.getIsDisconnectionRequest();
			 masterMap = mDataService.loadMasterData(request.getRequestInfo(),
					request.getCalculationCriteria().get(0).getTenantId());
			calculations = getCalculations(request, masterMap);
			
		} else if (request.getIsconnectionCalculation()) {
			connectionRequest = request.getIsconnectionCalculation();
			masterMap = mDataService.loadMasterData(request.getRequestInfo(),
					request.getCalculationCriteria().get(0).getTenantId());
			calculations = getCalculations(request, masterMap);
			

		}
		else if (request.getIsReconnectionRequest())	
		{
			connectionRequest = (!request.getIsReconnectionRequest());
			masterMap = mDataService.loadExemptionMaster(request.getRequestInfo(),
					request.getCalculationCriteria().get(0).getTenantId());
			calculations = getReconnectionFeeCalculation(request, masterMap);
			log.info("In reconnection request connectionRequest" + connectionRequest);
		} 
		else {
			// Calculate and create demand for application
			masterMap = mDataService.loadExemptionMaster(request.getRequestInfo(),
					request.getCalculationCriteria().get(0).getTenantId());
			calculations = getFeeCalculation(request, masterMap);
			connectionRequest = request.getIsconnectionCalculation();
		}
		demandService.generateDemand(request, calculations, masterMap,connectionRequest);
		unsetSewerageConnection(calculations);
		return calculations;
	}

	/**
	 * 
	 * @param requestInfo - Request Info
	 * @param criteria - Criteria
	 * @param estimatesAndBillingSlabs - List of estimates
	 * @param masterMap - MDMS Master Data
	 * @return - Returns Calculation object
	 * 
	 */
	public Calculation getCalculation(RequestInfo requestInfo, CalculationCriteria criteria,
			  Map<String, List> estimatesAndBillingSlabs, Map<String, Object> masterMap, boolean isConnectionFee, boolean isLastElementWithDisconnectionRequest) {

		@SuppressWarnings("unchecked")
		List<TaxHeadEstimate> estimates = estimatesAndBillingSlabs.get("estimates");
		@SuppressWarnings("unchecked")
		List<String> billingSlabIds = estimatesAndBillingSlabs.get("billingSlabIds");
		SewerageConnection sewerageConnection = criteria.getSewerageConnection();
		
		Property property = sWCalculationUtil.getProperty(SewerageConnectionRequest.builder()
				.sewerageConnection(sewerageConnection).requestInfo(requestInfo).build());

		String tenantId = null != property.getTenantId() ? property.getTenantId() : criteria.getTenantId();

		@SuppressWarnings("unchecked")
		Map<String, TaxHeadCategory> taxHeadCategoryMap = ((List<TaxHeadMaster>) masterMap
				.get(SWCalculationConstant.TAXHEADMASTER_MASTER_KEY)).stream()
						.collect(Collectors.toMap(TaxHeadMaster::getCode, TaxHeadMaster::getCategory, (OldValue, NewValue) -> NewValue));
		BigDecimal taxAmt = BigDecimal.ZERO;
		BigDecimal sewerageCharge = BigDecimal.ZERO;
		BigDecimal penalty = BigDecimal.ZERO;
		BigDecimal exemption = BigDecimal.ZERO;
		BigDecimal rebate = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;

		Map<String, Object> financialYearMaster =  (Map<String, Object>) masterMap
				.get(SWCalculationConstant.BILLING_PERIOD);
		/*
		 * Long fromDate = (Long)
		 * financialYearMaster.get(SWCalculationConstant.STARTING_DATE_APPLICABLES);
		 * Long toDate = (Long)
		 * financialYearMaster.get(SWCalculationConstant.ENDING_DATE_APPLICABLES);
		 */
		Long fromDate=(Long) criteria.getFrom();
		Long toDate=(Long)criteria.getTo();
		
		if(isLastElementWithDisconnectionRequest) {
			if (sewerageConnection.getApplicationStatus().equalsIgnoreCase(SWCalculationConstant.PENDING_APPROVAL_FOR_DISCONNECTION)) {

				List<SewerageConnection> sewerageConnectionList = util.getSewerageConnection(requestInfo, criteria.getConnectionNo(), requestInfo.getUserInfo().getTenantId());
				for (SewerageConnection connection : sewerageConnectionList) {
					if (connection.getApplicationType().equalsIgnoreCase(NEW_SEWERAGE_CONNECTION)) {
						List<Demand> demandsList = demandService.searchDemandForDisconnectionRequest(requestInfo.getUserInfo().getTenantId(), Collections.singleton(connection.getConnectionNo()),
								null, toDate, requestInfo, null, isLastElementWithDisconnectionRequest);
						Demand demand = null;
						if (!CollectionUtils.isEmpty(demandsList)) {
							demand = demandsList.get(0);
							fromDate = (Long) demand.getTaxPeriodFrom();
							toDate = (Long) demand.getTaxPeriodTo();
							BigDecimal totalTaxAmount = BigDecimal.ZERO;
							List<DemandDetail> demandDetails = demand.getDemandDetails();
							for (DemandDetail demandDetail : demandDetails) {
								totalTaxAmount = totalTaxAmount.add(demandDetail.getTaxAmount());
							}
							Integer taxPeriod = Math.round((toDate - fromDate) / 86400000);
							Long daysOfUsage = Math.round(Math.abs(Double.parseDouble(toDate.toString()) - sewerageConnection.getDateEffectiveFrom()) / 86400000);
							BigDecimal finalSewerageCharge = sewerageCharge.add(BigDecimal.valueOf(
									(Double.parseDouble(totalTaxAmount.toString()) * daysOfUsage) / taxPeriod));

							criteria.setTo(sewerageConnection.getDateEffectiveFrom());
							criteria.setFrom(toDate);

							estimates.stream().forEach(estimate -> {
								if (taxHeadCategoryMap.containsKey(estimate.getTaxHeadCode())) {
									if (taxHeadCategoryMap.get(estimate.getTaxHeadCode()).equals(CHARGES)) {
										estimate.setEstimateAmount(finalSewerageCharge);
									}
								}
							});
						}
					}
				}

			}
		}

		for (TaxHeadEstimate estimate : estimates) {

			TaxHeadCategory category = taxHeadCategoryMap.get(estimate.getTaxHeadCode());
			estimate.setCategory(category);

			if (category != null){
			switch (category) {

			case CHARGES:
				sewerageCharge = sewerageCharge.add(estimate.getEstimateAmount());
				break;

			case PENALTY:
				penalty = penalty.add(estimate.getEstimateAmount());
				break;

			case REBATE:
				rebate = rebate.add(estimate.getEstimateAmount());
				break;

			case EXEMPTION:
				exemption = exemption.add(estimate.getEstimateAmount());
				break;

			case FEE:
				fee = fee.add(estimate.getEstimateAmount());
				break;

			default:
				taxAmt = taxAmt.add(estimate.getEstimateAmount());
				break;
			}
		}
		}

		TaxHeadEstimate decimalEstimate = payService.roundOfDecimals(taxAmt.add(penalty).add(fee).add(sewerageCharge),
				rebate.add(exemption), isConnectionFee);
		if (null != decimalEstimate) {
			decimalEstimate.setCategory(taxHeadCategoryMap.get(decimalEstimate.getTaxHeadCode()));
			estimates.add(decimalEstimate);
			if (decimalEstimate.getEstimateAmount().compareTo(BigDecimal.ZERO) >= 0)
				taxAmt = taxAmt.add(decimalEstimate.getEstimateAmount());
			else
				rebate = rebate.add(decimalEstimate.getEstimateAmount());
		}

		BigDecimal totalAmount = taxAmt.add(penalty).add(rebate).add(exemption).add(sewerageCharge).add(fee);
		return Calculation.builder().totalAmount(totalAmount).taxAmount(taxAmt).penalty(penalty).exemption(exemption)
				.charge(sewerageCharge).fee(fee).sewerageConnection(sewerageConnection).rebate(rebate).taxPeriodFrom(fromDate).taxPeriodTo(toDate)
				.tenantId(criteria.getTenantId()).taxHeadEstimates(estimates).billingSlabIds(billingSlabIds)
				.connectionNo(criteria.getConnectionNo()).applicationNO(criteria.getApplicationNo()).build();
	}

	/**
	 * Generate Demand Based on Time (Monthly, Quarterly, Yearly)
	 */
	public void generateDemandBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		log.info("Time schedule start for sewerage demand generation on : " + date.format(dateTimeFormatter));
//		List<String> tenantIds = sewerageCalculatorDao.getTenantId();
		List<String> tenantIds = new ArrayList<>();
		String tenat = requestInfo.getMsgId();
		
		 if (!tenat.contains("pb"))
		{
		tenantIds.add("pb.abohar");
		tenantIds.add("pb.adampur");
		tenantIds.add("pb.ahmedgarh");
		tenantIds.add("pb.ajnala");
		tenantIds.add("pb.alawalpur");
		tenantIds.add("pb.amargarh");
		tenantIds.add("pb.amloh");
		tenantIds.add("pb.amritsar");
		tenantIds.add("pb.anandpursahib");
		tenantIds.add("pb.badhnikalan");
		tenantIds.add("pb.baghapurana");
		tenantIds.add("pb.balachaur");
		tenantIds.add("pb.banga");
		tenantIds.add("pb.banur");
		tenantIds.add("pb.bareta");
		tenantIds.add("pb.bariwala");
		tenantIds.add("pb.barnala");
		tenantIds.add("pb.bassipathana");
		tenantIds.add("pb.batala");
		tenantIds.add("pb.begowal");
		tenantIds.add("pb.bhadaur");
		tenantIds.add("pb.bhadson");
		tenantIds.add("pb.bhawanigarh");
		tenantIds.add("pb.bhikhi");
		tenantIds.add("pb.bhogpur");
		tenantIds.add("pb.bhuchomandi");
		tenantIds.add("pb.bhulath");
		tenantIds.add("pb.boha");
		tenantIds.add("pb.budhlada");
		tenantIds.add("pb.chamkaursahib");
		tenantIds.add("pb.cheema");
		tenantIds.add("pb.dasuya");
		tenantIds.add("pb.derababananak");
		tenantIds.add("pb.derabassi");
		tenantIds.add("pb.dhanaula");
		tenantIds.add("pb.dharamkot");
		tenantIds.add("pb.dhariwal");
		tenantIds.add("pb.dhilwan");
		tenantIds.add("pb.dhuri");
		tenantIds.add("pb.dinanagar");
		tenantIds.add("pb.dirba");
		tenantIds.add("pb.doraha");
		tenantIds.add("pb.faridkot");
		tenantIds.add("pb.fatehgarhchurian");
		tenantIds.add("pb.fazilka");
		tenantIds.add("pb.ferozepur");
		tenantIds.add("pb.garhdiwala");
		tenantIds.add("pb.garhshankar");
		tenantIds.add("pb.ghagga");
		tenantIds.add("pb.ghanaur");
		tenantIds.add("pb.gidderbaha");
		tenantIds.add("pb.goniana");
		tenantIds.add("pb.goraya");
		tenantIds.add("pb.gurdaspur");
		tenantIds.add("pb.guruharsahai");
		tenantIds.add("pb.handiaya");
		tenantIds.add("pb.hariana");
		tenantIds.add("pb.hoshiarpur");
		tenantIds.add("pb.jagraon");
		tenantIds.add("pb.jaitu");
		tenantIds.add("pb.jalalabad");
		tenantIds.add("pb.jalandhar");
		tenantIds.add("pb.jandialaguru");
		tenantIds.add("pb.kapurthala");
		tenantIds.add("pb.kartarpur");
		tenantIds.add("pb.khamano");
		tenantIds.add("pb.khanauri");
		tenantIds.add("pb.khanna");
		tenantIds.add("pb.kharar");
		tenantIds.add("pb.khemkaran");
		tenantIds.add("pb.kiratpursahib");
		tenantIds.add("pb.kotfatta");
		tenantIds.add("pb.kotkapura");
		tenantIds.add("pb.kurali");
		tenantIds.add("pb.lalru");
		tenantIds.add("pb.lehragaga");
		tenantIds.add("pb.longowal");
		tenantIds.add("pb.machhiwara");
		tenantIds.add("pb.mahilpur");
		tenantIds.add("pb.majitha");
		tenantIds.add("pb.makhu");
		tenantIds.add("pb.malerkotla");
		tenantIds.add("pb.mallanwala");
		tenantIds.add("pb.maloud");
		tenantIds.add("pb.malout");
		tenantIds.add("pb.mamdot");
		tenantIds.add("pb.mandigobindgarh");
		tenantIds.add("pb.mansa");
		tenantIds.add("pb.maur");
		tenantIds.add("pb.moga");
		tenantIds.add("pb.mohali");
		tenantIds.add("pb.moonak");
		tenantIds.add("pb.morinda");
		tenantIds.add("pb.mudki");
		tenantIds.add("pb.mukerian");
		tenantIds.add("pb.mullanpurdakha");
		tenantIds.add("pb.nabha");
		tenantIds.add("pb.nadala");
		tenantIds.add("pb.nakodar");
		tenantIds.add("pb.nangal");
		tenantIds.add("pb.nawanshahr");
		tenantIds.add("pb.nihalsinghwala");
		tenantIds.add("pb.nurmahal");
		tenantIds.add("pb.pathankot");
		tenantIds.add("pb.patiala");
		tenantIds.add("pb.patran");
		tenantIds.add("pb.patti");
		tenantIds.add("pb.payal");
		tenantIds.add("pb.phagwara");
		tenantIds.add("pb.phillaur");
		tenantIds.add("pb.quadian");
		tenantIds.add("pb.rahon");
		tenantIds.add("pb.raikot");
		tenantIds.add("pb.rajasansi");
		tenantIds.add("pb.rajpura");
		tenantIds.add("pb.raman");
		tenantIds.add("pb.ramdass");
		tenantIds.add("pb.rampuraphul");
		tenantIds.add("pb.rayya");
		tenantIds.add("pb.ropar");
		tenantIds.add("pb.samana");
		tenantIds.add("pb.samrala");
		tenantIds.add("pb.sanaur");
		tenantIds.add("pb.sangatmandi");
		tenantIds.add("pb.sangrur");
		tenantIds.add("pb.sardulgarh");
		tenantIds.add("pb.shahkot");
		tenantIds.add("pb.shamchurasi");
		tenantIds.add("pb.sirhind");
		tenantIds.add("pb.srihargobindpur");
		tenantIds.add("pb.sujanpur");
		tenantIds.add("pb.sultanpurlodhi");
		tenantIds.add("pb.sunam");
		tenantIds.add("pb.talwandisabo");
		tenantIds.add("pb.talwandibhai");
		tenantIds.add("pb.talwara");
		tenantIds.add("pb.tapa");
		tenantIds.add("pb.tarntaran");
		tenantIds.add("pb.urmartanda");
		tenantIds.add("pb.zira");
		tenantIds.add("pb.zirakpur");
		tenantIds.add("pb.itpatiala");
		}
		else {
			tenantIds.add(tenat);
			
		}
		
		if (tenantIds.isEmpty())
			return;
		log.info("Tenant Ids : " + tenantIds.toString());
		for (String tenantId : tenantIds) {
			try {
				demandService.generateDemandForTenantId(tenantId, requestInfo);
			} catch (Exception e) {
				log.error("Exception occurred while generating demand for tenant: {} : " , tenantId);
				log.error("Exception: {} : " , e);
				e.printStackTrace();
				continue;

			}
		}
	}

	public List<SewerageConnection> getConnnectionWithPendingDemand(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria)
	{
		return demandService.getConnectionPendingForDemand(requestInfo,bulkBillCriteria);
	}
	
	public String generateDemandForConsumerCodeBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		log.info("Going to generate Demand of Consumer Code" + bulkBillCriteria.getConsumerCode());
		
		if (bulkBillCriteria.getTenantId()==null)
			return "Tenant Id is mandatory!!";
		log.info("Tenant Ids : " + bulkBillCriteria.getTenantId());
			return demandService.generateDemandForConsumerCode(requestInfo,bulkBillCriteria);
	
	}
	
	/**
	 * 
	 * @param request
	 *            would be calculations request
	 * @param masterMap
	 *            master data
	 * @return all calculations including sewerage charge and taxhead on that
	 */
	public List<Calculation> getCalculations(CalculationReq request, Map<String, Object> masterMap) {
		List<Calculation> calculations = new ArrayList<>(request.getCalculationCriteria().size());
		for (CalculationCriteria criteria : request.getCalculationCriteria()) {
			Map<String, List> estimationMap = estimationService.getEstimationMap(criteria, request,
					masterMap);
			ArrayList<?> billingFrequencyMap = (ArrayList<?>) masterMap
					.get(SWCalculationConstant.Billing_Period_Master);
			mDataService.enrichBillingPeriod(criteria, billingFrequencyMap, masterMap, criteria.getSewerageConnection().getConnectionType());

			Calculation calculation = null;
			if (request.getIsDisconnectionRequest() != null && request.getIsDisconnectionRequest()) {
				if (request.getIsDisconnectionRequest() && criteria.getApplicationNo().equals(request.getCalculationCriteria().get(request.getCalculationCriteria().size() - 1)
								.getApplicationNo())) {
					calculation = getCalculation(request.getRequestInfo(), criteria, estimationMap, masterMap, true, true);
				}
			}  else if (request.getIsReconnectionRequest() != null && request.getIsReconnectionRequest()) {
				if (request.getIsReconnectionRequest() &&
						criteria.getApplicationNo().equals(request.getCalculationCriteria().get(request.getCalculationCriteria().size() - 1)
								.getApplicationNo())) {
					calculation = getCalculation(request.getRequestInfo(), criteria, estimationMap, masterMap, true, false);
				}
				
			} 
			else {
				calculation = getCalculation(request.getRequestInfo(), criteria, estimationMap, masterMap, true, false);
			}
			calculations.add(calculation);
		}
		return calculations;
	}

	List<Calculation> getReconnectionFeeCalculation(CalculationReq request, Map<String, Object> masterMap) {
		List<Calculation> calculations = new ArrayList<>(request.getCalculationCriteria().size());
		for (CalculationCriteria criteria : request.getCalculationCriteria()) {
			Map<String, List> estimationMap = estimationService.getReconnectionFeeEstimation(criteria, request.getRequestInfo(),
					masterMap);
			mDataService.enrichBillingPeriodForFee(masterMap);
			Calculation calculation = getCalculation(request.getRequestInfo(), criteria, estimationMap, masterMap,
					false, false);
			calculations.add(calculation);
		}
		return calculations;
	}
	/**
	 * 
	 * 
	 * @param request - Calculation Request
	 * @return List of calculation.
	 */
	public List<Calculation> bulkDemandGeneration(CalculationReq request, Map<String, Object> masterMap) {
		List<Calculation> calculations = getCalculations(request, masterMap);
		demandService.generateDemand(request, calculations, masterMap, true);
		return calculations;
	}

	/**
	 * 
	 * @param request - Calculation Request
	 * @return list of calculation based on request
	 */
	public List<Calculation> getEstimation(CalculationReq request) {
		Map<String, Object> masterData = mDataService.loadExemptionMaster(request.getRequestInfo(),
				request.getCalculationCriteria().get(0).getTenantId());
		List<Calculation> calculations = getFeeCalculation(request, masterData);
		unsetSewerageConnection(calculations);
		return calculations;
	}

	/**
	 * 
	 * @param request - Calculation Request
	 * @param masterMap - MDMS Master Data
	 * @return list of calculation based on estimation criteria
	 */
	List<Calculation> getFeeCalculation(CalculationReq request, Map<String, Object> masterMap) {
		List<Calculation> calculations = new ArrayList<>(request.getCalculationCriteria().size());
		for (CalculationCriteria criteria : request.getCalculationCriteria()) {
			Map<String, List> estimationMap = estimationService.getFeeEstimation(criteria, request.getRequestInfo(),
					masterMap);
			mDataService.enrichBillingPeriodForFee(masterMap);
			Calculation calculation = getCalculation(request.getRequestInfo(), criteria, estimationMap, masterMap,
					false, false);
			calculations.add(calculation);
		}
		return calculations;
	}

	public void unsetSewerageConnection(List<Calculation> calculation) {
		calculation.forEach(cal -> cal.setSewerageConnection(null));
	}
	
	/**
	 * Add adhoc tax to demand
	 * @param adhocTaxReq - Adhoc Tax Request Object
	 * @return List of Calculation
	 */
	public List<Calculation> applyAdhocTax(AdhocTaxReq adhocTaxReq) {
		List<TaxHeadEstimate> estimates = new ArrayList<>();
		String businessService = adhocTaxReq.getBusinessService();
		if(!businessService.equalsIgnoreCase(SERVICE_FIELD_VALUE_SW) && !businessService.equalsIgnoreCase(ONE_TIME_FEE_SERVICE_FIELD))
			throw new CustomException("INVALID_BUSINESSSERVICE", "Provide businessService is invalid");

		if (!(adhocTaxReq.getAdhocpenalty().compareTo(BigDecimal.ZERO) == 0)){
			String penaltyTaxhead = businessService.equals(SERVICE_FIELD_VALUE_SW) ? SW_TIME_ADHOC_PENALTY : SW_ADHOC_PENALTY;
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(penaltyTaxhead)
					.estimateAmount(adhocTaxReq.getAdhocpenalty().setScale(2, 2)).build());
		}
		if (!(adhocTaxReq.getAdhocrebate().compareTo(BigDecimal.ZERO) == 0)){
			String rebateTaxhead = businessService.equals(SERVICE_FIELD_VALUE_SW) ? SW_TIME_ADHOC_REBATE : SW_ADHOC_REBATE;
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(rebateTaxhead)
					.estimateAmount(adhocTaxReq.getAdhocrebate().setScale(2, 2).negate()).build());
		}

		Calculation calculation = Calculation.builder()
				.tenantId(adhocTaxReq.getRequestInfo().getUserInfo().getTenantId())
				.connectionNo(adhocTaxReq.getConsumerCode()).taxHeadEstimates(estimates).build();
		List<Calculation> calculations = Collections.singletonList(calculation);
		return demandService.updateDemandForAdhocTax(adhocTaxReq.getRequestInfo(), calculations, businessService);
	}

	/**
	 * Calculate SewerageCess during Disconnection Application final Sewerage charge calculation
	 *
	 * @param masterMap
	 * @return sewerageCess - SewerageCess amount
	 */
	private BigDecimal getSewerageCessForDisconnection(Map<String, Object> masterMap, BigDecimal finalSewerageCharge) {
		BigDecimal sewerageCess = BigDecimal.ZERO;
		Map<String, JSONArray> timeBasedExemptionMasterMap = new HashMap<>();
		timeBasedExemptionMasterMap.put(SW_SEWERAGE_CESS_MASTER,
				(JSONArray) (masterMap.getOrDefault(SWCalculationConstant.SW_SEWERAGE_CESS_MASTER, null)));

		if (timeBasedExemptionMasterMap.get(SWCalculationConstant.SW_SEWERAGE_CESS_MASTER) != null) {
			List<Object> sewerageCessMasterList = timeBasedExemptionMasterMap
					.get(SWCalculationConstant.SW_SEWERAGE_CESS_MASTER);

			Map<String, Object> CessMap = mDataService.getApplicableMasterCess(Assesment_Year, sewerageCessMasterList);
			sewerageCess = sewerageCessUtil.calculateSewerageCess(finalSewerageCharge, CessMap);

		}
		return sewerageCess;
	}
	
	public void generateSingleDemand(SingleDemand singledemand) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		log.info("Time schedule start for sewerage demand generation on : " + date.format(dateTimeFormatter));
//		List<String> tenantIds = wSCalculationDao.getTenantId();
		List<String> tenantIds = new ArrayList<>();
		String tenat = singledemand.getTenantId();
		tenantIds.add(tenat);
		if (tenantIds.isEmpty()) {
			log.info("No tenants are found for generating demand");
			return;
		}
		log.info("Tenant Ids : " + tenantIds.toString());
		tenantIds.forEach(tenantId -> {
			try {

				demandService.SingleDemandGenerate(tenantId, singledemand);

			} catch (Exception e) {
				log.error("Exception occured while generating demand for tenant: " + tenantId);
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * Generate bill Based on Time (Monthly, Quarterly, Yearly)
	 */
	public void generateBillBasedLocality(RequestInfo requestInfo) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		log.info("Time schedule start for water bill generation on : " + date.format(dateTimeFormatter));

		BillGenerationSearchCriteria criteria = new BillGenerationSearchCriteria();
		criteria.setStatus(SWCalculationConstant.INITIATED_CONST);

		List<BillScheduler> billSchedularList = billGeneratorService.getBillGenerationDetails(criteria);
		if (billSchedularList != null && billSchedularList.isEmpty())
			return;
		log.info("billSchedularList count : " + billSchedularList.size());
		for (BillScheduler billSchedular : billSchedularList) {
			try {
				billGeneratorDao.updateBillSchedularStatus(billSchedular.getId(), StatusEnum.INPROGRESS);

				requestInfo.getUserInfo().setTenantId(billSchedular.getTenantId() != null ? billSchedular.getTenantId() : requestInfo.getUserInfo().getTenantId());
				RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

				List<String> connectionNos = sewerageCalculatorDao.getConnectionsNoByLocality( billSchedular.getTenantId(), SWCalculationConstant.nonMeterdConnection, billSchedular.getLocality());

				//testing purpose added three consumercodes
//				connectionNos.add("0603000001");
//				connectionNos.add("0603000002");
//				connectionNos.add("0603009718");
				
				if (connectionNos == null || connectionNos.isEmpty()) {
					billGeneratorDao.updateBillSchedularStatus(billSchedular.getId(), StatusEnum.COMPLETED);
					continue;
				}

				Collection<List<String>> partitionConectionNoList = partitionBasedOnSize(connectionNos, configs.getBulkBillGenerateCount());
				int threadSleepCount = 1;
				
				log.info("partitionConectionNoList size: {}, Producer ConsumerCodes size : {} and BulkBillGenerateCount: {}",partitionConectionNoList.size(), connectionNos.size(), configs.getBulkBillGenerateCount());
				int count = 1;

				for (List<String>  conectionNoList : partitionConectionNoList) {

					BillGeneratorReq billGeneraterReq = BillGeneratorReq
							.builder()
							.requestInfoWrapper(requestInfoWrapper)
							.tenantId(billSchedular.getTenantId())
							.consumerCodes(ImmutableSet.copyOf(conectionNoList))
							.billSchedular(billSchedular)
							.build();

					producer.push(configs.getBillGenerateSchedulerTopic(), billGeneraterReq);
					log.info("Bill Scheduler pushed connections size:{} to kafka topic of batch no: ", conectionNoList.size(), count++);

					if(threadSleepCount == 2) {
						//Pausing controller for every three batches.
						Thread.sleep(10000);
						threadSleepCount=1;
					}
					threadSleepCount++;
				}
				billGeneratorDao.updateBillSchedularStatus(billSchedular.getId(), StatusEnum.COMPLETED);

			}catch (Exception e) {
				e.printStackTrace();
				log.error("Execptio occured while generating bills for tenant"+billSchedular.getTenantId()+" and locality: "+billSchedular.getLocality());
			}

		}
	}
	
	static <T> Collection<List<T>> partitionBasedOnSize(List<T> inputList, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        return inputList.stream()
                    .collect(Collectors.groupingBy(s -> counter.getAndIncrement()/size))
                    .values();
	}

//	@Override
//	public void generateDemandBasedOnTimePeriod(RequestInfo requestInfo) {
//		// TODO Auto-generated method stub
		
//	}


}
