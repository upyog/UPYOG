package org.egov.pg.models;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class RefundDump {

	@JsonProperty("refundId")
    private String refundId;

    @JsonProperty("refundRequest")
    private String refundRequest;

    @JsonProperty("refundResponse")
    private Object refundResponse;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
