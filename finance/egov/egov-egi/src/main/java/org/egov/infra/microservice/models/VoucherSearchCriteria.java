package org.egov.infra.microservice.models;

import java.util.Set;

import org.egov.infra.validation.SanitizeHtml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherSearchCriteria {
    private Set<Long> ids;
    @SanitizeHtml
    private String sortBy;
    private Integer pageSize;
    private Integer offset;
    private Set<String> voucherNumbers;
    private Long voucherFromDate;
    private Long voucherToDate;
    @SanitizeHtml
    private String voucherType;
    @SanitizeHtml
    private String voucherName;
    @SanitizeHtml
    private String fundId;
    @SanitizeHtml
    private String deptCode;
}
