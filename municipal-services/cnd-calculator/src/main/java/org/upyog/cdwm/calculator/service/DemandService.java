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
import org.upyog.cdwm.calculator.web.models.CNDApplicationDetail;
import org.upyog.cdwm.calculator.web.models.Calculation;
import org.upyog.cdwm.calculator.web.models.CalulationCriteria;
import org.upyog.cdwm.calculator.web.models.demand.Demand;
import org.upyog.cdwm.calculator.web.models.demand.DemandDetail;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class for handling demand generation.
 * It calculates the demand based on the CND application and persists it.
 */

@Service
@Slf4j
public class DemandService {


	@Autowired
	private CalculationService calculationService;

	@Autowired
	private DemandRepository demandRepository;

	@Autowired
	private CNDService cndService;
	
	@Autowired
	private CalculatorConfig config;

	/**
     * Creates demand by fetching the ton price from MDMS.
     * 
     * @param requestInfo The request information containing user and transaction details.
     * @param criterias   List of calculation criteria for demand generation.
     * @return List of generated demand objects.
     * @throws IllegalArgumentException if the provided criteria list is null.
     */

	public List<Demand> createDemand(RequestInfo requestInfo, List<CalulationCriteria> criterias) {
		if (criterias == null) {
			throw new IllegalArgumentException("CND Request is Empty");
		}
	
			List<Calculation> calculations = new LinkedList<>();
			for (CalulationCriteria criteria : criterias) {
				CNDApplicationDetail cndApplicationDetail = null;
				if (criteria.getCndRequest().getApplicationNumber()!= null) {
					cndApplicationDetail = cndService.getCNDApplication(requestInfo, criteria.getCndRequest().getTenantId(), criteria.getCndRequest().getApplicationNumber());
					criteria.setCndRequest(cndApplicationDetail);
				}		
				
		String consumerCode = cndApplicationDetail.getApplicationNumber();
		BigDecimal amountPayable = calculationService.calculateFee(cndApplicationDetail, requestInfo);
		log.info("Final amount payable after calculation : " + amountPayable);
		List<DemandDetail> demandDetails = buildDemandDetails(amountPayable, cndApplicationDetail.getTenantId(), cndApplicationDetail);
		Demand demand = buildDemand(cndApplicationDetail.getTenantId(), consumerCode, null, demandDetails, amountPayable, cndApplicationDetail);
		log.info("Final demand generation object" + demand.toString());
		return demandRepository.saveDemand(requestInfo, Collections.singletonList(demand));
	}
			return null;}


	 /**
     * Builds demand details based on the calculated payable amount.
     * 
     * @param amountPayable The total payable amount for the demand.
     * @param tenantId      The tenant ID associated with the demand.
     * @param cndApplicationDetail   The cndApplicationDetail for which the demand is being created.
     * @return A list of demand details.
     */
	
	private List<DemandDetail> buildDemandDetails(BigDecimal amountPayable, String tenantId, CNDApplicationDetail cndRequest) {
		return Collections.singletonList(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
				.taxAmount(amountPayable).taxHeadMasterCode(CalculatorConstants.CND_CALCULATOR_TAX_MASTER_CODE)
				.tenantId(tenantId).build());
	}
	
	  /**
     * Builds the final demand object.
     * 
     * @param tenantId      The tenant ID for the demand.
     * @param consumerCode  The unique consumer code for the demand.
     * @param owner         The user who is responsible for the demand.
     * @param demandDetails The list of demand details.
     * @param amountPayable The total amount payable for the demand.
     * @param cndRequest    The associated CND request.
     * @return The constructed Demand object.
     */
	
	private Demand buildDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails,
			BigDecimal amountPayable, CNDApplicationDetail cndRequest) {
		return Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner).tenantId(tenantId)
				.taxPeriodFrom(CalculationUtils.getCurrentTimestamp()).taxPeriodTo(CalculationUtils.getFinancialYearEnd())
				.consumerType(config.getModuleName())
				.businessService(config.getBusinessserviceName()).additionalDetails(null).build();
	}

}
