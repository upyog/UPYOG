package org.egov.demand.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.demand.helper.BillHelperV2;
import org.egov.demand.model.BillSearchCriteria;
import org.egov.demand.model.GenerateBillCriteria;
import org.egov.demand.model.UpdateBillCriteria;
import org.egov.demand.model.UpdateBillRequest;
import org.egov.demand.service.BillServicev2;
import org.egov.demand.util.Constants;
import org.egov.demand.web.contract.BillRequestV2;
import org.egov.demand.web.contract.BillResponseV2;
import org.egov.demand.web.contract.CancelBillCriteria;
import org.egov.demand.web.contract.RequestInfoWrapper;
import org.egov.demand.web.contract.factory.ResponseFactory;
import org.egov.demand.web.validator.BillValidator;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
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

import lombok.extern.slf4j.Slf4j;

import org.egov.demand.util.Constants;

@RestController
@Slf4j
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
		log.info("bill _search billcriteria : "+billCriteria); // added logger
		billValidator.validateBillSearchCriteria(billCriteria, requestInfo);
		return new ResponseEntity<>(billService.searchBill(billCriteria,requestInfo), HttpStatus.OK);
	}


	@PostMapping("_fetchbill")
	@ResponseBody
	public ResponseEntity<?> fetchBill(@RequestBody RequestInfoWrapper requestInfoWrapper, 
			@ModelAttribute @Valid GenerateBillCriteria generateBillCriteria){
		
		String ojb = new JSONObject(generateBillCriteria).toString();
		log.info(" fetchBill GenerateBillCriteria::"+ ojb);
		
		BillResponseV2 billResponse = billService.fetchBill(generateBillCriteria, requestInfoWrapper);
		log.info("_fetchbill generateBillCriteria :"+generateBillCriteria ); //added logger
		log.info("_fetchbill response :"+billResponse ); //added logger
		
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

		billHelper.getBillRequestWithIds(billRequest);
		log.info("_create bill request sent to kafka : "+billRequest); //added logger
		BillResponseV2 billResponse = billService.sendBillToKafka(billRequest);
		log.info("_create bill response : "+billResponse); //added logger
		return new ResponseEntity<>(billResponse,HttpStatus.CREATED);
	}
	
	@PostMapping("_cancelbill")
	@ResponseBody
	public ResponseEntity<?> cancelBill(@RequestBody RequestInfoWrapper requestInfoWrapper, 
			@ModelAttribute @Valid CancelBillCriteria cancelBillCriteria){
		log.info("_cancelbill criteria : "+cancelBillCriteria);
		UpdateBillRequest updateBillRequest =  new UpdateBillRequest();
		updateBillRequest.setRequestInfo(requestInfoWrapper.getRequestInfo());
		UpdateBillCriteria objectBillCriteria =new UpdateBillCriteria();
		objectBillCriteria.setTenantId(cancelBillCriteria.getTenantId());
		//objectBillCriteria.setConsumerCodes(Set.of(cancelBillCriteria.getConsumerCode().split(",")));
		objectBillCriteria.setBusinessService(cancelBillCriteria.getBusinessService());
		
		updateBillRequest.setUpdateBillCriteria(objectBillCriteria);
		billService.cancelBill(updateBillRequest);
		return new ResponseEntity<>(Constants.SUCCESS_CANCEL_BILL, HttpStatus.CREATED);
	}
	
	@PostMapping("_cancelbillv2")
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
