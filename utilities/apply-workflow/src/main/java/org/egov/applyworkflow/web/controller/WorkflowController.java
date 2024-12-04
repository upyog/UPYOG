package org.egov.applyworkflow.web.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.egov.applyworkflow.web.model.WorkflowApplyRequest;
import org.egov.applyworkflow.service.WorkflowApplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class WorkflowController {

    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);

    private final WorkflowApplyService workflowApplyService;

    public WorkflowController( WorkflowApplyService workflowApplyService) {
        this.workflowApplyService = workflowApplyService;
    }

    //@PostMapping("/_process")
    @RequestMapping(value = "/_process", method = RequestMethod.POST)
    @ApiOperation(value = "Process Workflow", notes = "Handles create or update workflow operations based on the payload")
    public ResponseEntity<Object> processWorkflow(@ApiParam(value = "Workflow payload", required = true)
                                                  @Valid @RequestBody WorkflowApplyRequest payload) {
        log.info("Received workflow process request: {}", payload);

        Object response = workflowApplyService.processWorkflow(payload);
        if (response == null ) {
            log.error("Workflow processing returned an empty response for payload: {}", payload);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Workflow processing failed. Empty response received.");
        }
        log.info("Workflow processed successfully");
        return ResponseEntity.ok(response);
    }
}
