package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class StreetVendingDetailsMapper implements CommonDetailsMapper {

	@Override
	public String getModuleName() {
		return STREET_VENDING_MODULE_NAME;
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode svDetailNode = json.path(STREET_VENDING_APPLICATIONS).isArray() && json.path(STREET_VENDING_APPLICATIONS).size() > 0
				? json.path(STREET_VENDING_APPLICATIONS).get(0)
				: null;

		if (svDetailNode == null) {
			return CommonDetails.builder().fromDate(NA).toDate(NA).address(EMPTY_STRING).name(EMPTY_STRING).mobileNumber(EMPTY_STRING).status(EMPTY_STRING).applicationNumber(EMPTY_STRING)
					.build();
		}

		long approvalDate = svDetailNode.path("approvalDate").asLong(0L); 
		String validFromString = NA;
		String validToString = NA;
		String status = svDetailNode.path("applicationStatus").asText(EMPTY_STRING);
		String applicationNumber = svDetailNode.path("applicationNo").asText(EMPTY_STRING);
		String moduleName = "Street-Vending";
		String ownerName = NA;
		String ownerMobileNumber = NA;
		
		// Fetch vendor details
        JsonNode vendorDetails = svDetailNode.path("vendorDetail");
        if (vendorDetails.isArray() && vendorDetails.size() > 0) {
            JsonNode vendorNode = vendorDetails.get(0);
            ownerName = vendorNode.path("name").asText(NA);
            ownerMobileNumber = CommonDetailUtil.maskMobileNumber(vendorNode.path("mobileNo").asText(NA));
        }
			
		if (!"REGISTRATIONCOMPLETED".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA)
					.address(NA).name(NA).mobileNumber(NA).status("Pending").moduleName(moduleName).build();
		}
		if (approvalDate != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(approvalDate), DATE_FORMAT);
			validToString = CommonDetailUtil.addOneYearToEpoch(String.valueOf(approvalDate)); // Add one year
		}

		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString).name(ownerName).mobileNumber(ownerMobileNumber)
				.toDate(validToString).address(NA).moduleName(moduleName)
				.status(status).build();
	}

}
