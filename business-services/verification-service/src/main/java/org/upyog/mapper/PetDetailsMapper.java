package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PetDetailsMapper implements CommonDetailsMapper {

	@Override
	public String getModuleName() {
		return "pet";
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode petDetailNode = json.path("PetRegistrationApplications").isArray() && json.path("PetRegistrationApplications").size() > 0
				? json.path("PetRegistrationApplications").get(0)
				: null;

		if (petDetailNode == null) {
			return CommonDetails.builder().fromDate("NA").toDate("NA").address("").status("").applicationNumber("")
					.build();
		}
		
		long validityDate = petDetailNode.path("validityDate").asLong(0L); // No approval Date, from and to date, need to ask, for now i have change from approval date to validitydate
		String validFromString = "NA";
		String validToString = "NA";
		String status = petDetailNode.path("status").asText("");
		String applicationNumber = petDetailNode.path("applicationNumber").asText("");
		String moduleName = "pet-services";
		if (!"Approved".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate("N/A").toDate("N/A")
					.address("N/A").status("Pending").moduleName(moduleName).build();
		}
		if (validityDate != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validityDate), "dd-MM-yyyy");
			validToString = CommonDetailUtil.addOneYearToEpoch(String.valueOf(validityDate)); // Add one year
		}
		
		// Handle address concatenation
		JsonNode addressDetails = petDetailNode.path("address");
		StringBuilder fullAddress = new StringBuilder();
		if (!addressDetails.isMissingNode() && addressDetails.isObject()) {
		    fullAddress.append(addressDetails.path("doorNo").asText("")).append(", ")
		               .append(addressDetails.path("addressLine1").asText("")).append(", ")
		               .append(addressDetails.path("addressLine2").asText("")).append(", ")
		               .append(addressDetails.path("landmark").asText("")).append(", ")
		               .append(addressDetails.path("city").asText("")).append(", ")
		               .append(addressDetails.path("pincode").asText(""));
		}

		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString)
				.toDate(validToString).address(fullAddress.toString().replaceAll(",\\s*$", "")).moduleName(moduleName)
				.status(status).build();
	}

}
