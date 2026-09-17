package org.egov.infra.microservice.models;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.egov.infra.validation.SanitizeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemittanceSearcCriteria {
    private List<String> ids;
    private List<String> referenceNumbers;
    private Long fromDate;
    private Long toDate;
    @SanitizeHtml
    private String voucherHeader;
    @SanitizeHtml
    private String function;
    @SanitizeHtml
    private String fund;
    @SanitizeHtml
    private String remarks;
    @SanitizeHtml
    private String reasonForDelay;
    @SanitizeHtml
    private String status;
    @SanitizeHtml
    private String bankaccount;
    @SanitizeHtml
    @NotNull
    private String tenantId;
    @SanitizeHtml
    private String sortBy;
    @SanitizeHtml
    private String sortOrder;
    private Integer pageSize;
    private Integer limit;
    private Integer offset;
}
