package digit.bmc.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public class BmcRegistrationResponse {
	
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo = null;

	@JsonProperty("BmcRegistrationApplication")
	@Valid
	private List<BmcRegistrationApplication> bmcRegistrationApplications = null;

	public BmcRegistrationResponse addBmcRegistrationApplicationsItem(
			BmcRegistrationApplication bmcRegistrationApplicationsItem) {
		if (this.bmcRegistrationApplications == null) {
			this.bmcRegistrationApplications = new ArrayList<>();
		}
		this.bmcRegistrationApplications.add(bmcRegistrationApplicationsItem);
		return this;
	}


}
