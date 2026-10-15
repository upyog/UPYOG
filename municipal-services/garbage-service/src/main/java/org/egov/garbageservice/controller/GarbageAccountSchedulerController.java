package org.egov.garbageservice.controller;

import org.egov.garbageservice.model.GenerateBillRequest;
import org.egov.garbageservice.model.MarkReadyForPrintingRequest;
import org.egov.garbageservice.model.MarkReadyForPrintingResponse;
import org.egov.garbageservice.model.OnDemandBillRequest;
import org.egov.garbageservice.repository.GarbageBillTrackerRepository;
import org.egov.garbageservice.service.GarbageAccountSchedulerService;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.garbageservice.model.GenerateBillPreviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.egov.garbageservice.model.BillIdRequest;
import org.egov.garbageservice.model.CustomAmountUpdateRequest;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.model.CustomAmountUpdateResponse;



@RestController
@Slf4j
@RequestMapping("/garbage-accounts-scheduler")
public class GarbageAccountSchedulerController {

	@Autowired
	private GarbageAccountSchedulerService service;

	@PostMapping("/bill-generator")
	public ResponseEntity<?> billGenerator(@RequestBody GenerateBillRequest generateBillRequest) {
//		service.generateBill(requestInfoWrapper);
//		return ResponseEntity.ok("Bill generated successfully!!!");
		return ResponseEntity.ok(service.generateBill(generateBillRequest));
	}
	
	@PostMapping("/bill-generator/preview")
	public ResponseEntity<List<GenerateBillPreviewResponse>> taxCalculatorPreview(
			@RequestBody GenerateBillRequest generateBillRequest) {

		return ResponseEntity.ok(service.generateBillPreview(generateBillRequest));
	}

	@PostMapping("/on-demand-generation")
	public ResponseEntity<?> demandGeneration(@RequestBody OnDemandBillRequest onDemandBillRequest) {
		return ResponseEntity.ok(service.generateBillOnDemand(onDemandBillRequest));
	}
	
	@PostMapping("/penalty/_update")
    public ResponseEntity<Void> updatePenalty(
            @RequestBody RequestInfoWrapper requestInfoWrapper) {
		service.processGarbagePenalty(
            requestInfoWrapper.getRequestInfo()
        );
        return ResponseEntity.ok().build();
    }
	
	@PostMapping("/reverse-rebate-amount")
	public ResponseEntity<?> reverseRebateAmount(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.reverseGarbageRebate(requestInfoWrapper);

		return ResponseEntity.ok("Rebate amount reversed successfully!!!");
//		return ResponseEntity.ok(service.reverseRebateAmount(requestInfoWrapper));
	}
	
	@PostMapping("/extract-tracker")
	public ResponseEntity<?> getTrackerByBillId(@RequestBody BillIdRequest request) {
	    return ResponseEntity.ok(service.getTrackerByBillId(request));
	}
	
	@PostMapping("/_updateCustomAmount")
	public ResponseEntity<CustomAmountUpdateResponse> updateCustomAmount(@RequestBody CustomAmountUpdateRequest request) {

	    CustomAmountUpdateResponse response = service.updateCustomAmount(request);
	    return ResponseEntity.ok(response);
	}

	/**
	 * Fetches the ULB/ward combinations added today to the ULBS.DdpPrinting
	 * MDMS master and, for each, marks all ddpVerified garbage accounts in
	 * that ULB/ward as ready for printing (batch-wise per ULB/ward).
	 */
	@PostMapping("/ddp/mark-ready-for-printing")
	public ResponseEntity<MarkReadyForPrintingResponse> markReadyForPrinting(
			@RequestBody MarkReadyForPrintingRequest markReadyForPrintingRequest) {
		return ResponseEntity.ok(service.markReadyForPrinting(markReadyForPrintingRequest));
	}

}
