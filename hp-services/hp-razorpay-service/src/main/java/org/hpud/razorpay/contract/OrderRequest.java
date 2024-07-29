package org.hpud.razorpay.contract;

import org.hpud.razorpay.model.CreateOrder;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {


	@JsonProperty("orders")
	private CreateOrder createOrder;

}
