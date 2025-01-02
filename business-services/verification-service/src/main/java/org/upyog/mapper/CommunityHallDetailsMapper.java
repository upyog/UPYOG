package org.upyog.mapper;

import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class CommunityHallDetailsMapper implements CommonDetailsMapper {

	@Override
    public String getModuleName() {
        return "communityhall";
    }
	
	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		// Access the first element of the HallApplication array
		
		JsonNode HallApplication = json.path("hallsBookingApplication").isArray()
				&& json.path("hallsBookingApplication").size() > 0 ? json.path("hallsBookingApplication").get(0) : null;

		if (HallApplication == null) {
			return CommonDetails.builder().applicationNumber("N/A").fromDate("N/A").toDate("N/A").address("N/A").name("N/A").mobileNumber("N/A")
					.status("N/A").build();
		}

		
		// Extract the application number and status
		String applicationNumber = HallApplication.path("bookingNo").asText("N/A");
		String status = HallApplication.path("bookingStatus").asText("N/A");
		String moduleName = "chb-services";
		if (!"BOOKED".equalsIgnoreCase(status)) {
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
		
		JsonNode bookingSlotDetails = HallApplication.path("bookingSlotDetails");

		if (bookingSlotDetails.isArray() && bookingSlotDetails.size() > 0) {
			fromDate = bookingSlotDetails.get(0).path("bookingDate").asText("N/A");
			toDate = bookingSlotDetails.get(bookingSlotDetails.size() - 1).path("bookingDate").asText("N/A");
			location = bookingSlotDetails.get(0).path("hallCode").asText("N/A"); // Map location to address
		}
		
		// Extract name and mobile number from applicantDetail
        JsonNode applicantDetail = HallApplication.path("applicantDetail");
        String ownerName = applicantDetail.path("applicantName").asText("N/A");
        String ownerMobileNumber = CommonDetailUtil.maskMobileNumber(applicantDetail.path("applicantMobileNo").asText("N/A"));


		// Build and return the CommonDetails object
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(toDate).name(ownerName).mobileNumber(ownerMobileNumber)
				.address(location).moduleName(moduleName)
				.status(status).build();
	}

}
