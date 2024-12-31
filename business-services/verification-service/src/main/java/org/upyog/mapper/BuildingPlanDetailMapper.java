package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class BuildingPlanDetailMapper implements CommonDetailsMapper {
	
	@Override
	public String getModuleName() {
		return "bpa";
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode bpaDetailNode = json.path("BPA").isArray() && json.path("BPA").size() > 0
				? json.path("BPA").get(0)
				: null;

		if (bpaDetailNode == null) {
			return CommonDetails.builder().fromDate("NA").toDate("NA").address("").status("").applicationNumber("")
					.build();
		}
		
		long approvalDate = bpaDetailNode.path("approvalDate").asLong(0L);
		String validFromString = "NA";
		String validToString = "NA";
		String status = bpaDetailNode.path("status").asText("");
		String applicationNumber = bpaDetailNode.path("applicationNo").asText("");
		String moduleName = "BPA";
		if (!"APPROVED".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate("N/A").toDate("N/A")
					.address("N/A").status("Pending").moduleName(moduleName).build();
		}
		if (approvalDate != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(approvalDate), "dd-MM-yyyy");
			validToString = CommonDetailUtil.addOneYearToEpoch(String.valueOf(approvalDate)); // Add one year
		}
		
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString)
				.toDate(validToString).address("N/A").moduleName(moduleName)
				.status(status).build();
	}


}
