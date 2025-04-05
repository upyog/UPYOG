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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Building;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OverHangs_Citya extends FeatureProcess {

    private static final Logger LOG = LogManager.getLogger(OverHangs_Citya.class);
    private static final String RULE_45 = "45";
    public static final String OVERHANGS_DESCRIPTION = "Minimum width of chajja";
    private static final String FLOOR = "Floor";

    @Override
    public Plan validate(Plan pl) {

        return pl;
    }
    
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan process(Plan pl) {

        // Initialize a map to store rule details
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_45); // Rule number for overhangs
        details.put(DESCRIPTION, OVERHANGS_DESCRIPTION); // Description of the rule

        // Initialize variables to store minimum width and permissible overhang value
        BigDecimal minWidth = BigDecimal.ZERO;
        BigDecimal overHangsValue = BigDecimal.ZERO;

        // Determine the occupancy type
        String occupancyName = null;
        String feature = MdmsFeatureConstants.OVERHANGS; // Feature name for overhangs

        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential"; // Set occupancy type to Residential if condition matches
        }

        // Add feature and occupancy to the parameters map
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch the list of rules from the plan object
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // Specify the columns to fetch from the rules
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE);

        // Initialize a list to store permissible values
        List<Map<String, Object>> permissibleValue = new ArrayList<>();

        // Fetch permissible values from MDMS
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue); // Log the fetched permissible values

        // Check if permissible values are available and update the overhangs value
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE)) {
            overHangsValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.PERMISSIBLE_VALUE).toString()));
        }

        // Iterate through all blocks in the plan
        for (Block b : pl.getBlocks()) {

            // Initialize scrutiny details for the block
            ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
            scrutinyDetail.setKey("Block_" + b.getNumber() + "_" + "Chajja");
            scrutinyDetail.addColumnHeading(1, RULE_NO);
            scrutinyDetail.addColumnHeading(2, FLOOR);
            scrutinyDetail.addColumnHeading(3, DESCRIPTION);
            scrutinyDetail.addColumnHeading(4, PERMISSIBLE);
            scrutinyDetail.addColumnHeading(5, PROVIDED);
            scrutinyDetail.addColumnHeading(6, STATUS);

            // Get the building object from the block
            Building building = b.getBuilding();
            if (building != null) {
                // Iterate through all floors in the building
                for (Floor floor : building.getFloors()) {
                    // Check if the floor has overhangs
                    if (floor.getOverHangs() != null && !floor.getOverHangs().isEmpty()) {
                        // Collect the widths of all overhangs
                        List<BigDecimal> widths = floor.getOverHangs().stream().map(overhang -> overhang.getWidth())
                                .collect(Collectors.toList());

                        // Find the minimum width among the overhangs
                        minWidth = widths.stream().reduce(BigDecimal::min).get();

                        // Compare the minimum width with the permissible overhang value
                        if (minWidth.compareTo(overHangsValue) > 0) {
                            // If the minimum width is greater than the permissible value, mark as Accepted
                            details.put(FLOOR, floor.getNumber().toString());
                            details.put(PERMISSIBLE, ">" + overHangsValue.toString());
                            details.put(PROVIDED, minWidth.toString());
                            details.put(STATUS, Result.Accepted.getResultVal());
                            scrutinyDetail.getDetail().add(details);
                            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                        } else {
                            // If the minimum width is less than or equal to the permissible value, mark as Not Accepted
                            details.put(FLOOR, floor.getNumber().toString());
                            details.put(PERMISSIBLE, ">" + overHangsValue.toString());
                            details.put(PROVIDED, minWidth.toString());
                            details.put(STATUS, Result.Not_Accepted.getResultVal());
                            scrutinyDetail.getDetail().add(details);
                            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                        }
                    }
                }
            }
        }
        return pl; // Return the updated plan object
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}
