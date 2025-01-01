package org.upyog.mapper;

import org.upyog.repository.ServiceRequestRepository;
import org.upyog.web.models.CommonDetails;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;


@Component
public class FireNocDetailsMapper implements CommonDetailsMapper {

	@Override
    public String getModuleName() {
        return "firenoc";
    }
	
	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode fireNocDetail = json.path("FireNOCs").isArray()
				&& json.path("FireNOCs").size() > 0 ? json.path("FireNOCs").get(0) : null;

		if (fireNocDetail == null) {
			return CommonDetails.builder().applicationNumber("N/A").fromDate("N/A").toDate("N/A").address("N/A")
					.status("N/A").build();
		}

		
		// Extract the application number and status
		String applicationNumber = fireNocDetail.path("fireNOCDetails").path("applicationNumber").asText("N/A");
		String status = fireNocDetail.path("fireNOCDetails").path("status").asText("N/A");
		String moduleName = "firenoc";
		
		String fromDate = "N/A";
		String toDate = "N/A";
		String location = "N/A";


		// Build and return the CommonDetails object
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(toDate)
				.address(location).moduleName(moduleName)
				.status(status).build();
	}
}
