/**
 * @author bpattanayak
 * @date 30 Jun 2025
 */

package org.egov.finance.report.controller;

import java.io.IOException;
import java.io.InputStream;

import org.egov.finance.report.model.Statement;
import org.egov.finance.report.model.request.BalanceSheetReportRequest;
import org.egov.finance.report.service.BalanceSheetReportService;
import org.egov.finance.report.service.BalanceSheetService;
import org.egov.finance.report.util.ReportHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

@RestController
public class BalanceSheetReportController {
	
	@Autowired
	BalanceSheetReportService balanceSheetReportService;
	
	@Autowired
	BalanceSheetService balanceSheetService;
	
	@Autowired
	ReportHelper reportHelper;

	@PostMapping("/report/balanceSheetReport-generateBalanceSheetPdf")
	public ResponseEntity<byte[]> generateBalanceSheetPdf(@RequestBody BalanceSheetReportRequest balanceSheetReportRequest) throws JRException, IOException {
		InputStream inputStream = null;
		StringBuffer header = new StringBuffer();
		balanceSheetReportService.populateDataSource(balanceSheetReportRequest.getStatement());
		final JasperPrint jasper = reportHelper.generateFinancialStatementReportJasperPrint(balanceSheetReportRequest.getStatement(),
				balanceSheetService.getMessage("report.heading"), header.toString(), balanceSheetService.getCurrentYearToDate(balanceSheetReportRequest.getStatement()), balanceSheetService.getPreviousYearToDate(), true);
		inputStream = reportHelper.exportPdf(inputStream, jasper);
		byte[] pdfBytes = inputStream.readAllBytes();
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_PDF);
	    headers.setContentDisposition(ContentDisposition.builder("attachment")
	            .filename("report.pdf")
	            .build());
	    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
		//return BALANCE_SHEET_PDF;
	}
}
