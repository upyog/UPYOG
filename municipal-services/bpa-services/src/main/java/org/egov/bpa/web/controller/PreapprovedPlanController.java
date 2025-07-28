package org.egov.bpa.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.bpa.service.PreapprovedPlanService;
import org.egov.bpa.util.BPAUtil;
import org.egov.bpa.util.ResponseInfoFactory;
import org.egov.bpa.web.model.BPASearchCriteria;
import org.egov.bpa.web.model.PreapprovedPlan;
import org.egov.bpa.web.model.PreapprovedPlanRequest;
import org.egov.bpa.web.model.PreapprovedPlanResponse;
import org.egov.bpa.web.model.PreapprovedPlanSearchCriteria;
import org.egov.bpa.web.model.PreapprovedPlanSearchCriteriaWrapper;
import org.egov.bpa.web.model.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/preapprovedplan")
public class PreapprovedPlanController {

	@Autowired
	private PreapprovedPlanService preapprovedPlanService;

	@Autowired
	private BPAUtil bpaUtil;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping(value = "/_create")
	public ResponseEntity<PreapprovedPlanResponse> create(
			@Valid @RequestBody PreapprovedPlanRequest preapprovedPlanRequest) {
		bpaUtil.defaultJsonPathConfig();
		PreapprovedPlan preapprovedPlan = preapprovedPlanService.create(preapprovedPlanRequest);
		List<PreapprovedPlan> preapprovedPlans = new ArrayList<PreapprovedPlan>();
		preapprovedPlans.add(preapprovedPlan);
		PreapprovedPlanResponse response = PreapprovedPlanResponse.builder().preapprovedPlan(preapprovedPlans)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(preapprovedPlanRequest.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/_search")
	public ResponseEntity<PreapprovedPlanResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute PreapprovedPlanSearchCriteria criteria) {
		
		

		List<PreapprovedPlan> preApprovedPlans = preapprovedPlanService
				.getPreapprovedPlanFromCriteria(criteria);

		PreapprovedPlanResponse response = PreapprovedPlanResponse.builder().preapprovedPlan(preApprovedPlans)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/_update")
	public ResponseEntity<PreapprovedPlanResponse> update(
			@Valid @RequestBody PreapprovedPlanRequest preapprovedPlanRequest) {
		PreapprovedPlan preapprovedPlan = preapprovedPlanService.update(preapprovedPlanRequest);
		List<PreapprovedPlan> preapprovedPlans = new ArrayList<PreapprovedPlan>();
		preapprovedPlans.add(preapprovedPlan);
		PreapprovedPlanResponse response = PreapprovedPlanResponse.builder().preapprovedPlan(preapprovedPlans)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(preapprovedPlanRequest.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
