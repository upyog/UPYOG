/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */

package org.egov.finance.report.controller;

import java.util.Arrays;
import java.util.List;

import org.egov.finance.report.model.FundModel;
import org.egov.finance.report.model.ResponseInfo;
import org.egov.finance.report.model.request.FundRequest;
import org.egov.finance.report.model.response.FundResponse;
import org.egov.finance.report.service.FundService;
import org.egov.finance.report.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class VoucherController {

	private FundService fundService;

	private ResponseInfoFactory responseInfoFactory;


	@PostMapping("/voucher/_print")
	public ResponseEntity<Resource> pritn(@Valid @RequestBody FundRequest fund) {
		 Resource pdf =null;// voucherReportService.generatePdfReport(id);
	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=JournalVoucherReport.pdf")
	                .contentType(MediaType.APPLICATION_PDF)
	                .body(pdf);
	}

}
