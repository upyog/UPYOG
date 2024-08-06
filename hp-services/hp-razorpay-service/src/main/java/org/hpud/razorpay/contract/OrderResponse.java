package org.hpud.razorpay.contract;

import org.hpud.razorpay.model.CreateOrderResponse;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.razorpay.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class OrderResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("orderResponse")
	private CreateOrderResponse orderResponse;

	private String msg;
	private String status;
	private Order order;

}
