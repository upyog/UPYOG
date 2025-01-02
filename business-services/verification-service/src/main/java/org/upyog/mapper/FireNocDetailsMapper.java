package org.upyog.mapper;

import org.upyog.web.models.CommonDetails;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import static org.upyog.constants.VerificationSearchConstants.*;

@Component
public class FireNocDetailsMapper implements CommonDetailsMapper {

	@Override
    public String getModuleName() {
        return FireNocModule;
    }
	
	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode fireNocDetail = json.path("FireNOCs").isArray()
				&& json.path("FireNOCs").size() > 0 ? json.path("FireNOCs").get(0) : null;

		if (fireNocDetail == null) {
			return CommonDetails.builder().applicationNumber(NA).fromDate(NA).toDate(NA).address(NA)
					.status(NA).build();
		}

		
		// Extract the application number and status
		String applicationNumber = fireNocDetail.path("fireNOCDetails").path("applicationNumber").asText(NA);
		String status = fireNocDetail.path("fireNOCDetails").path("status").asText(NA);
		String moduleName = "firenoc";
		
		String fromDate = NA;
		String toDate = NA;
		String location = NA;


		// Build and return the CommonDetails object
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(toDate)
				.address(location).moduleName(moduleName)
				.status(status).build();
	}
}
