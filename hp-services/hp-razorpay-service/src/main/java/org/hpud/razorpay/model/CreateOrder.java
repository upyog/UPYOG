package org.hpud.razorpay.model;

import javax.validation.constraints.NotNull;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrder {
	@JsonProperty("billNo")
	@NotNull
	private String billNo;
	@JsonProperty("applicationNo")
	private String applicationNo;
	

}
