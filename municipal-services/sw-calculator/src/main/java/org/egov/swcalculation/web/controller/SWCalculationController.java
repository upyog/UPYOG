package org.egov.swcalculation.web.controller;



import java.util.List;

import javax.validation.Valid;

import org.egov.swcalculation.service.DemandService;
import org.egov.swcalculation.service.SWCalculationService;
import org.egov.swcalculation.service.SWCalculationServiceImpl;
import org.egov.swcalculation.util.ResponseInfoFactory;
import org.egov.swcalculation.web.models.AdhocTaxReq;
import org.egov.swcalculation.web.models.BulkBillReq;
import org.egov.swcalculation.web.models.Calculation;
import org.egov.swcalculation.web.models.CalculationReq;
import org.egov.swcalculation.web.models.CalculationRes;
import org.egov.swcalculation.web.models.ConnectionResponse;
import org.egov.swcalculation.web.models.Demand;
import org.egov.swcalculation.web.models.DemandResponse;
import org.egov.swcalculation.web.models.GetBillCriteria;
import org.egov.swcalculation.web.models.RequestInfoWrapper;
import org.egov.swcalculation.web.models.SewerageConnection;
import org.egov.swcalculation.service.DemandService;
import org.egov.swcalculation.service.SWCalculationService;
import org.egov.swcalculation.service.SWCalculationServiceImpl;
import org.egov.swcalculation.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@RestController
@RequestMapping("/sewerageCalculator")
public class SWCalculationController {
	
	@Autowired
	private SWCalculationService sWCalculationService;
	
	@Autowired
	private DemandService demandService;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@Autowired
	private SWCalculationServiceImpl sWCalculationServiceImpl;
	
	@PostMapping("/_calculate")
	public ResponseEntity<CalculationRes> calculate(@RequestBody @Valid CalculationReq calculationReq) {
		List<Calculation> calculations = sWCalculationService.getCalculation(calculationReq);
		CalculationRes response = CalculationRes.builder().calculation(calculations)
				.responseInfo(
						responseInfoFactory.createResponseInfoFromRequestInfo(calculationReq.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_estimate")
	public ResponseEntity<CalculationRes> getTaxEstimation(@RequestBody @Valid CalculationReq calculationReq) {
		List<Calculation> calculations = sWCalculationService.getEstimation(calculationReq);
		CalculationRes response = CalculationRes.builder().calculation(calculations)
				.responseInfo(
						responseInfoFactory.createResponseInfoFromRequestInfo(calculationReq.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_updateDemand")
	public ResponseEntity<DemandResponse> updateDemands(@RequestBody @Valid RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute @Valid GetBillCriteria getBillCriteria) {
		List<Demand> demands = demandService.updateDemands(getBillCriteria, requestInfoWrapper, false);
		DemandResponse response = DemandResponse.builder().demands(demands)
				.responseInfo(
						responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_jobscheduler")
	public void jobscheduler(@Valid @RequestBody BulkBillReq bulkBillReq) {
		sWCalculationService.generateDemandBasedOnTimePeriod(bulkBillReq.getRequestInfo(), bulkBillReq.getBulkBillCriteria());
	}
	
	@PostMapping("/_jobbillscheduler")
	public void jobbillscheduler(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		sWCalculationService.generateBillBasedLocality(requestInfoWrapper.getRequestInfo());
	}
	

	@PostMapping("/_getConnectionForDemand")
	public ResponseEntity<ConnectionResponse> generateDemand(@Valid @RequestBody BulkBillReq bulkBillReq) {
		List<SewerageConnection> waterConnectionList=sWCalculationService.getConnnectionWithPendingDemand(bulkBillReq.getRequestInfo(), bulkBillReq.getBulkBillCriteria());
		ConnectionResponse response = ConnectionResponse.builder().connection(waterConnectionList)
				.responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(bulkBillReq.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_generateDemand")
	public String getConnection(@Valid @RequestBody BulkBillReq bulkBillReq) {
		return sWCalculationService.generateDemandForConsumerCodeBasedOnTimePeriod(bulkBillReq.getRequestInfo(), bulkBillReq.getBulkBillCriteria());
		//return "Demand Generated successfully for consumer Code "+bulkBillReq.getBulkBillCriteria().getConsumerCode();
	}
	

	@PostMapping("/_applyAdhocTax")
	public ResponseEntity<CalculationRes> applyAdhocTax(@Valid @RequestBody AdhocTaxReq adhocTaxReq) {
		List<Calculation> calculations = sWCalculationServiceImpl.applyAdhocTax(adhocTaxReq);
		CalculationRes response = CalculationRes.builder().calculation(calculations)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(adhocTaxReq.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
