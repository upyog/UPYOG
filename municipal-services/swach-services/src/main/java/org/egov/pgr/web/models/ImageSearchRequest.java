package org.egov.pgr.web.models;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.egov.pgr.web.models.RequestSearchCriteria.SortBy;
import org.egov.pgr.web.models.RequestSearchCriteria.SortOrder;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageSearchRequest {
	 @SafeHtml
	    @JsonProperty("tenantId")
	    private String tenantId;

	    @JsonProperty("tenantIds")
	    private Set<String> tenantIds;

	    @JsonProperty("lat")
	    private Set<String> latitude;

	    @JsonProperty("long")
	    private Set<String> logitude;



	    @SafeHtml
	    @JsonProperty("serviceRequestId")
	    private String serviceRequestId;

	    @JsonProperty("sortBy")
	    private SortBy sortBy;

	    @JsonProperty("sortOrder")
	    private SortOrder sortOrder;
	  
	    @JsonProperty("locality")
	    private Set<String> locality;

	    @JsonProperty("ids")
	    private Set<String> ids;

	    @JsonProperty("fromDate")
	    private Long fromDate;

	    @JsonProperty("toDate")
	    private Long toDate;

	    @JsonProperty("slaDeltaMaxLimit")
	    private Long slaDeltaMaxLimit;

	    @JsonProperty("slaDeltaMinLimit")
	    private Long slaDeltaMinLimit;

	    @JsonProperty("limit")
	    private Integer limit;

	    @JsonProperty("offset")
	    private Integer offset;

	    @JsonIgnore
	    private Set<String> userIds;

	    @JsonIgnore
	    private Boolean isPlainSearch;


	    public enum SortOrder {
	        ASC,
	        DESC
	    }

	    public enum SortBy {
	        locality,
	        applicationStatus,
	        serviceRequestId
	    }

	    @SafeHtml
	    @JsonProperty("accountId")
	    private String accountId;

	    public boolean isEmpty(){
	        return (this.tenantId==null && this.serviceRequestId==null
	       && this.ids==null && this.userIds==null && this.locality==null);
	    }
	
	
}
