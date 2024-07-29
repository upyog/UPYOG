package org.hpud.razorpay.contract;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RequestInfoWrapper {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

}
