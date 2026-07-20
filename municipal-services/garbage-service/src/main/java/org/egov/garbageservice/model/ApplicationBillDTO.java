package org.egov.garbageservice.model;
import org.egov.tracer.annotations.CustomSafeHtml;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Lightweight DTO for open-search pay preview listing applications with bill summary.
 * Exposes applicant contact, collection units, bill id/status, and computed total amount.
 */
@Data
public class ApplicationBillDTO {

    @CustomSafeHtml
    private String applicationNo;
    @CustomSafeHtml
    private String billId;
    @CustomSafeHtml
    private String status;
    @CustomSafeHtml
    private String consumerCode;
    private List<GrbgCollectionUnit> grbgCollectionUnits;

    @CustomSafeHtml
    private String name;
    @CustomSafeHtml
    private String mobileNumber;
    @CustomSafeHtml
    private String email;
    @CustomSafeHtml
    private String address;
    @CustomSafeHtml
    private String additionalDetails;
    @CustomSafeHtml
    private String formula;

    private Double totalAmount;
}
