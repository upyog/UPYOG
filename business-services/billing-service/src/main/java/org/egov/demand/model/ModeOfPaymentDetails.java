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
	
	private BigDecimal pastAmount;
	
	@JsonProperty("status")
	private TxnStatusEnum status;
	
	@JsonProperty("fromPeriod")
	private Long fromPeriod;

	@JsonProperty("toPeriod")
	private Long toPeriod;
	
	
	@JsonProperty("period")
	private TxnPeriodEnum period;

	
	
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
	 
	 
	 public enum TxnPeriodEnum {
		 	QUARTER_1("QUARTER_1"),
			QUARTER_2("QUARTER_2"),
			QUARTER_3("QUARTER_3"),
			QUARTER_4("QUARTER_4"),
		 	
		 HALF_YEAR_1("HALF_YEAR_1"),
		 HALF_YEAR_2("HALF_YEAR_2"),
		 YEARLY("YEARLY");

	        private String value;

	        TxnPeriodEnum(String value) {
	            this.value = value;
	        }

	        @JsonCreator
	        public static TxnPeriodEnum fromValue(String text) {
	            for (TxnPeriodEnum b : TxnPeriodEnum.values()) {
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
