package org.egov.egf.web.controller.microservice;

import org.apache.commons.lang.StringUtils;
import org.egov.egf.contract.model.RefundPaymentCreateRequest;
import org.egov.egf.contract.model.RefundPaymentCreateResponse;
import org.egov.egf.contract.model.RefundPaymentResponseDetail;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.model.refund.RefundApplication;
import org.egov.services.refund.RefundPaymentVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/refund/payment")
public class RefundPaymentController {

	@Autowired
	private RefundPaymentVoucherService refundPaymentVoucherService;

	@RequestMapping(value = "/_create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public RefundPaymentCreateResponse create(@RequestParam("tenantId") final String tenantId,
			@RequestBody final RefundPaymentCreateRequest request) {

		validateTenant(tenantId, request);

		final RefundApplication refundApplication = refundPaymentVoucherService.createPaymentVoucher(request);

		final RefundPaymentResponseDetail responseDetail = new RefundPaymentResponseDetail();

		responseDetail.setRefundApplicationNumber(refundApplication.getRefundApplicationNumber());

		responseDetail.setJournalVoucherNumber(refundApplication.getVoucherNumber());

		responseDetail.setPaymentVoucherNumber(refundApplication.getPaymentVoucherNumber());

		responseDetail.setGatewayRefundId(refundApplication.getGatewayRefundId());

		responseDetail.setPaymentStatus(refundApplication.getRefundPaymentStatus());

		final RefundPaymentCreateResponse response = new RefundPaymentCreateResponse();

		response.setResponseInfo(MicroserviceUtils.getResponseInfo(request.getRequestInfo(), 200, "Rainmaker"));

		response.setRefundPayment(responseDetail);

		return response;
	}

	private void validateTenant(final String tenantId, final RefundPaymentCreateRequest request) {

		if (StringUtils.isBlank(tenantId)) {
			throw new IllegalArgumentException("Tenant ID query parameter is mandatory");
		}

		if (request == null) {
			throw new IllegalArgumentException("Refund payment request is mandatory");
		}

		if (StringUtils.isBlank(request.getTenantId())) {
			throw new IllegalArgumentException("Tenant ID is mandatory in request body");
		}

		if (!tenantId.equalsIgnoreCase(request.getTenantId())) {

			throw new IllegalArgumentException("Tenant ID in query parameter and " + "request body must match");
		}
	}
}