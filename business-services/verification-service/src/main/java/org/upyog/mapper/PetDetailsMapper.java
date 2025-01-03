package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PetDetailsMapper implements CommonDetailsMapper {

	@Override
	public String getModuleName() {
		return PetModule;
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode petDetailNode = json.path(PetApplications).isArray() && json.path(PetApplications).size() > 0
				? json.path(PetApplications).get(0)
				: null;

		if (petDetailNode == null) {
			return CommonDetails.builder().fromDate(NA).toDate(NA).address(EmptyString).status(EmptyString).applicationNumber(EmptyString).name(NA).mobileNumber(NA)
					.build();
		}
		
		long validityDate = petDetailNode.path("validityDate").asLong(0L);
		String validFromString = NA;
		String validToString = NA;
		String status = petDetailNode.path("status").asText(EmptyString);
		String applicationNumber = petDetailNode.path("applicationNumber").asText(EmptyString);
		String ownerName = petDetailNode.path("applicantName").asText(NA);
        String ownerMobileNumber = CommonDetailUtil.maskMobileNumber(petDetailNode.path("mobileNumber").asText(NA));
		String moduleName = "pet-services";
		if (!"Approved".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA).name(NA).mobileNumber(NA)
					.address(NA).status("Pending").moduleName(moduleName).build();
		}
		if (validityDate != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validityDate), Date);
			validToString = CommonDetailUtil.addOneYearToEpoch(String.valueOf(validityDate)); // Add one year
		}
		

		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString).name(ownerName).mobileNumber(ownerMobileNumber)
				.toDate(validToString).address(EmptyString).moduleName(moduleName)
				.status(status).build();
	}

}
