package org.upyog.mapper;

import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class AdvertisementDetailsMapper implements CommonDetailsMapper {

	@Override
	public String getModuleName() {
		return "advertisement";
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		// Access the first element of the bookingApplication array
		JsonNode bookingApplication = json.path("bookingApplication").isArray()
				&& json.path("bookingApplication").size() > 0 ? json.path("bookingApplication").get(0) : null;

		if (bookingApplication == null) {
			return CommonDetails.builder().applicationNumber("N/A").fromDate("N/A").toDate("N/A").address("N/A").name("N/A").mobileNumber("N/A")
					.status("N/A").build();
		}

		
		// Extract the application number and status
		String applicationNumber = bookingApplication.path("bookingNo").asText("N/A");
		String status = bookingApplication.path("bookingStatus").asText("N/A");
		
		String moduleName = "Advertisement";
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
		
		// Extract cartDetails for fromDate, toDate, and location
		String fromDate = "N/A";
		String toDate = "N/A";
		String location = "N/A";
		JsonNode cartDetails = bookingApplication.path("cartDetails");

		if (cartDetails.isArray() && cartDetails.size() > 0) {
			fromDate = cartDetails.get(0).path("bookingDate").asText("N/A");
			toDate = cartDetails.get(cartDetails.size() - 1).path("bookingDate").asText("N/A");
			location = cartDetails.get(0).path("location").asText("N/A"); // Map location to address
		}
		
		// Extract applicant details
        JsonNode applicantDetail = bookingApplication.path("applicantDetail");
        String ownerName = applicantDetail.path("applicantName").asText("N/A");
        String ownerMobileNumber = CommonDetailUtil.maskMobileNumber(applicantDetail.path("applicantMobileNo").asText("N/A"));


		// Build and return the CommonDetails object
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(toDate).name(ownerName).mobileNumber(ownerMobileNumber)
				.address(location).moduleName(moduleName)
				.status(status).build();
	}

}
