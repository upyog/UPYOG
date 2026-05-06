package org.egov.noc.web.model;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NocSearchCriteria {

    @NotNull
    @SafeHtml
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("ids")
    private List<String> ids;

    @SafeHtml
    @JsonProperty("applicationNo")
    private String applicationNo;

    @SafeHtml
    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @SafeHtml
    @JsonProperty("nocNo")
    private String nocNo;

    @SafeHtml
    @JsonProperty("source")
    private String source;

    @SafeHtml
    @JsonProperty("nocType")
    private String nocType;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @SafeHtml
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

    @JsonProperty("createdBy")
    private String createdBy;

    @SafeHtml
//    @Size(min = 1, max = 15)
    @JsonProperty("vasikaNumber")
    private String vasikaNumber = null;
    
    @JsonProperty("vasikaDate")
    private String vasikaDate = null;
    
    public boolean isEmpty() {
        return (this.tenantId == null && this.ids == null && this.applicationNo == null
                && this.nocNo == null && this.accountId == null && this.status == null);
    }

    public boolean tenantIdOnly() {
        return (this.tenantId == null && this.ids == null && this.applicationNo == null
                && this.nocNo == null && this.accountId == null && this.status == null);
    }
}
