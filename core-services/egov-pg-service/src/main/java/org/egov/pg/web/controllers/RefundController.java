package org.egov.pg.web.controllers;

import java.util.List;
import java.util.Map;

import org.egov.pg.models.CollectionPayment;
import org.egov.pg.models.CollectionPaymentResponse;
import org.egov.pg.models.PaymentRefund;
import org.egov.pg.models.PaymentRefundResponse;
import org.egov.pg.models.PaymentRequest;
import org.egov.pg.models.PaymentResponse;
import org.egov.pg.models.Refund;
import org.egov.pg.models.RefundRequest;
import org.egov.pg.models.RefundResponse;
import org.egov.pg.service.RefundService;
import org.egov.pg.utils.ResponseInfoFactory;
import org.egov.pg.web.models.RefundCriteria;
import org.egov.pg.web.models.RefundInitiateResponse;
import org.egov.pg.web.models.RequestInfoWrapper;
import org.egov.pg.web.models.ResponseInfo;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/refund/v1")
public class RefundController {

	private final RefundService refundService;
	
	public RefundController(RefundService refundService) {
		this.refundService=refundService;
	}
	
	 @RequestMapping(value = "/_create", method = RequestMethod.POST)
	    public ResponseEntity<PaymentRefundResponse> refundV1Initiate(@Valid @RequestBody PaymentRequest paymentRequest) {
         
		 PaymentRefund processRefund = refundService.processRefund(paymentRequest);
	        ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(paymentRequest
	                .getRequestInfo(), true);
	        PaymentRefundResponse response = PaymentRefundResponse.builder()
	                .responseInfo(responseInfo)
	                .paymentRefund(processRefund)
	                .build();
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	 
	  @RequestMapping(value = "/_search", method = RequestMethod.POST)
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
	  
	  
	  @RequestMapping(value = "/_update", method = {RequestMethod.POST, RequestMethod.GET})
	    public ResponseEntity<RefundResponse> refundV1UpdatePost(@RequestBody RequestInfoWrapper
	                                                                                requestInfoWrapper, @RequestParam
	                                                                                Map<String,
	                                                                                        String> params) {
	        List<Refund> refunds = refundService.updateRefundTransaction(requestInfoWrapper.getRequestInfo(), params);
	        ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper
	                .getRequestInfo(), true);
	        RefundResponse response = new RefundResponse(responseInfo, refunds);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

}
