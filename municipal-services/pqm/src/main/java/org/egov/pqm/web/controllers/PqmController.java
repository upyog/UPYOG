package org.egov.pqm.web.controllers;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.service.PqmService;
import org.egov.pqm.util.ResponseInfoFactory;
import org.egov.pqm.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class PqmController {
	
	@Autowired
	private PqmService pqmService;
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

  @PostMapping(value = "/_create", consumes = {"application/json"})
  ResponseEntity<TestResponse> create(@Valid @RequestBody TestRequest testRequest) {
    Test test = pqmService.create(testRequest);
    List<Test> testList = new ArrayList<>();
    testList.add(test);
    TestResponse response = TestResponse.builder().tests(testList)
        .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(testRequest.getRequestInfo(), true))
        .build();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping(value = "/_update", consumes = {"application/json"})
  ResponseEntity<TestResponse> update(@Valid @RequestBody TestRequest testRequest) {
      Test test = pqmService.update(testRequest);
      List<Test> testList = new ArrayList<>();
      testList.add(test);
    TestResponse response = TestResponse.builder().tests(testList).responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(testRequest.getRequestInfo(),true)).build();
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @PostMapping(value = "/_search")
  ResponseEntity<TestResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
      @Valid @RequestBody TestSearchRequest testSearchRequest) {
    TestResponse response = pqmService.testSearch(testSearchRequest,
        requestInfoWrapper.getRequestInfo(), true);

    response.setResponseInfo(
        responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
            true));
//    return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping(value = "/_scheduler", produces = {"*/*"}, consumes = {"application/json"})
  ResponseEntity<TestResponse> scheduleTest(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper ) {
    pqmService.scheduleTest(requestInfoWrapper.getRequestInfo());
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
  }
  
	@PostMapping(value = "/_plainsearch")
	public ResponseEntity<TestResponse> plainsearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute TestSearchCriteria criteria) {
		TestResponse response = pqmService.searchTestPlainSearch(criteria,
		        requestInfoWrapper.getRequestInfo());
		response.setResponseInfo(
		        responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
		            true));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/_downloadPdf")
	public ResponseEntity<EgovPdfResp> downloadPdf(@RequestParam("testId") String testId,
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {
		EgovPdfResp egovPdfResp = pqmService.downloadPdf(requestInfoWrapper.getRequestInfo(), testId);
		return new ResponseEntity<>(egovPdfResp, HttpStatus.OK);
	}



}

