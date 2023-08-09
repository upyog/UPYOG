package org.egov.pt.calculator.web.controller;

import javax.validation.Valid;

import org.egov.pt.calculator.service.AssessmentService;
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

		assessmentService.createAssessmentsForFY(assessmentRequest);
		return new ResponseEntity<>("Assessments are generated for the configured tenants.", HttpStatus.CREATED);
	}
	
	@PostMapping("/reassess/_job")
	public ResponseEntity<Object> reAssessment(@Valid @RequestBody CreateAssessmentRequest assessmentRequest) {

		assessmentService.createReAssessmentsForFY(assessmentRequest);
		return new ResponseEntity<>("Re-Assessment done generated for the configured tenants.", HttpStatus.CREATED);
	}
	
}