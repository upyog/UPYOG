package digit.bmc.model;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;

public class BmcRegistrationRequest {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("BmcRegistrationApplications")

	@Valid
	private List<BmcRegistrationApplication> bmcRegistrationApplications = null;

	public BmcRegistrationRequest addBmcRegistrationApplicationItem(
			BmcRegistrationApplication bmcRegistrationApplicationsItem) {
		if (this.bmcRegistrationApplications == null) {
			this.bmcRegistrationApplications = new ArrayList<>();
		}
		this.bmcRegistrationApplications.add(bmcRegistrationApplicationsItem);
		return this;
	}

}
