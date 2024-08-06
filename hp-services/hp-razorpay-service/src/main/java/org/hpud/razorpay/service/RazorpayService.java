package org.hpud.razorpay.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.catalina.mapper.Mapper;
import org.hpud.razorpay.contract.BillResponseV2;
import org.hpud.razorpay.contract.BillV2;
import org.hpud.razorpay.contract.OrderRequest;
import org.hpud.razorpay.contract.OrderResponse;
import org.hpud.razorpay.contract.RequestInfoWrapper;
import org.hpud.razorpay.exception.RazorpayException;
import org.hpud.razorpay.model.CreateOrderResponse;
import org.hpud.razorpay.repo.RestCallRepository;
import org.hpud.razorpay.util.RazorpayUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class RazorpayService {

	@Autowired
	private RestCallRepository restCallRepository;

	@Autowired
	private ObjectMapper objectMapper;
	RazorpayUtil util = null;
	StringBuilder uri = null;
	BillResponseV2 billResponse = null;

	public OrderResponse createOrderForRazorpay(OrderRequest orderRequest, OrderResponse orderResponse)
			throws RazorpayException {
		JSONObject orderCreatreRequest = new JSONObject();
		util = new RazorpayUtil();
		BillV2 billAmount = null;
		String id = null;
		String entity = null;
		String amount = null;
		String amountPaid = null;
		String amountDue = null;
		String currency=null;
		String receipt=null;
		String offerId=null;
		String status=null;
		String attempts=null;
		String notes1=null;
		String createdAt=null;
		CreateOrderResponse createOrderResponse = new CreateOrderResponse();
		JSONObject notes = null;
		Order order = null;
		try {

			uri = new StringBuilder(util.BILLING_URI);
			billResponse = billCall(orderRequest, uri);
			if (CollectionUtils.isEmpty(billResponse.getBill())) {
				throw new RazorpayException("Bill not Found!!!");
			}
			if (!CollectionUtils.isEmpty(billResponse.getBill())) {
				billAmount = billResponse.getBill().get(0);
			}
			// total amount cannot be zero
			if (billAmount.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
				throw new RazorpayException("Bill amount cannot be Zero or less then zero");
			}
			RazorpayClient razorpay = new RazorpayClient(util.LIVE_KEY_ID, util.KEY_SECRET);
			orderCreatreRequest.put("amount", billAmount.getTotalAmount());
			orderCreatreRequest.put("currency", "INR");
			orderCreatreRequest.put("receipt", "receipt#1");
			notes = new JSONObject();
			notes.put("notes_key_1", "Tea, Earl Grey, Hot");
			orderCreatreRequest.put("notes", notes);
			order = razorpay.orders.create(orderCreatreRequest);
			if (null == order) {
				throw new RazorpayException("Order is not Generated!!!");

			}
			id = order.getModelJson().get("id").toString();
			entity = order.getModelJson().get("entity").toString();
			amount =  order.getModelJson().get("amount").toString();
			amountPaid = order.getModelJson().get("amount_paid").toString();
			amountDue=order.getModelJson().get("amount_due").toString();
			currency = order.getModelJson().get("currency").toString();
			receipt=order.getModelJson().get("receipt").toString();
			offerId=order.getModelJson().get("offer_id").toString();
			status=order.getModelJson().get("status").toString();
			attempts=order.getModelJson().get("attempts").toString();
			notes1=order.getModelJson().get("notes").toString();
			createdAt=order.getModelJson().get("created_at").toString();
			createOrderResponse.setAmount(amount);
			createOrderResponse.setAmountDue(amountDue);
			createOrderResponse.setAmountPaid(amountPaid);
			createOrderResponse.setAttempts(Integer.parseInt(attempts));
			createOrderResponse.setCreatedAt(Long.parseLong(createdAt));
			createOrderResponse.setCurrency(currency);
			createOrderResponse.setEntity(entity);
			createOrderResponse.setId(id);
			createOrderResponse.setNotes(notes1);
			createOrderResponse.setOfferId(offerId);
			createOrderResponse.setReceipt(receipt);
			createOrderResponse.setStatus(status);
			orderResponse.setOrderResponse(createOrderResponse);
		} catch (Exception e) {
			throw new RazorpayException(e.getMessage());
		}

		return orderResponse;

	}

	private BillResponseV2 billCall(OrderRequest orderRequest, StringBuilder uri) throws RazorpayException {

		uri.append("?tenantId=").append(orderRequest.getRequestInfo().getUserInfo().getTenantId());
		uri.append("&billNumber=").append(orderRequest.getCreateOrder().getBillNo());
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(orderRequest.getRequestInfo())
				.build();
		try {
			LinkedHashMap responseMap = (LinkedHashMap) restCallRepository.fetchResult(uri, requestInfoWrapper);
//			parseResponse(responseMap,dobFormat);
			BillResponseV2 responseObject = objectMapper.convertValue(responseMap, BillResponseV2.class);
			return responseObject;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("IllegalArgumentException : ObjectMapper not able to convertValue in billCall.");
		}
	}

}
