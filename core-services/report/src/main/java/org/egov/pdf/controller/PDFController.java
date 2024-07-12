package org.egov.pdf.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.egov.pdf.model.HtmlContentResponse;
import org.egov.pdf.model.PDFRequest;
import org.egov.pdf.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pdf-service/v1")
public class PDFController {

	@Autowired
	private PDFService pdfService;

	@PostMapping("/_create")
	public ResponseEntity<Resource> createnosave(@RequestBody @Valid PDFRequest pdfRequest) {
		return pdfService.generatePdf(pdfRequest);
	}

	@GetMapping("/_convert")
	public ResponseEntity<String> convertToBase64(@RequestParam("url") String url) {
		URL u = null;
		InputStream is = null;
		byte[] imageBytes = new byte[0];
		try {
			u = new URL(url);
			is = u.openStream();
			imageBytes = IOUtils.toByteArray(is);
		} catch (IOException e) {
			System.err.printf("Failed while reading bytes from %s: %s", u.toExternalForm(), e.getMessage());
			e.printStackTrace();
			// Perform any other exception handling that's appropriate.
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String resource = "";
		// File file = new
		// File("http://localhost:8080/edcr/rest/dcr/downloadfile?tenantId=ga&fileStoreId=7c24188f-a719-4abc-ae98-325e0353e348");
		byte[] encoded = Base64.getEncoder().encode(imageBytes);
		resource = new String(encoded, StandardCharsets.US_ASCII);
		return ResponseEntity.ok().body(resource);
	}
	@PostMapping("/html-content/_convert")
	public HtmlContentResponse generateConvertedHtmlContent(@RequestBody PDFRequest pdfRequest) throws IOException {
		HtmlContentResponse response = pdfService.generateConvertedHtmlContent(pdfRequest);
		return response;
	}

}
