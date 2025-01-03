package org.upyog.mapper;

import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class AdvertisementDetailsMapper implements CommonDetailsMapper {

	@Override
	public String getModuleName() {
		return ADVERTISEMENT_MODULE_NAME;
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		// Access the first element of the bookingApplication array
		JsonNode bookingApplication = json.path(ADVERTISEMENT_APPLICATIONS).isArray()
				&& json.path(ADVERTISEMENT_APPLICATIONS).size() > 0 ? json.path(ADVERTISEMENT_APPLICATIONS).get(0) : null;

		if (bookingApplication == null) {
			return CommonDetails.builder().applicationNumber(NA).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA)
					.status(NA).build();
		}

		
		// Extract the application number and status
		String applicationNumber = bookingApplication.path("bookingNo").asText(NA);
		String status = bookingApplication.path("bookingStatus").asText(NA);
		
		String moduleName = "Advertisement";
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
		
		// Extract cartDetails for fromDate, toDate, and location
		String fromDate = NA;
		String toDate = NA;
		String location = NA;
		JsonNode cartDetails = bookingApplication.path("cartDetails");

		if (cartDetails.isArray() && cartDetails.size() > 0) {
			fromDate = cartDetails.get(0).path("bookingDate").asText(NA);
			toDate = cartDetails.get(cartDetails.size() - 1).path("bookingDate").asText(NA);
			location = cartDetails.get(0).path("location").asText(NA); // Map location to address
		}
		
		// Extract applicant details
        JsonNode applicantDetail = bookingApplication.path("applicantDetail");
        String ownerName = applicantDetail.path("applicantName").asText(NA);
        String ownerMobileNumber = CommonDetailUtil.maskMobileNumber(applicantDetail.path("applicantMobileNo").asText(NA));


		// Build and return the CommonDetails object
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(toDate).name(ownerName).mobileNumber(ownerMobileNumber)
				.address(location).moduleName(moduleName)
				.status(status).build();
	}

}
