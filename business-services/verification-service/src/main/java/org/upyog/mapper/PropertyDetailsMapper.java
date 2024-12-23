package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PropertyDetailsMapper implements CommonDetailsMapper {
	
	@Override
	public String getModuleName() {
		return "property";
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode propertyDetailNode = json.path("Properties").isArray() && json.path("Properties").size() > 0
				? json.path("Properties").get(0)
				: null;

		if (propertyDetailNode == null) {
			return CommonDetails.builder().fromDate("NA").toDate("NA").address("").status("").applicationNumber("")
					.build();
		}
		
		long validityDate = propertyDetailNode.path("owners").get(0).path("createdDate").asLong(0L);
		String validFromString = "NA";
		String validToString = "NA";
		String status = propertyDetailNode.path("status").asText("");
		String applicationNumber = propertyDetailNode.path("propertyId").asText("");
		String moduleName = "PT";
		if (!"ACTIVE".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate("N/A").toDate("N/A")
					.address("N/A").status("Pending").moduleName(moduleName).build();
		}
		if (validityDate != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validityDate), "dd-MM-yyyy");
			validToString = CommonDetailUtil.addOneYearToEpoch(String.valueOf(validityDate)); // Add one year
		}
		
		// Handle address concatenation
				JsonNode addressDetails = propertyDetailNode.path("address");
				StringBuilder fullAddress = new StringBuilder();
				if (!addressDetails.isMissingNode() && addressDetails.isObject()) {
				    fullAddress.append(addressDetails.path("doorNo").asText("")).append(", ")
	               .append(addressDetails.path("street").asText("")).append(", ")
	               .append(addressDetails.path("city").asText("")).append(", ")
	               .append(addressDetails.path("pincode").asText(""));
				}
		
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString)
				.toDate(validToString).address(fullAddress.toString().replaceAll(",\\s*$", "")).moduleName(moduleName)
				.status(status).build();
	}
	
	
	
	

}
