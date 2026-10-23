package org.egov.ndc.web.controller;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ndc.config.ResponseInfoFactory;
import org.egov.ndc.service.NDCService;
import org.egov.ndc.web.model.RequestInfoWrapper;
import org.egov.ndc.web.model.ndc.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ndc")
public class NDCController {

	private final ResponseInfoFactory responseInfoFactory;
	private final NDCService ndcService;

	public NDCController(ResponseInfoFactory responseInfoFactory, NDCService ndcService) {
		this.responseInfoFactory = responseInfoFactory;
		this.ndcService = ndcService;
	}

	@PostMapping("/_create")
	public ResponseEntity<NdcApplicationResponse> createNdcApplication(
			@RequestParam(required = false) Boolean skipWorkFlow,
			@RequestBody NdcApplicationRequest ndcApplicationRequest) {
		boolean skipWorkflowFlag = Boolean.TRUE.equals(skipWorkFlow);
		NdcApplicationRequest request = ndcService.createNdcApplication(skipWorkflowFlag, ndcApplicationRequest);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);

		NdcApplicationResponse response = NdcApplicationResponse.builder()
				.responseInfo(responseInfo)
				.applications(request.getApplications())
				.build();

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/_update")
	public ResponseEntity<NdcApplicationResponse> updateNdcApplication(
			@RequestParam(required = false) Boolean skipWorkFlow,
			@RequestBody NdcApplicationRequest ndcApplicationRequest) {
		boolean skipWorkflowFlag = Boolean.TRUE.equals(skipWorkFlow);
		NdcApplicationRequest request = ndcService.updateNdcApplication(skipWorkflowFlag, ndcApplicationRequest);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);

		NdcApplicationResponse response = NdcApplicationResponse.builder()
				.responseInfo(responseInfo)
				.applications(request.getApplications())
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/_delete")
	public ResponseEntity<NdcApplicationResponse> deleteNdcApplication(@RequestBody NdcDeleteRequest ndcDeleteRequest) {
		NdcApplicationRequest request = ndcService.deleteNdcApplication(ndcDeleteRequest);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);

		NdcApplicationResponse response = NdcApplicationResponse.builder()
				.responseInfo(responseInfo)
				.applications(request.getApplications())
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<NdcApplicationSearchResponse> searchNdcApplications(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute NdcApplicationSearchCriteria criteria) {
		List<Application> applications = ndcService.searchNdcApplications(criteria,requestInfoWrapper.getRequestInfo() );
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);

		NdcApplicationSearchResponse response = NdcApplicationSearchResponse.builder()
				.responseInfo(responseInfo)
				.applications(applications)
				.totalCount(applications.size())
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
