package org.egov.demand.model;

import java.math.BigDecimal;

import org.egov.demand.model.Transaction.TxnStatusEnum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModeOfPaymentDetails {
	
	@JsonProperty("amount")
	private BigDecimal amount;
	
	@JsonProperty("status")
	private TxnStatusEnum status;
	
	@JsonProperty("fromPeriod")
	private Long fromPeriod;

	@JsonProperty("toPeriod")
	private Long toPeriod;
	
	
	 public enum TxnStatusEnum {
		 	PAID("PAID"),
			PAYMENT_FAILED ("PAYMENT_FAILED"),
			PAYMENT_PENDING ("PAYMENT_PENDING");

	        private String value;

	        TxnStatusEnum(String value) {
	            this.value = value;
	        }

	        @JsonCreator
	        public static TxnStatusEnum fromValue(String text) {
	            for (TxnStatusEnum b : TxnStatusEnum.values()) {
	                if (String.valueOf(b.value).equals(text)) {
	                    return b;
	                }
	            }
	            return null;
	        }

	        @Override
	        @JsonValue
	        public String toString() {
	            return String.valueOf(value);
	        }
	    }
}
