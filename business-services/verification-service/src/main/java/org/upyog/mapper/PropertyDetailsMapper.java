package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PropertyDetailsMapper implements CommonDetailsMapper {
	
	@Override
	public String getModuleName() {
		return PROPERTY_MODULE_NAME;
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode propertyDetailNode = json.path(PROPERTY_APPLICATIONS).isArray() && json.path(PROPERTY_APPLICATIONS).size() > 0
				? json.path(PROPERTY_APPLICATIONS).get(0)
				: null;

		if (propertyDetailNode == null) {
			return CommonDetails.builder().fromDate(NA).toDate(NA).address(EMPTY_STRING).status(EMPTY_STRING).applicationNumber(EMPTY_STRING).name(EMPTY_STRING).mobileNumber(EMPTY_STRING)
					.build();
		}
		
		long validityDate = propertyDetailNode.path("owners").get(0).path("createdDate").asLong(0L);
		String validFromString = NA;
		String validToString = NA;
		String status = propertyDetailNode.path("status").asText(EMPTY_STRING);
		String applicationNumber = propertyDetailNode.path("propertyId").asText(EMPTY_STRING);
		String moduleName = "PT";
		
		// Extracting name and mobile number from the owner details
        JsonNode ownerDetails = propertyDetailNode.path("owners").get(0);
        String ownerName = ownerDetails.path("name").asText(NA);
        String ownerMobileNumber = CommonDetailUtil.maskMobileNumber(ownerDetails.path("mobileNumber").asText(NA));
        
		if (!"ACTIVE".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA).name(NA).mobileNumber(NA)
					.address(NA).status("Pending").moduleName(moduleName).build();
		}
		if (validityDate != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validityDate), DATE_FORMAT);
			validToString = CommonDetailUtil.addOneYearToEpoch(String.valueOf(validityDate)); // Add one year
		}
	
		
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString).name(ownerName).mobileNumber(ownerMobileNumber)
				.toDate(validToString).address(NA).moduleName(moduleName)
				.status(status).build();
	}
	
	
	
	

}
