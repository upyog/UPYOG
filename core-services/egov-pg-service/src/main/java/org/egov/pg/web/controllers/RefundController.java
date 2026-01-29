package org.egov.pg.web.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.egov.pg.models.Refund;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.models.RefundResponse;
import org.egov.pg.models.Transaction;
import org.egov.pg.service.RefundService;
import org.egov.pg.utils.ResponseInfoFactory;
import org.egov.pg.web.models.RefundCriteria;
import org.egov.pg.web.models.RefundInitiateResponse;
import org.egov.pg.web.models.RequestInfoWrapper;
import org.egov.pg.web.models.ResponseInfo;
import org.egov.pg.web.models.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RefundController {

	private final RefundService refundService;
	
	@Autowired
	public RefundController(RefundService refundService) {
		this.refundService=refundService;
	}

	 @RequestMapping(value = "/refund/v1/_create", method = RequestMethod.POST)
	    public ResponseEntity<RefundInitiateResponse> refundV1Initiate(@Valid @RequestBody RefundRequest refundRequest) {
            
		 Refund refund = refundService.initiateRefund(refundRequest);
	        ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(refundRequest
	                .getRequestInfo(), true);
	        RefundInitiateResponse response = new RefundInitiateResponse(responseInfo, refund);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	 
	 
	  @RequestMapping(value = "/refund/v1/_search", method = RequestMethod.POST)
	    public ResponseEntity<RefundResponse> refundV1SearchPost(@Valid @RequestBody RequestInfoWrapper
	                                                                                requestInfoWrapper, @Valid
	                                                                        @ModelAttribute RefundCriteria refundCriteria) {
		  refundCriteria.setOffset(0);
		  refundCriteria.setLimit(5);
	        List<Refund> refunds = refundService.getRefundTransaction(refundCriteria);
	        ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper
	                .getRequestInfo(), true);
	        RefundResponse response = new RefundResponse(responseInfo, refunds);

	        return new ResponseEntity<>(response, HttpStatus.OK);

	    }
	  
	  
	  @RequestMapping(value = "/refund/v1/_update", method = {RequestMethod.POST, RequestMethod.GET})
	    public ResponseEntity<RefundResponse> refundV1UpdatePost(@RequestBody RequestInfoWrapper
	                                                                                requestInfoWrapper, @RequestParam
	                                                                                Map<String,
	                                                                                        String> params) {
	        List<Refund> transactions = refundService.updateRefundTransaction(requestInfoWrapper.getRequestInfo(), params);
	        ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper
	                .getRequestInfo(), true);
	        RefundResponse response = new RefundResponse(responseInfo, transactions);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

}
