package org.egov.feedback.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.egov.feedback.entity.FeedbackSubmissionEntity;
import org.egov.feedback.model.FeedbackCreateRequest;
import org.egov.feedback.model.FeedbackSearchRequest;
import org.egov.feedback.service.FeedbackService;
//import org.egov.feedback.;
//import digit.models.coremodels.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;

@Controller
@RequestMapping("/submission")
public class FeedbackSubmissionController {

	@Autowired
	private FeedbackService service;

//	@Autowired
//	AssetService assetService;

	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<FeedbackCreateRequest> createFeedbackSubmission(@Valid @RequestBody FeedbackCreateRequest feedbackSubmission) throws CustomException {
		service.createFeedback(feedbackSubmission);
		return new ResponseEntity<FeedbackCreateRequest>(feedbackSubmission, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<List <FeedbackSubmissionEntity>> fetchFeedbackSubmission(@Valid @RequestBody FeedbackSearchRequest feedbackSearch) throws CustomException {
		return new ResponseEntity<List <FeedbackSubmissionEntity>>(service.searchFeedback(feedbackSearch), HttpStatus.OK);
	}
}
