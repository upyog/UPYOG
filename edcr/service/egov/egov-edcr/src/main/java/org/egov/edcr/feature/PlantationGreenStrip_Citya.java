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
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlantationGreenStrip_Citya extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(PlantationGreenStrip_Citya.class);
    private static final String RULE_37_6 = "37-6";

    @Override
    public Plan validate(Plan pl) {
        return null; // Validation logic is not implemented
    }

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan process(Plan pl) {
        // Initialize variables to store permissible values for plantation green strip
        BigDecimal plantationGreenStripPlanValue = BigDecimal.ZERO;
        BigDecimal plantationGreenStripMinWidth = BigDecimal.ZERO;
        BigDecimal plantationGreenStripbuildResult = BigDecimal.ZERO;

        // Determine the occupancy type
        String occupancyName = null;
        String feature = MdmsFeatureConstants.PLANTATION_GREEN_STRIP; // Feature name for plantation green strip

        // Prepare parameters for fetching MDMS values
        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential"; // Set occupancy type to Residential if condition matches
        }
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch the list of rules from the plan object
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // Specify the columns to fetch from the rules
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_PLAN_VALUE); // Minimum plot area for plantation
        valueFromColumn.add(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_MIN_WIDTH); // Minimum width of plantation strip
        valueFromColumn.add(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_BUILD_RESULT); // Build result for plantation strip

        // Initialize a list to store permissible values
        List<Map<String, Object>> permissibleValue = new ArrayList<>();

        // Fetch permissible values from MDMS
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue); // Log the fetched permissible values

        // Check if permissible values are available and update the plantation green strip values
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_PLAN_VALUE)) {
            plantationGreenStripPlanValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_PLAN_VALUE).toString()));
            plantationGreenStripMinWidth = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_MIN_WIDTH).toString()));
            plantationGreenStripbuildResult = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PLANTATION_GREEN_STRIP_BUILD_RESULT).toString()));
        }

        // Check if the plot area exceeds the minimum required area for plantation
        if (pl.getPlot() != null && pl.getPlot().getArea().compareTo(plantationGreenStripPlanValue) > 0) {
            // Iterate through all blocks in the plan
            for (Block block : pl.getBlocks()) {

                // Initialize scrutiny details for the block
                ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                scrutinyDetail.addColumnHeading(3, PERMISSIBLE);
                scrutinyDetail.addColumnHeading(4, PROVIDED);
                scrutinyDetail.addColumnHeading(5, STATUS);
                scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Continuous Green Planting Strip");

                boolean isWidthAccepted = false; // Flag to track if the width is acceptable
                BigDecimal minWidth = BigDecimal.ZERO; // Minimum width of the plantation strip

                // Collect the widths of all plantation green strips in the block
                List<BigDecimal> widths = block.getPlantationGreenStripes().stream()
                        .map(greenStrip -> greenStrip.getWidth()).collect(Collectors.toList());

                // Check if widths are available
                if (!widths.isEmpty()) {
                    // Find the minimum width among the plantation strips
                    minWidth = widths.stream().reduce(BigDecimal::min).get();

                    // Validate the minimum width against the permissible width
                    if (minWidth.compareTo(plantationGreenStripMinWidth) >= 0) {
                        isWidthAccepted = true; // Mark as accepted if the width is within permissible limits
                    }

                    // Add the result to the scrutiny report
                    buildResult(pl, scrutinyDetail, isWidthAccepted, "Width of continuous plantation green strip",
                            ">= " + plantationGreenStripbuildResult.toString(),
                            minWidth.setScale(DcrConstants.DECIMALDIGITS_MEASUREMENTS, DcrConstants.ROUNDMODE_MEASUREMENTS)
                                    .toString());
                }
            }
        }
        return pl; // Return the updated plan object
    }

    /**
     * Adds the result of the validation to the scrutiny report.
     *
     * @param pl The plan object
     * @param scrutinyDetail The scrutiny detail object
     * @param valid Whether the validation passed or failed
     * @param description Description of the validation
     * @param permited The permissible value
     * @param provided The provided value
     */
    private void buildResult(Plan pl, ScrutinyDetail scrutinyDetail, boolean valid, String description, String permited,
            String provided) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_37_6); // Rule number for plantation green strip
        details.put(DESCRIPTION, description); // Description of the validation
        details.put(PERMISSIBLE, permited); // Permissible value
        details.put(PROVIDED, provided); // Provided value
        details.put(STATUS, valid ? Result.Accepted.getResultVal() : Result.Not_Accepted.getResultVal()); // Validation status
        scrutinyDetail.getDetail().add(details); // Add details to scrutiny detail
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail); // Add scrutiny detail to the plan's report output
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>(); // Return an empty map for amendments
    }
}
