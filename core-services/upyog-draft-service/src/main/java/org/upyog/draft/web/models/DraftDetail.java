package org.upyog.draft.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftDetail {

    private String draftId;
    private String tenantId;
    private String businessService;
    private String moduleName;
    private String moduleEntityId;
    private String creatorType;
    private Object draftData;
    private BigDecimal completionPct;
    private String status;
    private String createdBy;
    private String lastModifiedBy;
    private Long createdTime;
    private Long lastModifiedTime;

    @JsonProperty("id")
    public String getId() {
        return draftId;
    }

    @JsonProperty("applicationNumber")
    public String getApplicationNumber() {
        return draftId;
    }

    @JsonProperty("completionPercentage")
    public Double getCompletionPercentage() {
        return completionPct != null ? completionPct.doubleValue() : 0.0;
    }

    @JsonProperty("lastModifiedTime")
    public Long getLastModifiedTimeAlias() {
        return lastModifiedTime;
    }
}
