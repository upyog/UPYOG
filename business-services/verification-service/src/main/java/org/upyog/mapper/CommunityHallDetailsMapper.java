package org.upyog.mapper;

import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import com.fasterxml.jackson.databind.JsonNode;
import static org.upyog.constants.VerificationSearchConstants.*;

@Component
public class CommunityHallDetailsMapper implements CommonDetailsMapper {

	@Override
    public String getModuleName() {
        return CHB_MODULE_NAME;
    }
	
	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		// Access the first element of the HallApplication array
		
		JsonNode HallApplication = json.path(COMMUNITY_HALL_APPLICATIONS).isArray()
				&& json.path(COMMUNITY_HALL_APPLICATIONS).size() > 0 ? json.path(COMMUNITY_HALL_APPLICATIONS).get(0) : null;

		if (HallApplication == null) {
			return CommonDetails.builder().applicationNumber(NA).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA)
					.status(NA).build();
		}

		
		// Extract the application number and status
		String applicationNumber = HallApplication.path("bookingNo").asText(NA);
		String status = HallApplication.path("bookingStatus").asText(NA);
		String moduleName = "chb-services";
		if (!"BOOKED".equalsIgnoreCase(status)) {
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
		
		JsonNode bookingSlotDetails = HallApplication.path("bookingSlotDetails");

		if (bookingSlotDetails.isArray() && bookingSlotDetails.size() > 0) {
			fromDate = bookingSlotDetails.get(0).path("bookingDate").asText(NA);
			toDate = bookingSlotDetails.get(bookingSlotDetails.size() - 1).path("bookingDate").asText(NA);
			location = bookingSlotDetails.get(0).path("hallCode").asText(NA); // Map location to address
		}
		
		// Extract name and mobile number from applicantDetail
        JsonNode applicantDetail = HallApplication.path("applicantDetail");
        String ownerName = applicantDetail.path("applicantName").asText(NA);
        String ownerMobileNumber = CommonDetailUtil.maskMobileNumber(applicantDetail.path("applicantMobileNo").asText(NA));


		// Build and return the CommonDetails object
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(toDate).name(ownerName).mobileNumber(ownerMobileNumber)
				.address(location).moduleName(moduleName)
				.status(status).build();
	}

}
