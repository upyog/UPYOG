package org.upyog.mapper;

import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class StreetVendingDetailsMapper implements CommonDetailsMapper {

	@Override
	public String getModuleName() {
		return "streetvending";
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode svDetailNode = json.path("SVDetail").isArray() && json.path("SVDetail").size() > 0
				? json.path("SVDetail").get(0)
				: null;

		if (svDetailNode == null) {
			return CommonDetails.builder().fromDate("NA").toDate("NA").address("").status("").applicationNumber("")
					.build();
		}

		long approvalDate = svDetailNode.path("approvalDate").asLong(0L); 
		String validFromString = "NA";
		String validToString = "NA";
		String status = svDetailNode.path("applicationStatus").asText("");
		String applicationNumber = svDetailNode.path("applicationNo").asText("");
		String moduleName = "Street-Vending";
		if (!"REGISTRATIONCOMPLETED".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate("N/A").toDate("N/A")
					.address("N/A").status("Pending").moduleName(moduleName).build();
		}
		if (approvalDate != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(approvalDate), "dd-MM-yyyy");
			validToString = CommonDetailUtil.addOneYearToEpoch(String.valueOf(approvalDate)); // Add one year
		}

		// Handle address concatenation
		JsonNode addressDetails = svDetailNode.path("addressDetails");
		StringBuilder fullAddress = new StringBuilder();
		if (addressDetails.isArray() && addressDetails.size() > 0) {
			JsonNode addressNode = addressDetails.get(0);
			fullAddress.append(addressNode.path("houseNo").asText("")).append(", ")
					.append(addressNode.path("addressLine1").asText("")).append(", ")
					.append(addressNode.path("locality").asText("")).append(", ")
					.append(addressNode.path("city").asText(""));
		}

		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString)
				.toDate(validToString).address(fullAddress.toString().replaceAll(",\\s*$", "")).moduleName(moduleName)
				.status(status).build();
	}

}
