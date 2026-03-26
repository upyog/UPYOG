package org.egov.pg.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Refund {

	private String id;
	
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("refundId")
	private String refundId;

	@JsonProperty("originalTxnId")
	private String originalTxnId;

	@JsonProperty("serviceCode")
	private String serviceCode;

	@JsonProperty("originalAmount")
	private String originalAmount;

	@JsonProperty("refundAmount")
	private String refundAmount;

	@JsonProperty("gatewayTxnId")
	private String gatewayTxnId;

    @JsonProperty("gatewayStatusCode")
    private String gatewayStatusCode;

    @JsonProperty("gatewayStatusMsg")
    private String gatewayStatusMsg;

	@JsonProperty("gateway")
	@NotNull
	@Size(min = 2)
	private String gateway;
	
	@JsonProperty("consumerCode")
    @Size(min = 1, max = 128)
    private String consumerCode;

	@JsonProperty("refundTxnStatus")
	private RefundStatusEnum status;
	
	@JsonProperty("atomTxnId")
	private String atomTxnId;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

	/**
	 * Current status of the refund
	 */
	public enum RefundStatusEnum {
		SUCCESS("SUCCESS"),

		FAILURE("FAILURE"),

		INITIATED("INITIATED"),
		
		PENDING("PENDING"),
		
	    PROCESSING("PROCESSING");

		private String value;

		RefundStatusEnum(String value) {
			this.value = value;
		}

		@JsonCreator
		public static RefundStatusEnum fromValue(String text) {
			for (RefundStatusEnum b : RefundStatusEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			throw new IllegalArgumentException("Invalid RefundStatusEnum: " + text);
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}
	}

}
