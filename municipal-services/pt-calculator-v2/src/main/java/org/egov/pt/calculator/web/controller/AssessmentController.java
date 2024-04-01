package org.egov.pt.calculator.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.calculator.service.AssessmentService;
import org.egov.pt.calculator.web.models.Assessment;
import org.egov.pt.calculator.web.models.AssessmentResponse;
import org.egov.pt.calculator.web.models.CreateAssessmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assessment")
public class AssessmentController {

	@Autowired
	private AssessmentService assessmentService;

	@PostMapping("/_jobscheduler")
	public ResponseEntity<Object> create(@Valid @RequestBody CreateAssessmentRequest assessmentRequest) {

		List<Assessment> assessedProperties=assessmentService.createAssessmentsForFY(assessmentRequest);
        return new ResponseEntity<>(new AssessmentResponse(new ResponseInfo(),assessedProperties), HttpStatus.OK);

	}
	@PostMapping("/_create")
	public ResponseEntity<Object> createAssessment(@Valid @RequestBody CreateAssessmentRequest assessmentRequest) {

		List<Assessment> assessedProperties=assessmentService.createAssessmentsForFY(assessmentRequest);
        return new ResponseEntity<>(new AssessmentResponse(new ResponseInfo(),assessedProperties), HttpStatus.OK);

	}
	
	@PostMapping("/reassess/_job")
	public ResponseEntity<Object> reAssessment(@Valid @RequestBody CreateAssessmentRequest assessmentRequest) {

		assessmentService.createReAssessmentsForFY(assessmentRequest);
		return new ResponseEntity<>("Re-Assessment done generated for the configured tenants.", HttpStatus.CREATED);
	}
	
	@PostMapping("/cancelassessment/_job")
	public ResponseEntity<Object> cancelAssessment(@Valid @RequestBody CreateAssessmentRequest assessmentRequest) {

		assessmentService.cancelAssessmentsForFY(assessmentRequest);
		return new ResponseEntity<>("Cancel-Assessment done generated for the configured tenants.", HttpStatus.CREATED);
	}
	
}
