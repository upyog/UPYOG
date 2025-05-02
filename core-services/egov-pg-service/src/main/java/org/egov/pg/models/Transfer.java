package org.egov.pg.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transfer {

	private String account;

	private Integer amount;

	@Builder.Default
	private String currency = "INR";

	private Notes notes;

//	@JsonProperty("linked_account_notes")
//	private List<String> linkedAccountNotes;

	@JsonProperty("on_hold")
	@Builder.Default
	private Boolean onHold = false;

//	@JsonProperty("on_hold_until")
//	private Long onHoldUntil;

}
