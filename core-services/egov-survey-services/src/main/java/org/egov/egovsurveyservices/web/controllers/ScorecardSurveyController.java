package org.egov.egovsurveyservices.web.controllers;

import static org.egov.egovsurveyservices.utils.SurveyServiceConstants.CITIZEN;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.egovsurveyservices.service.ScorecardSurveyService;
import org.egov.egovsurveyservices.utils.ResponseInfoFactory;
import org.egov.egovsurveyservices.web.models.ScorecardSurveyEntity;
import org.egov.egovsurveyservices.web.models.ScorecardSurveyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.egov.egovsurveyservices.web.models.*;

@RestController
@RequestMapping("/egov-ss")
public class ScorecardSurveyController {

    @Autowired
    private ScorecardSurveyService surveyService;
    
    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @PostMapping("/survey/create")
    public ResponseEntity<ScorecardSurveyResponse> createSurvey(@Valid @RequestBody ScorecardSurveyRequest surveyRequest) {
    	ScorecardSurveyEntity scorecardSurveyEntity = surveyService.createSurvey(surveyRequest);
    	ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(surveyRequest.getRequestInfo(), true);
    	ScorecardSurveyResponse response = ScorecardSurveyResponse.builder().surveyEntities(Collections.singletonList(scorecardSurveyEntity)).responseInfo(responseInfo).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value="/survey/_search", method = RequestMethod.POST)
    public ResponseEntity<ScorecardSurveyResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                   @Valid @ModelAttribute ScorecardSurveySearchCriteria criteria) {
        Boolean isCitizen = requestInfoWrapper.getRequestInfo().getUserInfo().getType().equals(CITIZEN);
        List<ScorecardSurveyEntity> surveys = surveyService.searchSurveys(criteria);
        int surveyCount = (surveys != null) ? surveys.size() : 0;
        ScorecardSurveyResponse response  = ScorecardSurveyResponse.builder().surveyEntities(surveys).totalCount(surveyCount).build();
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        response.setResponseInfo(responseInfo);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/survey/active/_update")
    public ResponseEntity<?> updateActiveSurvey(@Valid @RequestBody UpdateSurveyActiveRequest request) {
        try {
            surveyService.updateSurveyActive(request);
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Survey active status updated successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @RequestMapping(value="/survey/response/_submit", method = RequestMethod.POST)
    public ResponseEntity<ScorecardSubmitResponse> responseSubmit(@Valid @RequestBody AnswerRequestNew answerRequest) {
        ScorecardSubmitResponse answerResponse = surveyService.submitResponse(answerRequest);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(answerRequest.getRequestInfo(), true);
        ScorecardSubmitResponse response = ScorecardSubmitResponse.builder().responseInfo(responseInfo).scorecardAnswerResponse(answerResponse.getScorecardAnswerResponse())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/survey/response/_answers", method = RequestMethod.POST)
    public ResponseEntity<ScorecardAnswerResponse> getAnswers(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @Valid @ModelAttribute AnswerFetchCriteria criteria) {
        ScorecardAnswerResponse answerResponse = surveyService.getAnswers(criteria);
        ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        answerResponse.setResponseInfo(responseInfo);
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/survey/response/_plainsearch", method = RequestMethod.POST)
    public ResponseEntity<ScorecardAnswerResponse> getAnswersForPlainSearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,@ModelAttribute AnswerFetchCriteria criteria) {
        ScorecardAnswerResponse answerResponse = surveyService.getAnswersForPlainSearch(criteria);
        ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
        answerResponse.setResponseInfo(responseInfo);
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }
	 
}