package org.hpud.razorpay.service;

import java.util.LinkedHashMap;

import org.hpud.razorpay.contract.OrderRequest;
import org.hpud.razorpay.contract.OrderResponse;
import org.hpud.razorpay.contract.RequestInfoWrapper;
import org.hpud.razorpay.exception.RazorpayException;
import org.hpud.razorpay.repo.RestCallRepository;
import org.hpud.razorpay.util.RazorpayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RazorpayService {

	@Autowired
	private RestCallRepository restCallRepository;

	@Autowired
	private ObjectMapper objectMapper;
	RazorpayUtil util = null;
	StringBuilder uri = null;

	public OrderResponse createOrderForRazorpay(OrderRequest orderRequest, OrderResponse orderResponse,
			RequestInfoWrapper requestInfoWrapper) throws RazorpayException {
		
		util = new RazorpayUtil();
		uri = new StringBuilder(util.BILLING_URI);
		billCall(orderRequest, uri, requestInfoWrapper);
		return orderResponse;

	}

	private Object billCall(OrderRequest orderRequest, StringBuilder uri, RequestInfoWrapper requestInfoWrapper)
			throws RazorpayException {

		uri.append("?tenantId=").append(requestInfoWrapper.getRequestInfo().getUserInfo().getTenantId());
		uri.append("&billNumber=").append(orderRequest.getCreateOrder().getBillNo());
		try {
			LinkedHashMap responseMap = (LinkedHashMap) restCallRepository.fetchResult(uri, requestInfoWrapper);
//			parseResponse(responseMap,dobFormat);
			Object responseObject = objectMapper.convertValue(responseMap, Object.class);
			return responseObject;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("IllegalArgumentException : ObjectMapper not able to convertValue in billCall.");
		}
	}

}
