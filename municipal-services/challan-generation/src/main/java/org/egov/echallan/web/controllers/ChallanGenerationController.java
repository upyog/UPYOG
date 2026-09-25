package org.egov.echallan.web.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.echallan.model.Challan;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.model.ChallanResponse;
import org.egov.echallan.model.RequestInfoWrapper;
import org.egov.echallan.model.SearchCriteria;
import org.egov.echallan.service.ChallanService;
import org.egov.echallan.util.ResponseInfoFactory;
import org.egov.echallan.producer.Producer;
import org.egov.echallan.util.ChallanConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/challan")
public class ChallanGenerationController {

	private final ChallanService challanService;

	private final ResponseInfoFactory responseInfoFactory;

	private final Producer producer;

	public ChallanGenerationController(ChallanService challanService, ResponseInfoFactory responseInfoFactory,
			Producer producer) {
		this.challanService = challanService;
		this.responseInfoFactory = responseInfoFactory;
		this.producer = producer;
	}

	@PostMapping("/_create")
	public ResponseEntity<ChallanResponse> create(@Valid @RequestBody ChallanRequest challanRequest) {

		Challan challan = challanService.create(challanRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(challanRequest.getRequestInfo(), true);
		ChallanResponse response = ChallanResponse.builder().challans(Arrays.asList(challan))
				.responseInfo(resInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	 @PostMapping("/_search")
	 public ResponseEntity<ChallanResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
	                                                       @Valid @ModelAttribute SearchCriteria criteria) {
		 String tenantId = criteria.getTenantId();
		 // Use tenantId from RequestInfo if criteria tenantId is null
		 if (tenantId == null && requestInfoWrapper.getRequestInfo() != null 
		     && requestInfoWrapper.getRequestInfo().getUserInfo() != null) {
			 tenantId = requestInfoWrapper.getRequestInfo().getUserInfo().getTenantId();
		 }
		 
	     List<Challan> challans = challanService.search(criteria, requestInfoWrapper.getRequestInfo());
	    	 
	     
	     Map<String,Integer> dynamicData = challanService.getDynamicData(tenantId);
	    	 
	     int countOfServices = dynamicData.get(ChallanConstants.TOTAL_SERVICES);
	     int totalAmountCollected = dynamicData.get(ChallanConstants.TOTAL_COLLECTION);
	     int validity = challanService.getChallanValidity();
	     int totalCount = challanService.countForSearch(criteria,requestInfoWrapper.getRequestInfo());

	     ChallanResponse response = ChallanResponse.builder().challans(challans).countOfServices(countOfServices)
				 .totalAmountCollected(totalAmountCollected).validity(validity).totalCount(totalCount)
				 .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				 .build();
	     return new ResponseEntity<>(response, HttpStatus.OK);
	}

	 @PostMapping("/_update")
	 public ResponseEntity<ChallanResponse> update(@Valid @RequestBody ChallanRequest challanRequest) {
		Challan challan = challanService.update(challanRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(challanRequest.getRequestInfo(), true);
		ChallanResponse response = ChallanResponse.builder().challans(Arrays.asList(challan))
				.responseInfo(resInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
		}

	@PostMapping("/_count")
	ResponseEntity<Map<String, Object>> count(@RequestParam("tenantId") String tenantId, @RequestBody RequestInfo requestInfo) {
		return new ResponseEntity<>(challanService.getChallanCountResponse(requestInfo, tenantId), HttpStatus.OK);
	}

	@PostMapping("/_test")
	public ResponseEntity<Void> test(@RequestBody ChallanRequest challanRequest) {
		producer.push("update-echallan", challanRequest);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
