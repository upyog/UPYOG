package org.egov.wscalculation.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.egov.wscalculation.web.models.BillScheduler.StatusEnum;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.config.WSCalculationConfiguration;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.producer.WSCalculationProducer;
import org.egov.wscalculation.repository.BillGeneratorDao;
import org.egov.wscalculation.util.WaterCessUtil;
import org.egov.wscalculation.web.models.*;
import org.egov.wscalculation.repository.ServiceRequestRepository;
import org.egov.wscalculation.repository.WSCalculationDao;
import org.egov.wscalculation.util.CalculatorUtil;
import org.egov.wscalculation.util.WSCalculationUtil;
import org.egov.wscalculation.web.models.BillScheduler.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.ImmutableSet;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import static org.egov.wscalculation.constants.WSCalculationConstant.*;
import static org.egov.wscalculation.web.models.TaxHeadCategory.CHARGES;

@Service
@Slf4j
public class WSCalculationServiceImpl implements WSCalculationService {

	@Autowired
	private PayService payService;

	@Autowired
	private EstimationService estimationService;
	
	@Autowired
	private CalculatorUtil calculatorUtil;
	
	@Autowired
	private DemandService demandService;
	
	@Autowired
	private MasterDataService masterDataService; 

	@Autowired
	private WSCalculationDao wSCalculationDao;
	
	@Autowired
	private ServiceRequestRepository repository;
	
	@Autowired
	private WSCalculationUtil wSCalculationUtil;
	
	
	@Autowired
	private BillGeneratorService billGeneratorService;
	
	@Autowired
	private WSCalculationProducer producer;
	
	@Autowired
	private WSCalculationConfiguration configs;
	
	@Autowired
	private BillGeneratorDao billGeneratorDao;

	@Autowired
	private CalculatorUtil util;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WaterCessUtil waterCessUtil;

	@Autowired
	private MasterDataService mDataService;


