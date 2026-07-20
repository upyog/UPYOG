package org.egov.garbageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.egov.garbageservice.model.GenerateBillRequest;
import org.egov.garbageservice.model.OnDemandBillRequest;
import org.egov.garbageservice.repository.GarbageBillTrackerRepository;
import org.egov.garbageservice.service.GarbageAccountSchedulerService;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.egov.garbageservice.model.BillIdRequest;

import lombok.extern.slf4j.Slf4j;

import org.egov.common.contract.request.RequestInfo;



@RestController
@Slf4j
@Tag(name = "Garbage Scheduler", description = "APIs for garbage bill scheduler operations")
@RequestMapping("/garbage-accounts-scheduler")
/**
 * REST controller for scheduled and on-demand garbage billing maintenance jobs.
 *
 * Behavior:
 * - POST /bill-generator — bulk bill generation via {@link org.egov.garbageservice.service.GarbageAccountSchedulerService#generateBill}.
 * - POST /on-demand-generation — generate bills for selected accounts/demands on demand.
 * - POST /penalty/_update — run penalty processing for garbage accounts from RequestInfo.
 * - POST /reverse-rebate-amount — reverse applied rebate amounts.
 * - POST /extract-tracker — fetch {@link org.egov.garbageservice.model.GrbgBillTracker} data by bill id.
 *
 * Notes:
 * - Base path: /garbage-accounts-scheduler; Swagger tag "Garbage Scheduler".
 * - Typically invoked by cron, ops tools, or internal services—not standard citizen CRUD.
 * - All heavy logic lives in GarbageAccountSchedulerService; controller only forwards requests.
 */
public class GarbageAccountSchedulerController {

	@Autowired
	private GarbageAccountSchedulerService service;

	@Operation(summary = "Generate garbage bills")
	@PostMapping("/bill-generator")
	public ResponseEntity<?> billGenerator(@RequestBody GenerateBillRequest generateBillRequest) {
//		service.generateBill(requestInfoWrapper);
//		return ResponseEntity.ok("Bill generated successfully!!!");
		return ResponseEntity.ok(service.generateBill(generateBillRequest));
	}

	
	@Operation(summary = "On-demand bill generation")
	@PostMapping("/on-demand-generation")
	public ResponseEntity<?> demandGeneration(@RequestBody OnDemandBillRequest onDemandBillRequest) {
		return ResponseEntity.ok(service.generateBillOnDemand(onDemandBillRequest));
	}
	
	@Operation(summary = "Update garbage penalty")
	@PostMapping("/penalty/_update")
    public ResponseEntity<Void> updatePenalty(
            @RequestBody RequestInfoWrapper requestInfoWrapper) {
		service.processGarbagePenalty(
            requestInfoWrapper.getRequestInfo()
        );
        return ResponseEntity.ok().build();
    }
	
	@Operation(summary = "Reverse rebate amount")
	@PostMapping("/reverse-rebate-amount")
	public ResponseEntity<?> reverseRebateAmount(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.reverseGarbageRebate(requestInfoWrapper);

		return ResponseEntity.ok("Rebate amount reversed successfully!!!");
//		return ResponseEntity.ok(service.reverseRebateAmount(requestInfoWrapper));
	}
	
	@Operation(summary = "Get tracker by bill ID")
	@PostMapping("/extract-tracker")
	public ResponseEntity<?> getTrackerByBillId(@RequestBody BillIdRequest request) {
	    return ResponseEntity.ok(service.getTrackerByBillId(request));
	}

}
