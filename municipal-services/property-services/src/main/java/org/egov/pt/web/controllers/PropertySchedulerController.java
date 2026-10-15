package org.egov.pt.web.controllers;
import java.util.UUID;

import org.egov.pt.models.CalculateTaxRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.CustomAmountUpdateRequest;
import org.egov.pt.models.CustomAmountUpdateResponse;
import org.egov.pt.models.CalculateTaxPreviewResponse;
import org.egov.pt.service.PropertySchedulerService;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.pt.web.contracts.alfresco.DmsResponse;
import org.egov.pt.web.contracts.alfresco.DmsRequest;
import org.egov.tracer.model.CustomException;
import org.egov.pt.util.PTConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

//pdf
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.egov.pt.service.AlfrescoService;
import org.egov.pt.models.BillIdRequest;
import org.springframework.web.bind.annotation.RequestParam;
//import org.egov.ptr.web.contracts.alfresco.DMSResponse;
//import org.egov.ptr.web.contracts.alfresco.DmsRequest;
import org.egov.pt.web.contracts.SendPropertyEmailRequest;
import org.egov.pt.web.contracts.SendPropertyNoticeRequest;

import java.io.*;
import java.util.*;
@RestController
@RequestMapping("/property-scheduler")
public class PropertySchedulerController {

	@Autowired
	private PropertySchedulerService service;
	
	@Autowired
	private AlfrescoService alfrescoService;


	@PostMapping("/tax-calculator")
	public ResponseEntity<?> taxCalculator(@RequestBody CalculateTaxRequest calculateTaxRequest) {

//		service.calculateTax(calculateTaxRequest);
//
//		return ResponseEntity.ok("Tax calculated successfully!!!");
		return ResponseEntity.ok(service.calculateTax(calculateTaxRequest));
	}
	
	@PostMapping("/tax-calculator/preview")
	public ResponseEntity<List<CalculateTaxPreviewResponse>> taxCalculatorPreview(
	        @RequestBody CalculateTaxRequest calculateTaxRequest) {

	    return ResponseEntity.ok(
	            service.taxCalculatorPreview(calculateTaxRequest)
	    );
	}



	@PostMapping("/update-tracker-bill-status")
	public ResponseEntity<?> updateTrackerBillStatus(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.updateTrackerBillStatus(requestInfoWrapper);

		return ResponseEntity.ok("Tracker bill status updated successfully!!!");
//		return ResponseEntity.ok(service.updateTrackerBillStatus(requestInfoWrapper));
	}

	@PostMapping("/reverse-rebate-amount")
	public ResponseEntity<?> reverseRebateAmount(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.reverseRebateAmount(requestInfoWrapper);

		return ResponseEntity.ok("Rebate amount reversed successfully!!!");
//		return ResponseEntity.ok(service.reverseRebateAmount(requestInfoWrapper));
	}

	@PostMapping("/update-penalty-amount")
	public ResponseEntity<?> updatePenaltyAmount(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		service.updatePenaltyAmount(requestInfoWrapper);

		return ResponseEntity.ok("Penalty amount updated successfully!!!");
//		return ResponseEntity.ok(service.updatePenaltyAmount(requestInfoWrapper)); 
	}
	
	@PostMapping("/_updateCustomAmount")
	public ResponseEntity<CustomAmountUpdateResponse> updateCustomAmount(@RequestBody CustomAmountUpdateRequest request) {

	    CustomAmountUpdateResponse response = service.updateCustomAmount(request);
	    return ResponseEntity.ok(response);
	}
	
	
	@PostMapping("/bulk-uploads")
	public ResponseEntity<Map<String, Object>> bulkBillUploads(
	        @RequestBody RequestInfoWrapper requestInfoWrapper,@RequestParam(value = "isforce", required = false) String isforce,@RequestParam(value = "ulbName", required = false) String ulbName,@RequestParam(value = "wardName", required = false) String wardName, @RequestParam(value = "created_at", required = false) String created_at) throws Exception {

	    boolean status = service.uploadBulkBills(requestInfoWrapper, isforce, ulbName, wardName, created_at);

	    Map<String, Object> response = new HashMap<>();
	    response.put("status", status);

	    return ResponseEntity.ok(response);
	}	
	
	@PostMapping("/extract-tracker")
	public ResponseEntity<?> getTrackerByBillId(@RequestBody BillIdRequest request) {
	    return ResponseEntity.ok(service.getTrackerByBillId(request));
	}
	
	@PostMapping("/send-property-email")
	public ResponseEntity<?> sendPropertyEmail(@RequestBody SendPropertyEmailRequest request) {
	    service.sendPropertyEmail(request);
	    return ResponseEntity.ok("Property email sent successfully");
	}
	
	@PostMapping("/send-property-notice")
	public ResponseEntity<?> sendPropertyNotice(@RequestBody SendPropertyNoticeRequest request) {
	    service.sendPropertyNotice(request);
	    return ResponseEntity.ok("Property Notice sent successfully");
	}
}