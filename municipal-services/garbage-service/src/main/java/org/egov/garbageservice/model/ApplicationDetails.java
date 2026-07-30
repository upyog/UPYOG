package org.egov.garbageservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.Map;

/**
 * Summary of one garbage application for fee preview and pay flows.
 * Holds application number, total payable, and maps for bill and user display data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetails {
    @CustomSafeHtml
    private String applicationNumber;
    private Double totalPayableAmount;
    private Map<String, Object> billDetails;
    private Map<String, Object> userDetails;
}
