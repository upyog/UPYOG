package org.egov.wscalculation.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.repository.WSCalculationDao;
import org.egov.wscalculation.util.CalculatorUtil;
import org.egov.wscalculation.validator.WSCalculationValidator;
import org.egov.wscalculation.validator.WSCalculationWorkflowValidator;
import org.egov.wscalculation.web.models.AuditDetails;
import org.egov.wscalculation.web.models.CalculationCriteria;
import org.egov.wscalculation.web.models.CalculationReq;
import org.egov.wscalculation.web.models.MeterConnectionRequest;
import org.egov.wscalculation.web.models.MeterConnectionRequests;
import org.egov.wscalculation.web.models.MeterReading;
import org.egov.wscalculation.web.models.MeterReadingList;
import org.egov.wscalculation.web.models.MeterReadingSearchCriteria;
import org.egov.wscalculation.web.models.WaterConnection;
import org.egov.wscalculation.web.models.WaterDetails;
import org.egov.wscalculation.web.models.MeterReading.MeterStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class MeterServicesImpl implements MeterService {

	@Autowired
	private WSCalculationDao wSCalculationDao;

	@Autowired
	private WSCalculationValidator wsCalculationValidator;
	
	@Autowired
	private WSCalculationService wSCalculationService;
	
	@Autowired
	private EstimationService estimationService;

	private EnrichmentService enrichmentService;
	
	@Autowired
	private WSCalculationWorkflowValidator wsCalulationWorkflowValidator;
	
	@Autowired
	private WSCalculationDao waterCalculatorDao;

	@Autowired
	public MeterServicesImpl(EnrichmentService enrichmentService) {
		this.enrichmentService = enrichmentService;
	}

	/**
	 * 
	 * @param meterConnectionRequest MeterConnectionRequest contains meter reading connection to be created
	 * @return List of MeterReading after create
	 */

	@Override
	public List<MeterReading> createMeterReading(MeterConnectionRequest meterConnectionRequest) {
		Boolean genratedemand = true;
		List<MeterReading> meterReadingsList = new ArrayList<MeterReading>();
		if(meterConnectionRequest.getMeterReading().getGenerateDemand()){
	//		wsCalulationWorkflowValidator.applicationValidation(meterConnectionRequest.getRequestInfo(),meterConnectionRequest.getMeterReading().getTenantId(),meterConnectionRequest.getMeterReading().getConnectionNo(),genratedemand);
	//		wsCalculationValidator.validateMeterReading(meterConnectionRequest, true);
		}
		//enrichmentService.enrichMeterReadingRequest(meterConnectionRequest);
		meterReadingsList.add(meterConnectionRequest.getMeterReading());
		//wSCalculationDao.saveMeterReading(meterConnectionRequest);
		if (meterConnectionRequest.getMeterReading().getGenerateDemand()) {
			generateDemandForMeterReading(meterReadingsList, meterConnectionRequest.getRequestInfo());
		}
		return meterReadingsList;
	}
	
	
	
	@Override
	public List<MeterReadingList> createMeterReadings(MeterConnectionRequests meterConnectionRequests) {
		Boolean genratedemand = true;
	    List<MeterReadingList> meterReadingslist = new ArrayList<>();
	    
	    if (meterConnectionRequests != null && meterConnectionRequests.getMeterReadingslist() != null) {

		    for (MeterReadingList meterReadinglist : meterConnectionRequests.getMeterReadingslist()) {
	        Boolean generateDemand = meterReadinglist.getGenerateDemand();
	   

						    RequestInfo requestInfo = meterConnectionRequests.getRequestInfo();
						    MeterConnectionRequests meterreq = new MeterConnectionRequests();
						    MeterConnectionRequest meterConnectionRequest=new MeterConnectionRequest();
						   
						    meterreq.setRequestInfo(requestInfo);
						
						    MeterReading meterReading = new MeterReading();
						    meterReading.setBillingPeriod(meterReadinglist.getBillingPeriod());
						    
						    meterReading.setAuditDetails(meterReadinglist.getAuditDetails());
						    meterReading.setConnectionNo(meterReadinglist.getConnectionNo());
						    meterReading.setConsumption(meterReadinglist.getConsumption());
						    meterReading.setCurrentReading(meterReadinglist.getCurrentReading());
						    meterReading.setCurrentReadingDate(meterReadinglist.getCurrentReadingDate());
						    meterReading.setGenerateDemand(generateDemand);
						    meterReading.setId(meterReadinglist.getId());
						    meterReading.setLastReading(meterReadinglist.getLastReading());
						    meterReading.setLastReadingDate(meterReadinglist.getLastReadingDate());
						    String newvar=meterReadinglist.getMeterStatus().toString();
							MeterStatusEnum meterStatusEnum= MeterStatusEnum.fromValue(newvar);
//
							meterReading.setMeterStatus(meterStatusEnum);
						    meterReading.setTenantId(meterReadinglist.getTenantId());
						    meterConnectionRequest.setMeterReading(meterReading);
						           
						            meterConnectionRequest.setRequestInfo(requestInfo);
						            List<MeterReading> meterReadingsList = new ArrayList<MeterReading>();
						            if(meterConnectionRequest.getMeterReading().getGenerateDemand()){
						    			wsCalulationWorkflowValidator.applicationValidation(meterConnectionRequest.getRequestInfo(),meterConnectionRequest.getMeterReading().getTenantId(),meterConnectionRequest.getMeterReading().getConnectionNo(),genratedemand);
						    			wsCalculationValidator.validateMeterReading(meterConnectionRequest, true);
						    		}
						    		enrichmentService.enrichMeterReadingRequest(meterConnectionRequest);
						    		
						    		meterReadingsList.add(meterConnectionRequest.getMeterReading());
						    		meterReadingslist.add(meterReadinglist);
						    		wSCalculationDao.saveMeterReading(meterConnectionRequest);
						    		if (meterConnectionRequest.getMeterReading().getGenerateDemand()) {
						    			generateDemandForMeterReading(meterReadingsList, meterConnectionRequest.getRequestInfo());
						    		}
}}
    return meterReadingslist;
	}
	
	private void generateDemandForMeterReading(List<MeterReading> meterReadingsList, RequestInfo requestInfo) {
		List<CalculationCriteria> criteriaList = new ArrayList<>();
		//List<WaterConnection> waterConnectionList = util.getWaterConnection(requestInfo,meterReadingsList.get(0).getConnectionNo(),meterReadingsList.get(0).getTenantId());
	
		
		List<WaterConnection> connections = waterCalculatorDao.getConnectionsNo(meterReadingsList.get(0).getTenantId(),
				WSCalculationConstant.meteredConnectionType,meterReadingsList.get(0).getConnectionNo());
		
		meterReadingsList.forEach(reading -> {
			CalculationCriteria criteria = new CalculationCriteria();
			criteria.setTenantId(reading.getTenantId());
			criteria.setAssessmentYear(estimationService.getAssessmentYear());
			criteria.setCurrentReading(reading.getCurrentReading());
			criteria.setLastReading(reading.getLastReading());
			criteria.setConnectionNo(reading.getConnectionNo());
			criteria.setConnectionType("Metered");
			criteria.setFrom(reading.getLastReadingDate());
			criteria.setTo(reading.getCurrentReadingDate());
			criteria.setWaterConnection(connections.get(0));
			criteria.setMeterStatus(reading.getMeterStatus());
			criteriaList.add(criteria);
		});
		CalculationReq calculationRequest = CalculationReq.builder().requestInfo(requestInfo)
				.calculationCriteria(criteriaList).isconnectionCalculation(true).build();
		wSCalculationService.getCalculation(calculationRequest);
	}
	
	
	
	
	/**
	 * 
	 * @param criteria
	 *            MeterConnectionSearchCriteria contains meter reading
	 *            connection criteria to be searched for in the meter
	 *            connection table
	 * @return List of MeterReading after search
	 */
	@Override
	public List<MeterReading> searchMeterReadings(MeterReadingSearchCriteria criteria, RequestInfo requestInfo) {
		return wSCalculationDao.searchMeterReadings(criteria);
	}



}
