package org.egov.refund.web.controller;

import org.egov.refund.service.RefundService;
import org.egov.refund.web.contracat.RefundGetRequest;
import org.egov.refund.web.contracat.RefundRequest;
import org.egov.refund.web.contracat.RefundResponse;
import org.egov.refund.web.contracat.RefundSearchRequest;
import org.egov.refund.web.contracat.RefundSearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refund/v1")
public class RefundController {

	private final RefundService refundService;

	public RefundController(RefundService refundService) {
		this.refundService = refundService;
	}

	@PostMapping("/_create")
	public ResponseEntity<RefundResponse> create(@RequestBody RefundRequest request) {

		return ResponseEntity.ok(refundService.create(request));
	}

	@PostMapping("/_update")
	public ResponseEntity<RefundResponse> update(@RequestBody RefundRequest request) {
		return ResponseEntity.ok(refundService.update(request));
	}

	@PostMapping("/_search")
	public ResponseEntity<RefundSearchResponse> search(@RequestBody RefundSearchRequest request) {

		return ResponseEntity.ok(refundService.search(request));
	}

	@PostMapping("/_get")
	public ResponseEntity<RefundResponse> get(@RequestBody RefundGetRequest request) {

		return ResponseEntity.ok(refundService.get(request));
	}

}