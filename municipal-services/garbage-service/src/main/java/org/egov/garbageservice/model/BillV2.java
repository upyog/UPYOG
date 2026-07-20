package org.egov.garbageservice.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.egov.garbageservice.contract.bill.BillDetail;
import org.egov.tracer.annotations.CustomSafeHtml;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
/**
 * Simplified bill view used internally for PDF generation and bill v2 APIs.
 * Mirrors key billing fields and embeds contract BillDetail lines from the collection service.
 */
@AllArgsConstructor
public class BillV2 {

    @CustomSafeHtml
    private String id;               
    @CustomSafeHtml
    private String tenantId;
    @CustomSafeHtml
    private String consumerCode;
    @CustomSafeHtml
    private String payerName;
    @CustomSafeHtml
    private String payerAddress;
    @CustomSafeHtml
    private String payerEmail;
    @CustomSafeHtml
    private String mobileNumber;
    @CustomSafeHtml
    private String userId;
    @CustomSafeHtml
    private String fileStoreId;
    @CustomSafeHtml
    private String status;
    private AuditDetails auditDetails;
    private JsonNode additionalDetails;
    private List<BillDetail> billDetails;
    private Long expiryDate;
}
