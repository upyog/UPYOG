package org.egov.ewst.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.egov.common.contract.response.ResponseInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a response containing business services in the Ewaste application.
 * This class contains the response information and a list of business services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BusinessServiceResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("BusinessServices")
	@Valid
	@NotNull
	private List<BusinessService> businessServices;

	/**
	 * Adds a business service to the list of business services in the response.
	 *
	 * @param businessServiceItem the business service to add
	 * @return the updated BusinessServiceResponse object
	 */
	public BusinessServiceResponse addBusinessServiceItem(BusinessService businessServiceItem) {
		if (this.businessServices == null) {
			this.businessServices = new ArrayList<>();
		}
		this.businessServices.add(businessServiceItem);
		return this;
	}

}
