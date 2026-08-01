package org.upyog.draft.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftSearchCriteria {

    private String tenantId;
    private String userUuid;
    private String businessService;
    private String status;
    private Integer offset;
    private Integer limit;
    private String sortBy;
    private String sortOrder;
}
