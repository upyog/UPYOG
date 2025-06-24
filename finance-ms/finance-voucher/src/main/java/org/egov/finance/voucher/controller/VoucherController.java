/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */

package org.egov.finance.voucher.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.Voucher;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.exception.TaskFailedException;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.model.AccountDetailModel;
import org.egov.finance.voucher.model.FundModel;
import org.egov.finance.voucher.model.ResponseInfo;
import org.egov.finance.voucher.model.SubledgerDetailModel;
import org.egov.finance.voucher.model.VoucherDetails;
import org.egov.finance.voucher.model.request.FundRequest;
import org.egov.finance.voucher.model.request.VoucherRequest;
import org.egov.finance.voucher.model.response.FundResponse;
import org.egov.finance.voucher.model.response.VoucherResponse;
import org.egov.finance.voucher.service.AppConfigValueService;
import org.egov.finance.voucher.service.ChartOfAccountDetailService;
import org.egov.finance.voucher.service.CreateVoucher;
import org.egov.finance.voucher.service.FundService;
import org.egov.finance.voucher.service.JournalVoucherResponse;
import org.egov.finance.voucher.service.VoucherCreateService;
import org.egov.finance.voucher.service.VoucherService;
import org.egov.finance.voucher.util.ResponseInfoFactory;
import org.egov.finance.voucher.util.VoucherConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/voucher")
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
}
