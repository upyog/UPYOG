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
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GovtBuildingDistance_Citya extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(GovtBuildingDistance_Citya.class);

    // Rule identifier and description for government building distance scrutiny
    private static final String RULE_21 = "21";
    public static final String GOVTBUILDING_DESCRIPTION = "Distance from Government Building";

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
     * Processes the given plan to validate the distance from government buildings.
     * Fetches permissible values for distances and validates them against the plan details.
     *
     * @param pl The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */
    @Override
    public Plan process(Plan pl) {
        // Initialize scrutiny detail for government building distance validation
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Government Building Distance");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, DISTANCE);
        scrutinyDetail.addColumnHeading(4, PERMITTED);
        scrutinyDetail.addColumnHeading(5, PROVIDED);
        scrutinyDetail.addColumnHeading(6, STATUS);

        // Map to store validation errors
        HashMap<String, String> errors = new HashMap<>();

        // Map to store rule details
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_21);
        details.put(DESCRIPTION, GOVTBUILDING_DESCRIPTION);

        // Variables to store permissible and actual values
        BigDecimal minDistanceFromGovtBuilding = BigDecimal.ZERO;
        BigDecimal maxHeightOfBuilding = BigDecimal.ZERO;
        String GovtBuildingDistanceValue = "0";
        BigDecimal GovtBuildingDistanceMin = BigDecimal.ZERO;
        BigDecimal GovtBuildingDistanceMaxHeight = BigDecimal.ZERO;
        String GovtBuildingDistancePermitted = "0";

        // Fetch distances from government buildings and blocks in the plan
        List<BigDecimal> distancesFromGovtBuilding = pl.getDistanceToExternalEntity().getGovtBuildings();
        List<Block> blocks = pl.getBlocks();

        // Determine the occupancy type and feature for fetching permissible values
        String occupancyName = null;
        String feature = "GovtBuildingDistance";

        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch permissible values for government building distance
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add("GovtBuildingDistanceValue");
        valueFromColumn.add("GovtBuildingDistanceMin");
        valueFromColumn.add("GovtBuildingDistanceMaxHeight");
        valueFromColumn.add("GovtBuildingDistancePermitted");

        List<Map<String, Object>> permissibleValue = new ArrayList<>();
        try {
            permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
            LOG.info("permissibleValue" + permissibleValue);
        } catch (NullPointerException e) {
            LOG.error("Permissible Value for GovtBuildingDistance not found--------", e);
            return null;
        }

        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey("GovtBuildingDistanceValue")) {
            GovtBuildingDistanceValue = permissibleValue.get(0).get("GovtBuildingDistanceValue").toString();
            GovtBuildingDistanceMin = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("GovtBuildingDistanceMin").toString()));
            GovtBuildingDistanceMaxHeight = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("GovtBuildingDistanceMaxHeight").toString()));
            GovtBuildingDistancePermitted = permissibleValue.get(0).get("GovtBuildingDistancePermitted").toString();
        }

        // Validate distances from government buildings
        if (StringUtils.isNotBlank(pl.getPlanInformation().getBuildingNearGovtBuilding())
                && "YES".equalsIgnoreCase(pl.getPlanInformation().getBuildingNearGovtBuilding())) {
            if (!distancesFromGovtBuilding.isEmpty()) {
                minDistanceFromGovtBuilding = distancesFromGovtBuilding.stream().reduce(BigDecimal::min).get();

                // Get the maximum height of buildings in the plan
                for (Block b : blocks) {
                    if (b.getBuilding().getBuildingHeight().compareTo(maxHeightOfBuilding) > 0) {
                        maxHeightOfBuilding = b.getBuilding().getBuildingHeight();
                    }
                }

                // Validate the minimum distance and building height
                if (minDistanceFromGovtBuilding.compareTo(GovtBuildingDistanceMin) > 0) {
                    details.put(DISTANCE, ">" + GovtBuildingDistanceValue);
                    details.put(PERMITTED, "ALL");
                    details.put(PROVIDED, minDistanceFromGovtBuilding.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                    scrutinyDetail.getDetail().add(details);
                    pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                } else {
                    if (maxHeightOfBuilding.compareTo(GovtBuildingDistanceMaxHeight) <= 0) {
                        details.put(DISTANCE, "<=" + GovtBuildingDistanceValue);
                        details.put(PERMITTED, "Building Height: " + GovtBuildingDistancePermitted + "mt");
                        details.put(PROVIDED, "Building Height: " + maxHeightOfBuilding + "mt");
                        details.put(STATUS, Result.Accepted.getResultVal());
                        scrutinyDetail.getDetail().add(details);
                        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                    } else {
                        details.put(DISTANCE, "<=" + GovtBuildingDistanceValue);
                        details.put(PERMITTED, "Building Height: " + GovtBuildingDistancePermitted + "mt");
                        details.put(PROVIDED, "Building Height: " + maxHeightOfBuilding + "mt");
                        details.put(STATUS, Result.Not_Accepted.getResultVal());
                        scrutinyDetail.getDetail().add(details);
                        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                    }
                }
            } else {
                errors.put("Distance_From_Govt_Building", "No distance is provided from government building");
                pl.addErrors(errors);
            }
        }
        return pl;
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
