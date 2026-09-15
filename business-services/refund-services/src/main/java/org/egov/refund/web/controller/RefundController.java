package org.egov.refund.web.controller;

import java.util.List;

import org.egov.refund.model.Refund;
import org.egov.refund.service.RefundService;
import org.egov.refund.web.contracat.RefundGetRequest;
import org.egov.refund.web.contracat.RefundRequest;
import org.egov.refund.web.contracat.RefundResponse;
import org.egov.refund.web.contracat.RefundSearchRequest;
import org.egov.refund.web.contracat.RefundSearchResponse;
import org.egov.refund.web.factory.ResponseInfoFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refund/v1")
public class RefundController {

	private final RefundService refundService;
	private final ResponseInfoFactory responseInfoFactory;

	public RefundController(RefundService refundService, ResponseInfoFactory responseInfoFactory) {
		this.refundService = refundService;
		this.responseInfoFactory = responseInfoFactory;
	}

	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<RefundResponse> create(@RequestBody RefundRequest request) {

		Refund refund = refundService.create(request);

		RefundResponse refundResponse = RefundResponse.builder().refund(refund)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(refundResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/_update", method = RequestMethod.POST)
	public ResponseEntity<RefundResponse> update(@RequestBody RefundRequest request) {

		Refund refund = refundService.update(request);

		RefundResponse refundResponse = RefundResponse.builder().refund(refund)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(refundResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<RefundSearchResponse> refundSearch(@RequestBody RefundSearchRequest request) {

		List<Refund> refunds = refundService.search(request);

		RefundSearchResponse refundResponse = RefundSearchResponse.builder().refunds(refunds)
				.responseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(refundResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/_get", method = RequestMethod.POST)
	public ResponseEntity<RefundResponse> get(@RequestBody RefundGetRequest request) {

		Refund refund = refundService.get(request);

		RefundResponse refundResponse = RefundResponse.builder().refund(refund)
				.responseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(refundResponse, HttpStatus.OK);
	}
}