package org.egov.web.models;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contract class to receive request. Array of  items are used in case of create, whereas single  item is used for update
 */
@ApiModel(description = "Contract class to receive request. Array of  items are used in case of create, whereas single  item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-10-25T21:43:19.662+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class WMSTenderCategoryRequest {
	
	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;

    @JsonProperty("WMSTenderCategoryApplication")
    @Valid
    private List<WMSTenderCategoryApplication> wmsTenderCategoryApplications = null;


    public WMSTenderCategoryRequest addTenderCategoryApplicationsItem(WMSTenderCategoryApplication wmsTenderCategoryApplicationsItem) {
        if (this.wmsTenderCategoryApplications == null) {
            this.wmsTenderCategoryApplications = new ArrayList<>();
        }
        this.wmsTenderCategoryApplications.add(wmsTenderCategoryApplicationsItem);
        return this;
        
    }

}
