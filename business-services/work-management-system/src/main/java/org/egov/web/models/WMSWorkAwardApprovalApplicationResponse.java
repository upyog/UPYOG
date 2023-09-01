package org.egov.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
* Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update
*/
@ApiModel(description = "Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-10-25T21:43:19.662+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WMSWorkAwardApprovalApplicationResponse {
	
	@JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;

    @JsonProperty("WMSWorkAwardApprovalApplications")
    @Valid
    private List<WMSWorkAwardApprovalApplication> wmsWorkAwardApprovalApplications = null;


    public WMSWorkAwardApprovalApplicationResponse addWMSWorkAwardApprovalApplicationsItem(WMSWorkAwardApprovalApplication wmsWorkAwardApprovalApplicationsItem) {
        if (this.wmsWorkAwardApprovalApplications == null) {
            this.wmsWorkAwardApprovalApplications = new ArrayList<>();
        }
        this.wmsWorkAwardApprovalApplications.add(wmsWorkAwardApprovalApplicationsItem);
        return this;
    }

}
