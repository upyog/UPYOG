package org.egov.demand.web.contract;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Compact DTO representing a bill summary. Contains only basic properties
 * like identifiers, amount, service category, date, and consumer code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortBillV2 {

    /**
     * Unique identifier of the bill.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Total amount outstanding or paid on the bill.
     */
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    /**
     * Name of the municipal service module that generated the bill.
     */
    @JsonProperty("businessService")
    private String businessService;

    /**
     * User-facing bill number.
     */
    @JsonProperty("billNumber")
    private String billNumber;

    /**
     * Epoch timestamp representing the date the bill was generated.
     */
    @JsonProperty("billDate")
    private Long billDate;

    /**
     * Unique business identifier (e.g. application or consumer number) connected to the bill.
     */
    @JsonProperty("consumerCode")
    private String consumerCode;

    /**
     * Epoch timestamp representing the due date of the bill.
     */
    @JsonProperty("dueDate")
    private Long dueDate;
}
