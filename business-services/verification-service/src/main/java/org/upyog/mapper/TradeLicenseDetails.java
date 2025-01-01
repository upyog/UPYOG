package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class TradeLicenseDetails implements CommonDetailsMapper {
	
	@Override
	public String getModuleName() {
		return "tradelicense";
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode tradeDetailNode = json.path("Licenses").isArray() && json.path("Licenses").size() > 0
				? json.path("Licenses").get(0)
				: null;

		if (tradeDetailNode == null) {
			return CommonDetails.builder().fromDate("NA").toDate("NA").address("").status("").applicationNumber("")
					.build();
		}
		
		long validFrom = tradeDetailNode.path("validFrom").asLong(0L);    //valid to is also available so creating one more variable
		long validTo = tradeDetailNode.path("validTo").asLong(0L);
		String validFromString = "NA";
		String validToString = "NA";
		String status = tradeDetailNode.path("status").asText("");
		String applicationNumber = tradeDetailNode.path("licenseNumber").asText("");
		String moduleName = "BPA";
		if (!"APPROVED".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate("N/A").toDate("N/A")
					.address("N/A").status("Pending").moduleName(moduleName).build();
		}
		if (validFrom != 0L && validTo != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validFrom), "dd-MM-yyyy");
			validToString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validTo), "dd-MM-yyyy");
		}
		
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString)
				.toDate(validToString).address("N/A").moduleName(moduleName)
				.status(status).build();
	}

}
