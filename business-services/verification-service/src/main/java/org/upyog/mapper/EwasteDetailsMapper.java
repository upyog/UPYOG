package org.upyog.mapper;

import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class EwasteDetailsMapper implements CommonDetailsMapper {

	@Override
    public String getModuleName() {
        return "ewaste";
    }
	
	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		// Access the first element of the EwasteApplication array
		
		JsonNode EwasteApplication = json.path("EwasteApplication").isArray()
				&& json.path("EwasteApplication").size() > 0 ? json.path("EwasteApplication").get(0) : null;

		if (EwasteApplication == null) {
			return CommonDetails.builder().applicationNumber("N/A").fromDate("N/A").toDate("N/A").address("N/A").name("N/A").mobileNumber("N/A")
					.status("N/A").build();
		}

		
		// Extract the application number and status
		String applicationNumber = EwasteApplication.path("requestId").asText("N/A");
		String status = EwasteApplication.path("requestStatus").asText("N/A");
		String moduleName = "ew-services";
		if (!"REQUESTCOMPLETED".equalsIgnoreCase(status)) {
	        // If not BOOKED, set status as Pending and other details as N/A
	        return CommonDetails.builder()
	                .applicationNumber(applicationNumber)
	                .fromDate("N/A")
	                .toDate("N/A")
	                .address("N/A")
	                .name("N/A")
	                .mobileNumber("N/A")
	                .status("Pending")
	                .moduleName(moduleName)
	                .build();
	    }
		
		String fromDate = "N/A";
		String toDate = "N/A";
		String location = "N/A";
		String ownerName = EwasteApplication.path("applicant").path("applicantName").asText("N/A");
		String ownerMobileNumber = CommonDetailUtil.maskMobileNumber(EwasteApplication.path("applicant").path("mobileNumber").asText("N/A"));


		// Build and return the CommonDetails object
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(toDate).name(ownerName).mobileNumber(ownerMobileNumber)
				.address(location).moduleName(moduleName)
				.status(status).build();
	}
}
