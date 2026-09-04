package org.egov.receipt.consumer.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundKafkaRequest {

    @JsonProperty("RequestInfo")
    @JsonAlias("requestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("refund")
    @JsonAlias("Refund")
    private RefundKafkaDetail refund;
}