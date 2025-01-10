package org.egov.wf.web.controllers;

import javax.validation.Valid;

import org.egov.wf.service.WorkflowInboxService;
import org.egov.wf.web.models.RequestInfoWrapper;
import org.egov.wf.web.models.ValidActionResponce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/egov-wf")
public class WorkflowInboxController {

	@Autowired
	private WorkflowInboxService inboxService;

	@PostMapping("/_action")
	public ResponseEntity<ValidActionResponce> featchValidAction(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @RequestParam("businessId") String businessId,
			@RequestParam("tenantId") String tenantId) {
		ValidActionResponce actionResponce = inboxService.getValidAction(requestInfoWrapper.getRequestInfo(),
				businessId, tenantId);
		return new ResponseEntity<>(actionResponce, HttpStatus.OK);
	}

}
