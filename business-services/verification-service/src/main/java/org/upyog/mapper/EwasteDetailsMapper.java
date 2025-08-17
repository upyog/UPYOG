package org.upyog.mapper;

import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import com.fasterxml.jackson.databind.JsonNode;
import static org.upyog.constants.VerificationSearchConstants.*;

@Component
public class EwasteDetailsMapper implements CommonDetailsMapper {

	@Override
    public String getModuleName() {
        return EWASTE_MODULE_NAME;
    }
	
	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		// Access the first element of the EwasteApplication array
		
		JsonNode EwasteApplication = json.path(EWASTE_APPLICATIONS).isArray()
				&& json.path(EWASTE_APPLICATIONS).size() > 0 ? json.path(EWASTE_APPLICATIONS).get(0) : null;

		if (EwasteApplication == null) {
			return CommonDetails.builder().applicationNumber(NA).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA)
					.status(NA).build();
		}

		
		// Extract the application number and status
		String applicationNumber = EwasteApplication.path("requestId").asText(NA);
		String status = EwasteApplication.path("requestStatus").asText(NA);
		String moduleName = "ew-services";
		if (!"REQUESTCOMPLETED".equalsIgnoreCase(status)) {
	        // If not BOOKED, set status as Pending and other details as N/A
	        return CommonDetails.builder()
	                .applicationNumber(applicationNumber)
	                .fromDate(NA)
	                .toDate(NA)
	                .address(NA)
	                .name(NA)
	                .mobileNumber(NA)
	                .status("Pending")
	                .moduleName(moduleName)
	                .build();
	    }
		
		String fromDate = NA;
		String toDate = NA;
		String location = NA;
		String ownerName = EwasteApplication.path("applicant").path("applicantName").asText(NA);
		String ownerMobileNumber = CommonDetailUtil.maskMobileNumber(EwasteApplication.path("applicant").path("mobileNumber").asText(NA));


		// Build and return the CommonDetails object
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(toDate).name(ownerName).mobileNumber(ownerMobileNumber)
				.address(location).moduleName(moduleName)
				.status(status).build();
	}
}
