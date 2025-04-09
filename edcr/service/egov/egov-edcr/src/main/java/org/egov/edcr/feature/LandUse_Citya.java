/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.edcr.feature;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Occupancy;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.Util;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LandUse_Citya extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(LandUse_Citya.class);

    // Constants for rule identifiers and descriptions
    private static final String RULE_28 = "28";
    private static final String ROAD_WIDTH = "Road Width";

    // Variable to store permissible road width
    public static BigDecimal RoadWidth = BigDecimal.ZERO;

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    /**
     * Validates the given plan object.
     * Currently, no specific validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    /**
     * Processes the given plan to validate land use zones.
     * Fetches permissible values for road width and validates them against the plan details.
     *
     * @param pl The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */
    @Override
    public Plan process(Plan pl) {
        // Map to store validation errors
        HashMap<String, String> errors = new HashMap<>();

        // Validate commercial zones in the plan
        validateCommercialZone(pl, errors);
        return pl;
    }

    /**
     * Validates the commercial zone for the given plan.
     * Checks if the road width and land use zone meet the required conditions.
     *
     * @param pl The plan object to validate.
     * @param errors The map to store validation errors.
     */
    private void validateCommercialZone(Plan pl, HashMap<String, String> errors) {
        String occupancyName = null;
        String feature = MdmsFeatureConstants.LAND_USE;

        // Determine the occupancy type for fetching permissible values
        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch permissible values for road width
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE);

				List<Map<String, Object>> permissibleValue = new ArrayList<>();
			
				
					permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
            RoadWidth = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
        } else {
            RoadWidth = BigDecimal.ZERO;
        }

        // Iterate through all blocks in the plan
        for (Block block : pl.getBlocks()) {
            StringBuffer floorNos = new StringBuffer();
            boolean isAccepted = false;
            String blkNo = block.getNumber();

            // Initialize scrutiny detail for land use validation
            scrutinyDetail.addColumnHeading(1, RULE_NO);
            scrutinyDetail.addColumnHeading(2, DESCRIPTION);
            scrutinyDetail.addColumnHeading(3, ROAD_WIDTH);
            scrutinyDetail.addColumnHeading(4, REQUIRED);
            scrutinyDetail.addColumnHeading(5, PROVIDED);
            scrutinyDetail.addColumnHeading(6, STATUS);
            scrutinyDetail.setKey("Block_" + blkNo + "_" + "Land Use");

            // Validate road width and land use zone
            BigDecimal roadWidth = pl.getPlanInformation().getRoadWidth();
            if (pl.getPlanInformation() != null && roadWidth != null
                    && StringUtils.isNotBlank(pl.getPlanInformation().getLandUseZone())
                    && DxfFileConstants.COMMERCIAL.equalsIgnoreCase(pl.getPlanInformation().getLandUseZone())
                    && Util.roundOffTwoDecimal(roadWidth).compareTo(RoadWidth) >= 0) {

                List<Floor> floors = block.getBuilding().getFloors();

                // Check if at least one floor is commercial
                for (Floor floor : floors) {
                    List<Occupancy> occupancies = floor.getOccupancies();
                    List<String> occupancyTypes = new ArrayList<>();

                    for (Occupancy occupancy : occupancies) {
                        if (occupancy.getTypeHelper() != null && occupancy.getTypeHelper().getType() != null) {
                            occupancyTypes.add(occupancy.getTypeHelper().getType().getCode());
                        }

                        if (occupancy.getTypeHelper() != null && occupancy.getTypeHelper().getSubtype() != null) {
                            occupancyTypes.add(occupancy.getTypeHelper().getSubtype().getCode());
                        }
                    }

                    if (occupancyTypes.size() > 0) {
                        if (occupancyTypes.contains(DxfFileConstants.F)) {
                            floorNos.append(floor.getNumber()).append(",");
                        }
                    }
                }

                // Add scrutiny details for land use validation
                Map<String, String> details = new HashMap<>();
                details.put(RULE_NO, RULE_28);
                details.put(DESCRIPTION, "Land use Zone");
                details.put(ROAD_WIDTH, pl.getPlanInformation().getRoadWidth().toString());
                details.put(REQUIRED, "At least one floor should be commercial");
                details.put(PROVIDED, floorNos.length() == 0 ? "Commercial floor is not present"
                        : floorNos.toString().substring(0, floorNos.length() - 1) + " floors are commercial");
                details.put(STATUS, isAccepted ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal());
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }
        }
    }

    /**
     * Returns an empty map as no amendments are defined for this feature.
     *
     * @return An empty map of amendments.
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
