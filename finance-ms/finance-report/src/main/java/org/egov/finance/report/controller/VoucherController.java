/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */

package org.egov.finance.report.controller;

import java.util.HashMap;
import java.util.Map;

import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.model.request.VoucherPrintRequest;
import org.egov.finance.report.service.VoucherReportService;
import org.egov.finance.report.util.ReportConstants;
import org.egov.finance.report.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class VoucherController {

	@Autowired
	private VoucherReportService voucherReportService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/voucher/_print")
	public ResponseEntity<Resource> pritn(@Valid @RequestBody VoucherPrintRequest request) {
		MediaType mediaType = null;
		String fileExt = null;
		Map<String,String> errormap = new HashMap<>();
		if(ObjectUtils.isEmpty( request.getVoucher().getId()))
			errormap.put(ReportConstants.INVALID_ID_PASSED,
					ReportConstants.INVALID_ID_PASSED_MESSAGE);
		switch (request.getVoucher().getType()) {
			case PDF -> {
				mediaType = MediaType.APPLICATION_PDF;
				fileExt = "pdf";
			}
			case XLS -> {
				mediaType = MediaType.parseMediaType("application/vnd.ms-excel");
				fileExt = "xls";
			}
			case UNKNOWN -> {
				errormap.put(ReportConstants.INVALID_PARAMETERS_FILE_TYPE,
						ReportConstants.INVALID_PARAMETERS_FILE_TYPE_MSG);
				
			}
		}
		
		if(!errormap.isEmpty())
			throw new ReportServiceException(errormap);
		Resource pdf = null;// voucherReportService.generateReport(request);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=JournalVoucherReport." + fileExt)
				.contentType(mediaType).body(pdf);

	}

}
