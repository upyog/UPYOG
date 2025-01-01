package org.egov.ptr.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Contract class to send response. Array of items are used in case of search
 * results or response for create, whereas single item is used for update
 */
@ApiModel(description = "Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-08-20T09:30:27.617+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetRegistrationResponse {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo = null;

	@JsonProperty("PetRegistrationApplications")
	@Valid
	private List<PetRegistrationApplication> petRegistrationApplications = null;

	public PetRegistrationResponse addPetRegistrationApplicationsItem(
			PetRegistrationApplication petRegistrationApplicationsItem) {
		if (this.petRegistrationApplications == null) {
			this.petRegistrationApplications = new ArrayList<>();
		}
		this.petRegistrationApplications.add(petRegistrationApplicationsItem);
		return this;
	}

}
