package org.egov.notice.web.model.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.egov.notice.web.model.workflow.ProcessInstanceSearchCriteria;

import java.util.HashMap;


@Data
public class InboxElasticSearchCriteria {

    @NotNull
    @JsonProperty("indexKey")
    private String indexKey;

    @NotNull
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("fromDate")
    private long fromDate;

    @JsonProperty("toDate")
    private long toDate;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    @Max(value = 300)
    private Integer limit;

    @JsonProperty("sortOrder")
    private String sortOrder;

}
