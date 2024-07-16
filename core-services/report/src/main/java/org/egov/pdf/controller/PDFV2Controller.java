package org.egov.pdf.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.egov.pdf.model.HtmlContentResponse;
import org.egov.pdf.model.PDFRequest;
import org.egov.pdf.model.PdfHeaderFooterRequestWrapper;
import org.egov.pdf.model.PdfHeaderFooterResponseWrapper;
import org.egov.pdf.service.PdfV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pdf-service/v2")
public class PDFV2Controller {

	@Autowired
	private PdfV2Service pdfService;

	@PostMapping("/html-content/placeholder/_replacement")
	public HtmlContentResponse htmlPlaceholderReplacement(@RequestBody PDFRequest pdfRequest) throws IOException {
		HtmlContentResponse response = pdfService.htmlPlaceholderReplacement(pdfRequest);
		return response;
	}

	@PostMapping("/_create")
	public ResponseEntity<Resource> createnosave(@RequestBody @Valid PDFRequest pdfRequest) throws IOException {
		return pdfService.generatePdf(pdfRequest);
	}

	@PostMapping("/header-footer/_update")
	public ResponseEntity<PdfHeaderFooterResponseWrapper> addHeaderFooter(
			@RequestBody PdfHeaderFooterRequestWrapper pdfHeaderFooterRequest) throws IOException {

		return new ResponseEntity<>(pdfService.addHeaderFooter(pdfHeaderFooterRequest), HttpStatus.OK);
	}

	@PostMapping("/_merge")
	@ResponseBody
	public ResponseEntity<Resource> pdfMerge(@RequestParam(value = "files", required = false) List<MultipartFile> files,
			@RequestParam(value = "fileStoreIds", required = false) List<String> fileStoreIds) {

		return pdfService.mergePdf(files, fileStoreIds);
	}
}
