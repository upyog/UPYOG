package org.upyog.wms.web.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search parameters (bound from the query string). A state-level tenantId (no '.') searches all ULBs of
 * the state. Lists accept comma separated values, e.g. applicationNumbers=A,B.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlanSearchCriteria {

    @NotBlank
    @Size(max = 64)
    private String tenantId;

    private List<String> ids;

    private List<String> applicationNumbers;

    private List<String> actionPlanNumbers;

    private List<String> projectIds;

    private List<String> statuses;

    @Size(max = 16)
    private String financialYear;

    @Size(max = 64)
    private String schemeCode;

    @Size(max = 32)
    private String fundSource;

    @Size(max = 64)
    private String districtCode;

    @Size(max = 64)
    private String createdBy;

    /** createdTime range, epoch millis. */
    private Long fromDate;

    private Long toDate;

    /** true = only the published (latest) version of every plan. */
    private Boolean latestVersionOnly;

    /** createdTime | approvedDate | projectId | applicationNumber | actionPlanNumber */
    private String sortBy;

    /** ASC | DESC */
    private String sortOrder;

    @Min(0)
    private Integer offset;

    @Min(1)
    @Max(500)
    private Integer limit;
}
