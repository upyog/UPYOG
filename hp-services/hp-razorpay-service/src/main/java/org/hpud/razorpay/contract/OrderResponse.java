package org.hpud.razorpay.contract;



import org.hpud.razorpay.model.CreateOrderResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

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

public class OrderResponse {
	
	  @JsonProperty("ResponseInfo")
      private ResponseInfo responseInfo;
	  
	  @JsonProperty("orderResponse")
		private CreateOrderResponse orderResponse;
	  private String msg;
	  private String status;

}
