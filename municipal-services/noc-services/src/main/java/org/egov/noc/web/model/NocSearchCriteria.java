package org.egov.noc.web.model;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.tracer.annotations.CustomSafeHtml;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NocSearchCriteria {

    @NotNull
    @CustomSafeHtml
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("ids")
    private List<String> ids;

    @CustomSafeHtml
    @JsonProperty("applicationNo")
    private String applicationNo;

    @CustomSafeHtml
    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @CustomSafeHtml
    @JsonProperty("nocNo")
    private String nocNo;

    @CustomSafeHtml
    @JsonProperty("source")
    private String source;

    @CustomSafeHtml
    @JsonProperty("nocType")
    private String nocType;

    @CustomSafeHtml
    @JsonProperty("sourceRefId")
    private String sourceRefId;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonIgnore
    private List<String> ownerIds;

    @JsonProperty("accountId")
    private List<String> accountId;
    
    @JsonProperty("status")
    private List<String> status;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    public boolean isEmpty() {
        return (this.tenantId == null && this.ids == null && this.applicationNo == null
                && this.nocNo == null && this.accountId == null && this.status == null);
    }

    public boolean tenantIdOnly() {
        return (this.tenantId == null && this.ids == null && this.applicationNo == null
                && this.nocNo == null && this.accountId == null && this.status == null);
    }
}
