package org.egov.asset.calculator.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.asset.calculator.config.CalculatorConfig;
import org.egov.asset.calculator.kafka.broker.CalculatorProducer;
import org.egov.asset.calculator.repository.BillingSlabRepository;
import org.egov.asset.calculator.repository.querybuilder.BillingSlabQueryBuilder;
import org.egov.asset.calculator.utils.CalculationUtils;
import org.egov.asset.calculator.utils.CalculatorConstants;
import org.egov.asset.calculator.web.models.BillResponse;
import org.egov.asset.calculator.web.models.BillingSlab;
import org.egov.asset.calculator.web.models.BillingSlab.SlumEnum;
import org.egov.asset.calculator.web.models.BillingSlabSearchCriteria;
import org.egov.asset.calculator.web.models.Calculation;
import org.egov.asset.calculator.web.models.CalculationReq;
import org.egov.asset.calculator.web.models.CalculationRes;
import org.egov.asset.calculator.web.models.CalulationCriteria;
import org.egov.asset.calculator.web.models.EstimatesAndSlabs;
import org.egov.asset.calculator.web.models.FSM;
import org.egov.asset.calculator.web.models.demand.Category;
import org.egov.asset.calculator.web.models.demand.TaxHeadEstimate;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
@Slf4j
public class CalculationService {

	@Autowired
	private MDMSService mdmsService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private CalculatorConfig config;

	@Autowired
	private CalculationUtils utils;

	@Autowired
	private CalculatorProducer producer;

	@Autowired
	private AssetService assetService;

	@Autowired
	private BillingSlabQueryBuilder billingSlabQueryBuilder;

	@Autowired
	private BillingSlabRepository billingSlabRepository;

	@Autowired
	private BillingService billingService;
//	@Autowired

	/**
	 * Calculates tax estimates and creates demand
	 * 
	 * @param calculationReq The calculationCriteria request
	 * @return List of calculations for all applicationNumbers or tradeLicenses in
	 *         calculationReq
	 */
	public List<Calculation> calculate(CalculationReq calculationReq) {
		String tenantId = calculationReq.getCalulationCriteria().get(0).getTenantId().split("\\.")[0];
		Object mdmsData = mdmsService.mDMSCall(calculationReq, tenantId);

		List<Calculation> calculations = getCalculation(calculationReq.getRequestInfo(),
				calculationReq.getCalulationCriteria(), mdmsData);
		demandService.generateDemand(calculationReq.getRequestInfo(), calculations);
		CalculationRes calculationRes = CalculationRes.builder().calculations(calculations).build();
		return calculations;
	}

	private List<Calculation> getCalculation(@NotNull @Valid RequestInfo requestInfo, @Valid List<CalulationCriteria> calulationCriteria, Object mdmsData) {
        return null;
    }


	public String getAmountForVehicleType(String vehicleType, Object mdmsData) {
		String amount = null;
		List<Map> vehicleTypeList = JsonPath.read(mdmsData, CalculatorConstants.VEHICLE_MAKE_MODEL_JSON_PATH);
		for (Map vehicleTypeMap : vehicleTypeList) {
			if (vehicleTypeMap.get("code").equals(vehicleType)) {
				amount = (String) vehicleTypeMap.get("capacity");
				break;
			}
		}
		return amount;
	}

}