	/**
	 * Get CalculationReq and Calculate the Tax Head on Water Charge And Estimation Charge
	 */
	public List<Calculation> getCalculation(CalculationReq request) {
		List<Calculation> calculations;

		Map<String, Object> masterMap;
		boolean connectionRequest = false;
		//Calculate and create demand for connection
		if (request.getIsDisconnectionRequest() != null && request.getIsDisconnectionRequest()) {
			MeterReadingSearchCriteria meterCriteria = new MeterReadingSearchCriteria();
			meterCriteria.setTenantId(request.getCalculationCriteria().get(0).getTenantId());
			meterCriteria.setConnectionNos(Collections.singleton(request.getCalculationCriteria().get(0).getConnectionNo()));
			List<MeterReading> meterreadingList = wSCalculationDao.searchMeterReadings(meterCriteria);
			if (!meterreadingList.isEmpty()) {
				request.getCalculationCriteria().get(0).setLastReading(meterreadingList.get(0).getLastReading());
				request.getCalculationCriteria().get(0).setCurrentReading(meterreadingList.get(0).getCurrentReading());
				SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
				String dates[] = meterreadingList.get(0).getBillingPeriod().split("-");
				try {
					Date startDate = f.parse(dates[0]);
					request.getCalculationCriteria().get(0).setFrom(startDate.getTime() + ONE_DAY_ADDON);
					Date endDate = f.parse(dates[1]);
					request.getCalculationCriteria().get(0).setTo(endDate.getTime() + ONE_DAY_ADDON);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
			connectionRequest = request.getIsDisconnectionRequest();
			masterMap = masterDataService.loadMasterData(request.getRequestInfo(),
					request.getCalculationCriteria().get(0).getTenantId());
			calculations = getCalculations(request, masterMap);
		} else if (request.getIsconnectionCalculation()) {
			connectionRequest = request.getIsconnectionCalculation();
			masterMap = masterDataService.loadMasterData(request.getRequestInfo(),
					request.getCalculationCriteria().get(0).getTenantId());
			calculations = getCalculations(request, masterMap);
		}
		else if (request.getIsReconnectionRequest()) {
			connectionRequest = (!request.getIsReconnectionRequest());
			masterMap = masterDataService.loadExemptionMaster(request.getRequestInfo(),
					request.getCalculationCriteria().get(0).getTenantId());
			calculations = getReconnectionFeeCalculation(request, masterMap);
			log.info("In reconnection request connectionRequest" + connectionRequest);
		}
		else {
			//Calculate and create demand for application
			masterMap = masterDataService.loadExemptionMaster(request.getRequestInfo(),
					request.getCalculationCriteria().get(0).getTenantId());
			calculations = getFeeCalculation(request, masterMap);
			connectionRequest = request.getIsconnectionCalculation();
		}
		demandService.generateDemand(request, calculations, masterMap, connectionRequest);
		unsetWaterConnection(calculations);
		return calculations;
	}
	
	
	/**
	 * 
	 * 
	 * @param request - Calculation Request Object
	 * @return List of calculation.
	 */
	public List<Calculation> bulkDemandGeneration(CalculationReq request, Map<String, Object> masterMap) {
		List<Calculation> calculations = getCalculations(request, masterMap);
		demandService.generateDemandForBillingCycleInBulk(request, calculations, masterMap, true);
		return calculations;
	}

	/**
	 * 
	 * @param request - Calculation Request Object
	 * @return list of calculation based on request
	 */
	public List<Calculation> getEstimation(CalculationReq request) {
		Map<String, Object> masterData = masterDataService.loadExemptionMaster(request.getRequestInfo(),
				request.getCalculationCriteria().get(0).getTenantId());
		List<Calculation> calculations = getFeeCalculation(request, masterData);
		unsetWaterConnection(calculations);
		return calculations;
	}
	
	/**
	 * It will take calculation and return calculation with tax head code 
	 * 
	 * @param requestInfo Request Info Object
	 * @param criteria Calculation criteria on meter charge
	 * @param estimatesAndBillingSlabs Billing Slabs
	 * @param masterMap Master MDMS Data
	 * @return Calculation With Tax head
	 */
	public Calculation getCalculation(RequestInfo requestInfo, CalculationCriteria criteria,
									  Map<String, List> estimatesAndBillingSlabs, Map<String, Object> masterMap, boolean isConnectionFee, boolean isLastElementWithDisconnectionRequest) {

		@SuppressWarnings("unchecked")
		List<TaxHeadEstimate> estimates = estimatesAndBillingSlabs.get("estimates");
		@SuppressWarnings("unchecked")
		List<String> billingSlabIds = estimatesAndBillingSlabs.get("billingSlabIds");
		WaterConnection waterConnection = criteria.getWaterConnection();
		
		@SuppressWarnings("unchecked")
		List<TaxHeadMaster> ll=((List<TaxHeadMaster>) masterMap
				.get(WSCalculationConstant.TAXHEADMASTER_MASTER_KEY));
		Map<String, TaxHeadCategory> taxHeadCategoryMap = ll.stream()
						.collect(Collectors.toMap(TaxHeadMaster::getCode, TaxHeadMaster::getCategory, (OldValue, NewValue) -> NewValue));

		BigDecimal taxAmt = BigDecimal.ZERO;
		BigDecimal waterCharge = BigDecimal.ZERO;
		BigDecimal penalty = BigDecimal.ZERO;
		BigDecimal exemption = BigDecimal.ZERO;
		BigDecimal rebate = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;

		Map<String, Object> financialYearMaster =  (Map<String, Object>) masterMap
				.get(WSCalculationConstant.BILLING_PERIOD);
		Long fromDate = (Long) financialYearMaster.get(WSCalculationConstant.STARTING_DATE_APPLICABLES);
		Long toDate = (Long) financialYearMaster.get(WSCalculationConstant.ENDING_DATE_APPLICABLES);
		if(isLastElementWithDisconnectionRequest) {
			if (waterConnection.getApplicationStatus().equalsIgnoreCase(WSCalculationConstant.PENDING_APPROVAL_FOR_DISCONNECTION)) {

				Map<String, Object> finalMap = new HashMap<>();
				List<WaterConnection> waterConnectionList = calculatorUtil.getWaterConnection(requestInfo, criteria.getConnectionNo(), requestInfo.getUserInfo().getTenantId());
				for (WaterConnection connection : waterConnectionList) {
					if (connection.getApplicationType().equalsIgnoreCase(NEW_WATER_CONNECTION)) {
						List<Demand> demandsList = demandService.searchDemandForDisconnectionRequest(requestInfo.getUserInfo().getTenantId(), Collections.singleton(connection.getConnectionNo()),
								null,	toDate, requestInfo, null, isLastElementWithDisconnectionRequest);
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
							Long daysOfUsage = Math.round(Math.abs(Double.parseDouble(toDate.toString()) - waterConnection.getDateEffectiveFrom()) / 86400000);
							BigDecimal finalWaterCharge = waterCharge.add(BigDecimal.valueOf(
									(Double.parseDouble(totalTaxAmount.toString()) * daysOfUsage) / taxPeriod));
							criteria.setTo(waterConnection.getDateEffectiveFrom());
							criteria.setFrom(toDate);

							//Calculate water cess for disconnection charge
							BigDecimal waterCess = getWaterCessForDisconnection(masterMap, finalWaterCharge);
							for (TaxHeadEstimate estimate : estimates) {
								if (estimate.getTaxHeadCode().equals(WS_WATER_CESS)) {
									estimates.remove(estimate);
									break;
								}
							}
							estimates.add(TaxHeadEstimate.builder().taxHeadCode(WSCalculationConstant.WS_WATER_CESS)
									.estimateAmount(waterCess.setScale(2, 2)).build());
							estimates.stream().forEach(estimate -> {
								if (taxHeadCategoryMap.containsKey(estimate.getTaxHeadCode())) {
									if (taxHeadCategoryMap.get(estimate.getTaxHeadCode()).equals(CHARGES)) {
										estimate.setEstimateAmount(finalWaterCharge);
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
if(category!=null)
			switch (category) {

			case CHARGES:
				waterCharge = waterCharge.add(estimate.getEstimateAmount());
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
		TaxHeadEstimate decimalEstimate = payService.roundOfDecimals(taxAmt.add(penalty).add(waterCharge).add(fee),
				rebate.add(exemption), isConnectionFee);
		if (null != decimalEstimate) {
			decimalEstimate.setCategory(taxHeadCategoryMap.get(decimalEstimate.getTaxHeadCode()));
			estimates.add(decimalEstimate);
			if (decimalEstimate.getEstimateAmount().compareTo(BigDecimal.ZERO) >= 0)
				taxAmt = taxAmt.add(decimalEstimate.getEstimateAmount());
			else
				rebate = rebate.add(decimalEstimate.getEstimateAmount());
		}

		BigDecimal totalAmount = taxAmt.add(penalty).add(rebate).add(exemption).add(waterCharge).add(fee);
		return Calculation.builder().totalAmount(totalAmount).taxAmount(taxAmt).penalty(penalty).exemption(exemption)
				.charge(waterCharge).fee(fee).waterConnection(waterConnection).rebate(rebate).tenantId(criteria.getTenantId())
				.taxHeadEstimates(estimates).billingSlabIds(billingSlabIds).connectionNo(criteria.getConnectionNo()).applicationNO(criteria.getApplicationNo())
				.build();
	}
	
	/**
	 * 
	 * @param request would be calculations request
	 * @param masterMap master data
	 * @return all calculations including water charge and taxhead on that
	 */
	public List<Calculation> getCalculations(CalculationReq request, Map<String, Object> masterMap) {
		List<Calculation> calculations = new ArrayList<>(request.getCalculationCriteria().size());
		for (CalculationCriteria criteria : request.getCalculationCriteria()) {
			Map<String, List> estimationMap = estimationService.getEstimationMap(criteria, request,
					masterMap);
			ArrayList<?> billingFrequencyMap = (ArrayList<?>) masterMap
					.get(WSCalculationConstant.Billing_Period_Master);
			masterDataService.enrichBillingPeriod(criteria, billingFrequencyMap, masterMap, criteria.getWaterConnection().getConnectionType());

			Calculation calculation = null;

			if (request.getIsDisconnectionRequest() != null && request.getIsDisconnectionRequest() ) {
				if (request.getIsDisconnectionRequest() &&
						criteria.getApplicationNo().equals(request.getCalculationCriteria().get(request.getCalculationCriteria().size() - 1)
								.getApplicationNo())) {
					calculation = getCalculation(request.getRequestInfo(), criteria, estimationMap, masterMap, true, true);
				}
			} else if (request.getIsReconnectionRequest() != null && request.getIsReconnectionRequest()) {
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


	@Override
	public void jobScheduler() {
		// TODO Auto-generated method stub
		ArrayList<String> tenantIds = wSCalculationDao.searchTenantIds();

		for (String tenantId : tenantIds) {
			RequestInfo requestInfo = new RequestInfo();
			User user = new User();
			user.setTenantId(tenantId);
			requestInfo.setUserInfo(user);
			String jsonPath = WSCalculationConstant.JSONPATH_ROOT_FOR_BilingPeriod;
			MdmsCriteriaReq mdmsCriteriaReq = calculatorUtil.getBillingFrequency(requestInfo, tenantId);
			StringBuilder url = calculatorUtil.getMdmsSearchUrl();
			Object res = repository.fetchResult(url, mdmsCriteriaReq);
			if (res == null) {
				throw new CustomException("MDMS_ERROR_FOR_BILLING_FREQUENCY",
						"ERROR IN FETCHING THE BILLING FREQUENCY");
			}
			ArrayList<?> mdmsResponse = JsonPath.read(res, jsonPath);
			getBillingPeriod(mdmsResponse, requestInfo, tenantId);
		}
	}
	

	@SuppressWarnings("unchecked")
	public void getBillingPeriod(ArrayList<?> mdmsResponse, RequestInfo requestInfo, String tenantId) {
		log.info("Billing Frequency Map" + mdmsResponse.toString());
		Map<String, Object> master = (Map<String, Object>) mdmsResponse.get(0);
		LocalDateTime demandStartingDate = LocalDateTime.now();
		Long demandGenerateDateMillis = (Long) master.get(WSCalculationConstant.Demand_Generate_Date_String);

		String connectionType = "Non-metred";

		if (demandStartingDate.getDayOfMonth() == (demandGenerateDateMillis) / 86400) {

			ArrayList<String> connectionNos = wSCalculationDao.searchConnectionNos(connectionType, tenantId);
			for (String connectionNo : connectionNos) {

				CalculationReq calculationReq = new CalculationReq();
				CalculationCriteria calculationCriteria = new CalculationCriteria();
				calculationCriteria.setTenantId(tenantId);
				calculationCriteria.setConnectionNo(connectionNo);

				List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
				calculationCriteriaList.add(calculationCriteria);

				calculationReq.setRequestInfo(requestInfo);
				calculationReq.setCalculationCriteria(calculationCriteriaList);
				calculationReq.setIsconnectionCalculation(true);
				getCalculation(calculationReq);

			}
		}
	}

	/**
	 * Generate Demand Based on Time (Monthly, Quarterly, Yearly)
	 */
	public void generateDemandBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		log.info("Time schedule start for water demand generation on : " + date.format(dateTimeFormatter));
//		List<String> tenantIds = wSCalculationDao.getTenantId();
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
		if (tenantIds.isEmpty()) {
			log.info("No tenants are found for generating demand");
			return;
		}
		log.info("Tenant Ids : " + tenantIds.toString());
		tenantIds.forEach(tenantId -> {
			try {
			
				demandService.generateDemandForTenantId(tenantId, requestInfo);
				
			} catch (Exception e) {
				log.error("Exception occured while generating demand for tenant: " + tenantId);
				e.printStackTrace();
			}
		});
	}
	
	public List<WaterConnection> getConnnectionWithPendingDemand(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria)
	{
		return demandService.getConnectionPendingForDemand(requestInfo,bulkBillCriteria.getTenantId());
	}
	
	public String generateDemandForConsumerCodeBasedOnTimePeriod(RequestInfo requestInfo, BulkBillCriteria bulkBillCriteria) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		log.info("Going to generate Demand of Consumer Code" + bulkBillCriteria.getConsumerCode());
		
		if (bulkBillCriteria.getTenantId()==null)
			return "Tenant Id should not be null.";
		log.info("Tenant Ids : " + bulkBillCriteria.getTenantId());
			String msg=demandService.generateDemandForConsumerCode(requestInfo,bulkBillCriteria);
	
			return msg;
	}
	
	// Single Demand Generation 
	
     public String generateSingleDemand(SingleDemand singledemand) {
		String tempvariable=null;
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		log.info("Time schedule start for water demand generation on : " + date.format(dateTimeFormatter));
//		List<String> tenantIds = wSCalculationDao.getTenantId();
		List<String> tenantIds = new ArrayList<>();
		String tenat = singledemand.getTenantId();
		tenantIds.add(tenat);
		if (tenantIds.isEmpty()) {
			log.info("No tenants are found for generating demand");
			
		}
		log.info("Tenant Ids : " + tenantIds.toString());
		
			try {
			
				tempvariable=	demandService.SingleDemandGenerate(tenat, singledemand);
				
			} catch (Exception e) {
				log.error("Exception occured while generating demand for tenant: " + tenat);
				e.printStackTrace();
			}
      return tempvariable;
	}
	
	
	/**
	 * Generate bill Based on Time (Monthly, Quarterly, Yearly)
	 */
	public void generateBillBasedLocality(RequestInfo requestInfo) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime date = LocalDateTime.now();
		log.info("Time schedule start for water bill generation on : " + date.format(dateTimeFormatter));

		BillGenerationSearchCriteria criteria = new BillGenerationSearchCriteria();
		criteria.setStatus(WSCalculationConstant.INITIATED_CONST);

		List<BillScheduler> billSchedularList = billGeneratorService.getBillGenerationDetails(criteria);
		if (billSchedularList.isEmpty())
			return;
		log.info("billSchedularList count : " + billSchedularList.size());
		for (BillScheduler billSchedular : billSchedularList) {
			try {

				billGeneratorDao.updateBillSchedularStatus(billSchedular.getId(), StatusEnum.INPROGRESS);
				log.info("Updated Bill Schedular Status To INPROGRESS");

				requestInfo.getUserInfo().setTenantId(billSchedular.getTenantId() != null ? billSchedular.getTenantId() : requestInfo.getUserInfo().getTenantId());
				RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

				List<String> connectionNos = wSCalculationDao.getConnectionsNoByLocality( billSchedular.getTenantId(), WSCalculationConstant.nonMeterdConnection, billSchedular.getLocality());
				//			connectionNos.add("0603000002");
				//			connectionNos.add("0603009718");
				//			connectionNos.add("0603000001");
				if (connectionNos == null || connectionNos.isEmpty()) {
					billGeneratorDao.updateBillSchedularStatus(billSchedular.getId(), StatusEnum.COMPLETED);
					continue;
				}

				Collection<List<String>> partitionConectionNoList = partitionBasedOnSize(connectionNos, configs.getBulkBillGenerateCount());
				log.info("partitionConectionNoList size: {}, Producer ConsumerCodes size : {} and BulkBillGenerateCount: {}",partitionConectionNoList.size(), connectionNos.size(), configs.getBulkBillGenerateCount());
				int threadSleepCount = 1;
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
						//Pausing the controller for 10 seconds after every two batches pushed to Kafka topic
						Thread.sleep(10000);
						threadSleepCount=1;
					}
					threadSleepCount++;

				}
				billGeneratorDao.updateBillSchedularStatus(billSchedular.getId(), StatusEnum.COMPLETED);
				log.info("Updated Bill Schedular Status To COMPLETED");

			}catch (Exception e) {
				log.error("Execption occured while generating bills for tenant"+billSchedular.getTenantId()+" and locality: "+billSchedular.getLocality());
			}

		}
	}
	/**
	 * 
	 * @param request - Calculation Request Object
	 * @param masterMap - Master MDMS Data
	 * @return list of calculation based on estimation criteria
	 */
	List<Calculation> getFeeCalculation(CalculationReq request, Map<String, Object> masterMap) {
		List<Calculation> calculations = new ArrayList<>(request.getCalculationCriteria().size());
		for (CalculationCriteria criteria : request.getCalculationCriteria()) {
			Map<String, List> estimationMap = estimationService.getFeeEstimation(criteria, request.getRequestInfo(),
					masterMap);
			masterDataService.enrichBillingPeriodForFee(masterMap);
			Calculation calculation = getCalculation(request.getRequestInfo(), criteria, estimationMap, masterMap, false, false);
			calculations.add(calculation);
		}
		return calculations;
	}
	
	List<Calculation> getReconnectionFeeCalculation(CalculationReq request, Map<String, Object> masterMap) {
		List<Calculation> calculations = new ArrayList<>(request.getCalculationCriteria().size());
		for (CalculationCriteria criteria : request.getCalculationCriteria()) {
			Map<String, List> estimationMap = estimationService.getReconnectionFeeEstimation(criteria, request.getRequestInfo(),
					masterMap);
			masterDataService.enrichBillingPeriodForFee(masterMap);
			Calculation calculation = getCalculation(request.getRequestInfo(), criteria, estimationMap, masterMap, false, false);
			calculations.add(calculation);
		}
		return calculations;
	}
	
	public void unsetWaterConnection(List<Calculation> calculation) {
		calculation.forEach(cal -> cal.setWaterConnection(null));
	}
	
	/**
	 * Add adhoc tax to demand
	 * @param adhocTaxReq - Adhox Tax Request Object
	 * @return List of Calculation
	 */
	public List<Calculation> applyAdhocTax(AdhocTaxReq adhocTaxReq) {
		List<TaxHeadEstimate> estimates = new ArrayList<>();
		String businessService = adhocTaxReq.getBusinessService();
		if(!businessService.equalsIgnoreCase(SERVICE_FIELD_VALUE_WS) && !businessService.equalsIgnoreCase(ONE_TIME_FEE_SERVICE_FIELD))
			throw new CustomException("INVALID_BUSINESSSERVICE", "Provide businessService is invalid");

		if (!(adhocTaxReq.getAdhocpenalty().compareTo(BigDecimal.ZERO) == 0)){
			String penaltyTaxhead = businessService.equals(SERVICE_FIELD_VALUE_WS) ? WS_TIME_ADHOC_PENALTY : WS_ADHOC_PENALTY;
			estimates.add(TaxHeadEstimate.builder().taxHeadCode(penaltyTaxhead)
					.estimateAmount(adhocTaxReq.getAdhocpenalty().setScale(2, 2)).build());
		}
		if (!(adhocTaxReq.getAdhocrebate().compareTo(BigDecimal.ZERO) == 0)){
			String rebateTaxhead = businessService.equals(SERVICE_FIELD_VALUE_WS) ? WS_TIME_ADHOC_REBATE : WS_ADHOC_REBATE;
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
	 * Calculate WaterCess during Disconnection Application final Water charge calculation
	 *
	 * @param masterMap
	 * @return waterCess - watercess amount
	 */
	private BigDecimal getWaterCessForDisconnection(Map<String, Object> masterMap, BigDecimal finalWaterCharge) {
		BigDecimal waterCess = BigDecimal.ZERO;
		Map<String, JSONArray> timeBasedExemptionMasterMap = new HashMap<>();
		timeBasedExemptionMasterMap.put(WSCalculationConstant.WC_WATER_CESS_MASTER,
				(JSONArray) (masterMap.getOrDefault(WSCalculationConstant.WC_WATER_CESS_MASTER, null)));

		if (timeBasedExemptionMasterMap.get(WSCalculationConstant.WC_WATER_CESS_MASTER) != null) {
			List<Object> waterCessMasterList = timeBasedExemptionMasterMap
					.get(WSCalculationConstant.WC_WATER_CESS_MASTER);

			Map<String, Object> CessMap = mDataService.getApplicableMasterCess(WSCalculationConstant.Assessment_Year, waterCessMasterList);
			waterCess = waterCessUtil.calculateWaterCess(finalWaterCharge, CessMap);

		}
		return waterCess;
	}
	
	static <T> Collection<List<T>> partitionBasedOnSize(List<T> inputList, int size) {
        final AtomicInteger counter = new AtomicInteger(0);
        return inputList.stream()
                    .collect(Collectors.groupingBy(s -> counter.getAndIncrement()/size))
                    .values();
	}


	@Override
	public void generateDemandBasedOnTimePeriod(RequestInfo requestInfo) {
		// TODO Auto-generated method stub
		
	}
	
}
