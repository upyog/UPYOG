package org.hpud.razorpay.web.controller;

import org.hpud.razorpay.contract.OrderRequest;
import org.hpud.razorpay.contract.OrderResponse;
import org.hpud.razorpay.contract.RequestInfoWrapper;
import org.hpud.razorpay.contract.ResponseInfo;
import org.hpud.razorpay.exception.RazorpayException;
import org.hpud.razorpay.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@RestController
@RequestMapping("/razorpay")
public class RazorpayController {

	@Autowired
	RazorpayService razorpayService;

	@Autowired
	ResponseInfo responseInfo;

	@PostMapping("/_createOrder")
	public ResponseEntity<?> createOrderforRazorpay(@RequestBody OrderRequest orderRequest, @RequestBody RequestInfoWrapper requestInfoWrapper) throws RazorpayException {
		OrderResponse orderResponse = new OrderResponse();
		try {
			orderResponse = razorpayService.createOrderForRazorpay(orderRequest, orderResponse,requestInfoWrapper);
			orderResponse.setStatus("SUCCESS");
			orderResponse.setMsg("Order Response Fetched Successfully!!!");

		} catch (Exception e) {
			e.printStackTrace();
			orderResponse.setStatus("ERROR");
			orderResponse.setMsg(e.getMessage());
			return new ResponseEntity<>(orderResponse, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(orderResponse, HttpStatus.OK);
	}

}
