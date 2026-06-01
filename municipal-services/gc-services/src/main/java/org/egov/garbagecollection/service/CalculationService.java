package org.egov.garbagecollection.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbagecollection.config.GCConfiguration;
import org.egov.garbagecollection.constants.GCConstants;
import org.egov.garbagecollection.repository.GcDao;
import org.egov.garbagecollection.repository.ServiceRequestRepository;
import org.egov.garbagecollection.util.GcServicesUtil;
import org.egov.garbagecollection.web.models.*;
import org.egov.garbagecollection.web.models.collection.Bill;
import org.egov.garbagecollection.web.models.collection.BillResponse;
import org.egov.garbagecollection.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
@Slf4j
public class CalculationService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private GcServicesUtil gcServiceUtil;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private GCConfiguration config;

	@Autowired
	private GcDao gcDao;

	@Autowired
	private GcService gcService;

	@Autowired
	private EnrichmentService enrichmentService;

	/**
	 * 
	 * @param request
	 * 
	 * For NewGC trimmed workflow: demand is generated on SUBMIT_APPLICATION
	 * (direct transition to PENDING_FOR_PAYMENT, no verifier step).
	 * For modify/reconnect flows: demand is still generated on APPROVE_FOR_CONNECTION.
	 *
	 */
	public void calculateFeeAndGenerateDemand(GarbageConnectionRequest request, Property property) {
		String action = request.getGarbageConnection().getProcessInstance().getAction();
		boolean isReconnect = request.isReconnectRequest()
				|| request.getGarbageConnection().getApplicationType().equalsIgnoreCase(GCConstants.GARBAGE_RECONNECTION);

		boolean isNewGcSubmit = GCConstants.SUBMIT_APPLICATION_CONST.equalsIgnoreCase(action)
				&& request.getGarbageConnection().getApplicationType()
						.equalsIgnoreCase(GCConstants.NEW_GARBAGE_CONNECTION);

		if ((GCConstants.APPROVE_CONNECTION_CONST.equalsIgnoreCase(action) && !isReconnect) || isNewGcSubmit) {
			CalculationCriteria criteria = CalculationCriteria.builder()
					.applicationNo(request.getGarbageConnection().getApplicationNo())
					.garbageConnection(request.getGarbageConnection())
					.tenantId(property.getTenantId()).build();
			CalculationReq calRequest = CalculationReq.builder().calculationCriteria(Arrays.asList(criteria))
					.requestInfo(request.getRequestInfo()).isconnectionCalculation(false).isDisconnectionRequest(false).isReconnectionRequest(false).build();
			try {
				Object response = serviceRequestRepository.fetchResult(gcServiceUtil.getCalculatorURL(), calRequest);
				CalculationRes calResponse = mapper.convertValue(response, CalculationRes.class);
			} catch (Exception ex) {
				log.error("Calculation response error!!", ex);
				throw new CustomException("WATER_CALCULATION_EXCEPTION", "Calculation response can not parsed!!!");
			}
		} else if (GCConstants.APPROVE_DISCONNECTION_CONST.equalsIgnoreCase(request.getGarbageConnection().getProcessInstance().getAction())) {
			CalculationCriteria criteria = CalculationCriteria.builder()
					.applicationNo(request.getGarbageConnection().getApplicationNo())
					.garbageConnection(request.getGarbageConnection())
					.tenantId(property.getTenantId()).connectionNo(request.getGarbageConnection().getConnectionNo()).build();
			CalculationReq calRequest = CalculationReq.builder().calculationCriteria(Arrays.asList(criteria))
          .requestInfo(request.getRequestInfo()).isconnectionCalculation(false).isDisconnectionRequest(true).isReconnectionRequest(false).build();
		      
			try {
				Object response = serviceRequestRepository.fetchResult(gcServiceUtil.getCalculatorURL(), calRequest);
				CalculationRes calResponse = mapper.convertValue(response, CalculationRes.class);
			} catch (ServiceCallException e) {
				throw new ServiceCallException(e.getError());
			} catch (Exception ex) {
				log.error("Calculation response error!!", ex);
				throw new CustomException("WATER_CALCULATION_EXCEPTION", "Calculation response can not parsed!!!");
			}
		}
		else if (GCConstants.RECONNECT_DISCONNECTION_CONST.equalsIgnoreCase(request.getGarbageConnection().getProcessInstance().getAction()) && (request.isReconnectRequest() || request.getGarbageConnection().getApplicationType().equalsIgnoreCase(GCConstants.GARBAGE_RECONNECTION))) {
			CalculationCriteria criteria = CalculationCriteria.builder()
					.applicationNo(request.getGarbageConnection().getApplicationNo())
					.garbageConnection(request.getGarbageConnection())
					.tenantId(property.getTenantId()).connectionNo(request.getGarbageConnection().getConnectionNo()).build();
			CalculationReq calRequest = CalculationReq.builder().calculationCriteria(Arrays.asList(criteria))
					.requestInfo(request.getRequestInfo()).isconnectionCalculation(false).isDisconnectionRequest(false).isReconnectionRequest(true).build();
			try {
				Object response = serviceRequestRepository.fetchResult(gcServiceUtil.getCalculatorURL(), calRequest);
				CalculationRes calResponse = mapper.convertValue(response, CalculationRes.class);
			} catch (ServiceCallException e) {
				throw new ServiceCallException(e.getError());
			} catch (Exception ex) {
				log.error("Calculation response error!!", ex);
				throw new CustomException("WATER_CALCULATION_EXCEPTION", "Calculation response can not parsed!!!");
			}
		}
	}

	public boolean fetchBill(String tenantId, String connectionNo, RequestInfo requestInfo) {
		boolean isNoPayment = false;
		try {
			Object result = serviceRequestRepository.fetchResult(getFetchBillURL(tenantId, connectionNo)
					, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
			BillResponse billResponse = mapper.convertValue(result, BillResponse.class);
			for (Bill bill : billResponse.getBill()) {
				if (bill.getTotalAmount().equals(BigDecimal.valueOf(0.0))) {
					isNoPayment = true;
				}
			}
		} catch (Exception ex) {
			throw new CustomException("WATER_FETCH_BILL_ERRORCODE", "Error while fetching the bill" + ex.getMessage());
		}
		return isNoPayment;
	}
	
	public boolean fetchBillForReconnect(String tenantId, String connectionNo, RequestInfo requestInfo) {
		boolean isNoPayment = false;
		try {
			Object result = serviceRequestRepository.fetchResult(getFetchBillURLForReconnect(tenantId, connectionNo)
					, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
			BillResponse billResponse = mapper.convertValue(result, BillResponse.class);
			for (Bill bill : billResponse.getBill()) {
				if (bill.getTotalAmount().equals(BigDecimal.valueOf(0.0))) {
					isNoPayment = true;
				}
			}
		} catch (Exception ex) {
			throw new CustomException("WATER_FETCH_BILL_ERRORCODE", "Error while fetching the bill" + ex.getMessage());
		}
		return isNoPayment;
	}

	private StringBuilder getFetchBillURL(String tenantId, String connectionNo) {

		return new StringBuilder().append(config.getBillingServiceHost())
				.append(config.getFetchBillEndPoint()).append(GCConstants.URL_PARAMS_SEPARATER)
				.append(GCConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(GCConstants.SEPARATER).append(GCConstants.CONSUMER_CODE_SEARCH_FIELD_NAME)
				.append(connectionNo).append(GCConstants.SEPARATER)
				.append(GCConstants.BUSINESSSERVICE_FIELD_FOR_SEARCH_URL)
				.append(GCConstants.WATER_TAX_SERVICE_CODE);
	}
	
	private StringBuilder getFetchBillURLForReconnect(String tenantId, String connectionNo) {

		return new StringBuilder().append(config.getBillingServiceHost())
				.append(config.getFetchBillEndPoint()).append(GCConstants.URL_PARAMS_SEPARATER)
				.append(GCConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(GCConstants.SEPARATER).append(GCConstants.CONSUMER_CODE_SEARCH_FIELD_NAME)
				.append(connectionNo).append(GCConstants.SEPARATER)
				.append(GCConstants.BUSINESSSERVICE_FIELD_FOR_SEARCH_URL)
				.append("WSReconnection");
	}

	/**
	 * Generates the recurring GC demand (consumer code = connectionNo) for the
	 * previous calendar month when a new garbage connection is activated.
	 * <p>
	 * Called from PaymentUpdateService after PAY → CONNECTION_ACTIVATED transition
	 * and after postStatusEnrichment() has set the connectionNo on the request.
	 * <p>
	 * e.g. Connection activated in May → demand created for April 1–30
	 */
	public void generatePreviousMonthConnectionDemand(GarbageConnectionRequest request, Property property) {
		String connectionNo = request.getGarbageConnection().getConnectionNo();
		if (connectionNo == null || connectionNo.isEmpty()) {
			log.warn("generatePreviousMonthConnectionDemand: connectionNo is null/empty, skipping previous month demand.");
			return;
		}

		// Tax period convention: fromDate = UTC midnight (1st of month),
		//                        toDate   = IST 23:59:59 (last day) → UTC epoch ms
		java.time.YearMonth prevMonth = java.time.YearMonth.now().minusMonths(1);
		java.time.ZoneId ist = java.time.ZoneId.of("Asia/Kolkata");

		long fromDate = prevMonth.atDay(1)
				.atStartOfDay(java.time.ZoneOffset.UTC)   // 00:00:00 UTC
				.toInstant().toEpochMilli();

		long toDate = prevMonth.atEndOfMonth()
				.atTime(23, 59, 59)
				.atZone(ist)                               // 23:59:59 IST → UTC
				.toInstant().toEpochMilli();

		log.info("Generating previous month GC demand for connectionNo: {} [fromDate={}, toDate={}]",
				connectionNo, fromDate, toDate);

		CalculationCriteria criteria = CalculationCriteria.builder()
				.connectionNo(connectionNo)
				.garbageConnection(request.getGarbageConnection())
				.tenantId(property.getTenantId())
				.from(fromDate)
				.to(toDate)
				.build();

		CalculationReq calRequest = CalculationReq.builder()
				.calculationCriteria(Arrays.asList(criteria))
				.requestInfo(request.getRequestInfo())
				.isconnectionCalculation(true)   // connectionNo as consumer code → GC demand
				.isDisconnectionRequest(false)
				.isReconnectionRequest(false)
				.build();
		try {
			Object response = serviceRequestRepository.fetchResult(gcServiceUtil.getCalculatorURL(), calRequest);
			CalculationRes calResponse = mapper.convertValue(response, CalculationRes.class);
			log.info("Previous month GC demand generated successfully for connectionNo: {}", connectionNo);
		} catch (Exception ex) {
			log.error("Failed to generate previous month GC demand for connectionNo: {}", connectionNo, ex);
			// Non-fatal: don't block activation if previous month demand generation fails
		}
	}
}
