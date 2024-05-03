package org.egov.swcalculation.web.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillScheduler {

	@JsonProperty("id")
	private String id = null;

	private String transactionType;

	@NotNull
	private String locality;

	@NotNull
	private long billingcycleStartdate;

	@NotNull
	private long billingcycleEnddate;

	private BillStatus status;

	private AuditDetails auditDetails;

	@NotNull
	private String tenantId;
	
	/**
	 * status of the bill .
	 */
	public enum StatusEnum {

		INITIATED("INITIATED"),

		INPROGRESS("INPROGRESS"),

		COMPLETED("COMPLETED");

		private String value;

		StatusEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static StatusEnum fromValue(String text) {
			for (StatusEnum b : StatusEnum.values()) {
				if (String.valueOf(b.value).equalsIgnoreCase(text)) {
					return b;
				}
			}
			return null;
		}
	}
}
