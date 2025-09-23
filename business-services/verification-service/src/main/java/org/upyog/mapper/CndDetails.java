package org.upyog.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import org.upyog.util.CommonDetailUtil;

@Component
public class CndDetails implements CommonDetailsMapper {

    @Override
    public String getModuleName() {
        return CND_MODULE_NAME;
    }

    @Override
    public CommonDetails mapJsonToCommonDetails(JsonNode json) {

        JsonNode cndDetailNode = json.path(CND_APPLICATIONS).isArray() && json.path(CND_APPLICATIONS).size() > 0
                ? json.path(CND_APPLICATIONS).get(0)
                : null;

        if (cndDetailNode == null) {
            return CommonDetails.builder().applicationNumber(NA).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA)
                    .status(NA).build();
        }

        // Extract main fields
        String applicationNumber = cndDetailNode.path("applicationNumber").asText(NA);
        String status = cndDetailNode.path("applicationStatus").asText(NA);
        String constructionFromDate = cndDetailNode.path("constructionFromDate").asText(NA);
        String constructionToDate = cndDetailNode.path("constructionToDate").asText(NA);
        String moduleName = "cnd-service";
        // Extract applicant details
        JsonNode applicantDetail = cndDetailNode.path("applicantDetail");
        String name = applicantDetail.path("nameOfApplicant").asText(NA);
        String mobileNumber = CommonDetailUtil.maskMobileNumber(
                applicantDetail.path("mobileNumber").asText(NA)
        );

        // Extract address
        JsonNode address = cndDetailNode.path("addressDetail");
        String fullAddress = String.format("%s, %s, %s, %s",
                address.path("addressLine1").asText(""),
                address.path("locality").asText(""),
                address.path("city").asText(""),
                address.path("pinCode").asText("")
        ).replaceAll("^,\\s*|,\\s*$", "").replaceAll(",\\s*,", ",");


        if (!"APPROVED".equalsIgnoreCase(status)) {
            // If not APPROVED, set status as Pending and other details as N/A
            return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA).status("Pending").moduleName(moduleName)
                    .build();
        }
        return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(constructionFromDate).toDate(constructionToDate).address(fullAddress.isEmpty() ? NA : fullAddress).name(name).mobileNumber(mobileNumber).status(status).moduleName(moduleName)
                .build();
    }

}
