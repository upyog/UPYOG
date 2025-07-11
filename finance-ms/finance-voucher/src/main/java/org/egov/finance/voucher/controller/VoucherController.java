/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */

package org.egov.finance.voucher.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.Voucher;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.model.EgModules;
import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.ResponseInfo;
import org.egov.finance.voucher.model.request.VoucherRequest;
import org.egov.finance.voucher.model.response.VoucherResponse;
import org.egov.finance.voucher.service.VoucherCreateService;
import org.egov.finance.voucher.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.EncodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/voucher")
@Slf4j
public class VoucherController {

	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private VoucherCreateService voucherCreateService;

//	@PostMapping("/journalvoucher/_create")
//	public ResponseEntity<VoucherResponse> createJournalVoucher(@RequestBody JournalVoucherRequest request) {
//	    try {
//	        JournalVoucherResponse response = journalVoucherService.createJournalVoucher(request);
//	        return ResponseEntity.ok().body(response);
//	    } catch (ValidationException e) {
//	        throw new CustomBadRequestException("Validation Failed", e.getMessage());
//	    }
//	}

	@PostMapping("/_create")
	public ResponseEntity<VoucherResponse> createVoucher(@Valid @RequestBody VoucherRequest voucherRequest) {
		VoucherResponse response = voucherCreateService.processVoucherCreate(voucherRequest);

		// Set ResponseInfo
		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(voucherRequest.getRequestInfo(), true);
		response.setResponseInfo(responseInfo);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Search voucher by service code and reference document
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping(value = "/_searchbyserviceandreference", produces = "application/json")
	public ResponseEntity<VoucherResponse> searchVoucherByServiceCodeAndReferenceDoc(
			@RequestParam(name = "servicecode", required = false) String serviceCode,
			@RequestParam(name = "referencedocument") String referenceDocument, @RequestBody RequestInfo requestInfo)
			throws UnsupportedEncodingException {

		VoucherResponse response = new VoucherResponse();

		try {
			// Encode reference document safely
			// referenceDocument = ESAPI.encoder().encodeForURL(referenceDocument);
			referenceDocument = URLEncoder.encode(referenceDocument, StandardCharsets.UTF_8.toString());

			List<CVoucherHeader> voucherHeaders = voucherCreateService
					.getVoucherByServiceNameAndReferenceDocument(serviceCode, referenceDocument);

			if (voucherHeaders == null || voucherHeaders.isEmpty()) {
				response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, false));
				response.setVouchers(new ArrayList<>());
			} else {
				response.setVouchers(voucherHeaders.stream().map(Voucher::new).collect(Collectors.toList()));
				response.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true));
			}
			return ResponseEntity.ok(response);

		} catch (EncodingException e) {
			throw new ApplicationRuntimeException("Failed to encode reference document", e);
		}
	}

	/**
	 * API to check if manual receipt date is enabled
	 */
	@PostMapping("/_ismanualreceiptdateenabled")
	public ResponseEntity<AppConfigValues> getManualReceiptDateConsiderationForVoucher() {
		try {
			AppConfigValues configValue = voucherCreateService.isManualReceiptDateEnabledForVoucher();
			return ResponseEntity.ok(configValue);
		} catch (HttpClientErrorException e) {
			log.error("Failed to fetch config for manual receipt date: {}", e.getMessage(), e);
			throw new ApplicationRuntimeException("Unable to fetch configuration", e);
		}
	}

	/**
	 * API to fetch EgModules ID by module name
	 */
	@PostMapping("/_getmoduleidbyname")
	public ResponseEntity<EgModules> getEgModuleIdByName(@RequestParam("moduleName") String moduleName) {
		try {
			EgModules module = voucherCreateService.getModulesIdByName(moduleName);
			return ResponseEntity.ok(module);
		} catch (HttpClientErrorException e) {
			log.error("Failed to fetch module ID for name {}: {}", moduleName, e.getMessage(), e);
			throw new ApplicationRuntimeException("Unable to fetch module ID", e);
		}
	}

}
