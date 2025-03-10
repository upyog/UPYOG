package org.upyog.cdwm.calculator.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.calculator.config.CalculatorConfig;
import org.upyog.cdwm.calculator.repository.DemandRepository;
import org.upyog.cdwm.calculator.util.CalculationUtils;
import org.upyog.cdwm.calculator.util.CalculatorConstants;
import org.upyog.cdwm.calculator.web.models.AuditDetails;
import org.upyog.cdwm.calculator.web.models.CNDRequest;
import org.upyog.cdwm.calculator.web.models.Calculation;
import org.upyog.cdwm.calculator.web.models.CalulationCriteria;
import org.upyog.cdwm.calculator.web.models.demand.Demand;
import org.upyog.cdwm.calculator.web.models.demand.DemandDetail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {


	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private CNDService cndService;



	/**
	 * Create demand by bringing tanker price from mdms
	 * 
	 * @param calculationRequest
	 * @return
	 */

	public List<Demand> createDemand(RequestInfo requestInfo, List<CalulationCriteria> criterias) {
		if (criterias == null) {
			throw new IllegalArgumentException("CND Request is Empty");
		}
	
			List<Calculation> calculations = new LinkedList<>();
			for (CalulationCriteria criteria : criterias) {
				CNDRequest cndRequest = null;
				if (criteria.getCndRequest().getApplicationNumber()!= null) {
					cndRequest = cndService.getCNDApplication(requestInfo, criteria.getCndRequest().getTenantId(), criteria.getCndRequest().getApplicationNumber());
					criteria.setCndRequest(cndRequest);
				}		
				
		String consumerCode = cndRequest.getApplicationNumber();
		BigDecimal amountPayable = calculationService.calculateFee(cndRequest, requestInfo);
		log.info("Final amount payable after calculation : " + amountPayable);
		List<DemandDetail> demandDetails = buildDemandDetails(amountPayable, cndRequest.getTenantId(), cndRequest);
		Demand demand = buildDemand(cndRequest.getTenantId(), consumerCode, null, demandDetails, amountPayable, cndRequest);
		log.info("Final demand generation object" + demand.toString());
		return demandRepository.saveDemand(requestInfo, Collections.singletonList(demand));
	}
			return null;}


	private List<DemandDetail> buildDemandDetails(BigDecimal amountPayable, String tenantId, CNDRequest cndRequest) {
		return Collections.singletonList(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
				.taxAmount(amountPayable).taxHeadMasterCode(CalculatorConstants.CND_CALCULATOR_TAX_MASTER_CODE)
				.tenantId(tenantId).build());
	}
	
	private Demand buildDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails,
			BigDecimal amountPayable, CNDRequest cndRequest) {
		return Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner).tenantId(tenantId)
				.taxPeriodFrom(CalculationUtils.getCurrentTimestamp()).taxPeriodTo(CalculationUtils.getFinancialYearEnd())
				.consumerType(CalculatorConstants.CND_SERVICE_NAME)
				.businessService(CalculatorConstants.CND_MODULE_NAME).additionalDetails(null).build();
	}

}
