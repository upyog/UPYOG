package org.egov.demand.web.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.demand.helper.BillHelperV2;
import org.egov.demand.model.BillSearchCriteria;
import org.egov.demand.model.GenerateBillCriteria;
import org.egov.demand.model.UpdateBillRequest;
import org.egov.demand.service.BillServicev2;
import org.egov.demand.util.Constants;
import org.egov.demand.web.contract.BillRequestV2;
import org.egov.demand.web.contract.BillResponseV2;
import org.egov.demand.web.contract.ShortBillResponseV2;
import org.egov.demand.web.contract.ShortBillV2;
import org.egov.demand.web.contract.RequestInfoWrapper;
import org.egov.demand.web.contract.factory.ResponseFactory;
import org.egov.demand.web.validator.BillValidator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bill/v2/")
public class BillControllerv2 {
	
	@Autowired
	private BillServicev2 billService;
	
	@Autowired
	private ResponseFactory responseFactory;
	
	@Autowired
	private BillValidator billValidator;
	
	@Autowired
	private BillHelperV2 billHelper;
	
	@PostMapping("_search")
	@ResponseBody
	public ResponseEntity<?> search(@RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute @Valid final BillSearchCriteria billCriteria) {

		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		billValidator.validateBillSearchCriteria(billCriteria, requestInfo);
		return new ResponseEntity<>(billService.searchBill(billCriteria,requestInfo), HttpStatus.OK);
	}

	/**
	 * Searches and returns a compact list of bills containing only core fields
	 * (id, totalAmount, businessService, billNumber, billDate, consumerCode).
	 *
	 * @param requestInfoWrapper Request body envelope containing RequestInfo
	 * @param billCriteria Query parameters mapping search criteria (tenantId, mobileNumber, isActive, etc.)
	 * @return ResponseEntity containing ShortBillResponseV2
	 */
	@PostMapping("short/_search")
	@ResponseBody
	public ResponseEntity<?> shortSearch(@RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute @Valid final BillSearchCriteria billCriteria) {

		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		billValidator.validateBillSearchCriteria(billCriteria, requestInfo);
		
		BillResponseV2 fullResponse = billService.searchBill(billCriteria, requestInfo);
		
		java.util.List<ShortBillV2> shortBills = new java.util.ArrayList<>();
		if (fullResponse.getBill() != null) {
			for (org.egov.demand.model.BillV2 b : fullResponse.getBill()) {
				ShortBillV2 sb = ShortBillV2.builder()
						.id(b.getId())
						.totalAmount(b.getTotalAmount())
						.businessService(b.getBusinessService())
						.billNumber(b.getBillNumber())
						.billDate(b.getBillDate())
						.consumerCode(b.getConsumerCode())
						.build();
				shortBills.add(sb);
			}
		}
		
		ShortBillResponseV2 shortResponse = ShortBillResponseV2.builder()
				.responseInfo(fullResponse.getResposneInfo())
				.bill(shortBills)
				.build();
				
		return new ResponseEntity<>(shortResponse, HttpStatus.OK);
	}


	@PostMapping("_fetchbill")
	@ResponseBody
	public ResponseEntity<?> fetchBill(@RequestBody RequestInfoWrapper requestInfoWrapper, 
			@ModelAttribute @Valid GenerateBillCriteria generateBillCriteria){
		
		BillResponseV2 billResponse = billService.fetchBill(generateBillCriteria, requestInfoWrapper);
		return new ResponseEntity<>(billResponse, HttpStatus.CREATED);
	}
	
	
	@PostMapping("_generate")
	@ResponseBody
	public ResponseEntity<?> genrateBill(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute @Valid GenerateBillCriteria generateBillCriteria) {

		throw new CustomException("EG_BS_API_ERROR", "The Generate bill API has been deprecated, Access the fetchBill");
	}
	
	@PostMapping("_create")
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody @Valid BillRequestV2 billRequest, BindingResult bindingResult){

		BillResponseV2 billResponse = billService.sendBillToKafka(billRequest);
		billHelper.getBillRequestWithIds(billRequest);
		return new ResponseEntity<>(billResponse,HttpStatus.CREATED);
	}
	
	@PostMapping("_cancelbill")
	@ResponseBody
	public ResponseEntity<?> cancelBill(@RequestBody @Valid UpdateBillRequest updateBillRequest){

		Integer count = billService.cancelBill(updateBillRequest);
		
		HttpStatus status;
		String responseMsg;
		
		if (count > 0) {
			status = HttpStatus.OK;
			responseMsg = Constants.SUCCESS_CANCEL_BILL_MSG.replace(Constants.COUNT_REPLACE_CANCEL_BILL_MSG,
					count.toString());
		} else {
			status = HttpStatus.BAD_REQUEST;
			responseMsg = count < 0 ? Constants.PAID_CANCEL_BILL_MSG : Constants.FAILURE_CANCEL_BILL_MSG;
		}
		
		ResponseInfo responseInfo = responseFactory.getResponseInfo(updateBillRequest.getRequestInfo(), status);
		Map<String, Object> responseMap = new HashMap<>(); 
		responseMap.put(Constants.RESPONSEINFO_STRING, responseInfo);
		responseMap.put(Constants.MESSAGE_STRING, responseMsg);
		return new ResponseEntity<>(responseMap, status);
	}
}
