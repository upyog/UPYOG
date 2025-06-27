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
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BathRoom_Citya extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(BathRoom_Citya.class);

    // Rule identifier and description for bathroom scrutiny
    private static final String RULE_41_IV = "41-iv";
    public static final String BATHROOM_DESCRIPTION = "Bathroom";
    public static final String TOTAL_AREA = "Total Area >= ";
    public static final String WIDTH = ", Width >= ";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan validate(Plan pl) {
        // Currently, no validation logic is implemented
        return pl;
    }

    @Override
    public Plan process(Plan pl) {
        // Initialize scrutiny detail for bathroom validation
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Bathroom");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        // Map to store rule details
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_41_IV);
        details.put(DESCRIPTION, BATHROOM_DESCRIPTION);

        // Variables to store permissible and actual values
        BigDecimal bathroomMinWidth = BigDecimal.ZERO;
        BigDecimal bathroomtotalArea = BigDecimal.ZERO;
        BigDecimal minHeight = BigDecimal.ZERO, totalArea = BigDecimal.ZERO, minWidth = BigDecimal.ZERO;

        // Iterate through all blocks in the plan
        for (Block b : pl.getBlocks()) {
            if (b.getBuilding() != null && b.getBuilding().getFloors() != null
                    && !b.getBuilding().getFloors().isEmpty()) {

            	String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
                String feature = MdmsFeatureConstants.BATHROOM;

                // Prepare parameters for fetching permissible values
                Map<String, Object> params = new HashMap<>();
               
                params.put("feature", feature);
                params.put("occupancy", occupancyName);

                // Fetch permissible values for bathroom dimensions
                Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
                ArrayList<String> valueFromColumn = new ArrayList<>();
                valueFromColumn.add(EdcrRulesMdmsConstants.BATHROOM_TOTAL_AREA);
                valueFromColumn.add(EdcrRulesMdmsConstants.BATHROOM_MIN_WIDTH);

                List<Map<String, Object>> permissibleValue = new ArrayList<>();
                try {
                    permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
                    LOG.info("permissibleValue" + permissibleValue);
                } catch (NullPointerException e) {
                    LOG.error("Permissible Value for Bathroom not found--------", e);
                    return null;
                }

                // Extract permissible values if available
                if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.BATHROOM_TOTAL_AREA)) {
                    bathroomtotalArea = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.BATHROOM_TOTAL_AREA).toString()));
                    bathroomMinWidth = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.BATHROOM_MIN_WIDTH).toString()));
                }

                // Iterate through all floors in the block
                for (Floor f : b.getBuilding().getFloors()) {
                    if (f.getBathRoom() != null && f.getBathRoom().getHeights() != null
                            && !f.getBathRoom().getHeights().isEmpty() && f.getBathRoom().getRooms() != null
                            && !f.getBathRoom().getRooms().isEmpty()) {

                        // Calculate minimum height of bathrooms
                        if (f.getBathRoom().getHeights() != null && !f.getBathRoom().getHeights().isEmpty()) {
                            minHeight = f.getBathRoom().getHeights().get(0).getHeight();
                            for (RoomHeight rh : f.getBathRoom().getHeights()) {
                                if (rh.getHeight().compareTo(minHeight) < 0) {
                                    minHeight = rh.getHeight();
                                }
                            }
                        }

                        // Calculate total area and minimum width of bathrooms
                        if (f.getBathRoom().getRooms() != null && !f.getBathRoom().getRooms().isEmpty()) {
                            minWidth = f.getBathRoom().getRooms().get(0).getWidth();
                            for (Measurement m : f.getBathRoom().getRooms()) {
                                totalArea = totalArea.add(m.getArea());
                                if (m.getWidth().compareTo(minWidth) < 0) {
                                    minWidth = m.getWidth();
                                }
                            }
                        }

                        // Validate bathroom dimensions against permissible values
                        if (totalArea.compareTo(bathroomtotalArea) >= 0
                                && minWidth.compareTo(bathroomMinWidth) >= 0) {
                            details.put(REQUIRED, TOTAL_AREA + bathroomtotalArea.toString() + WIDTH + bathroomMinWidth.toString());
                            details.put(PROVIDED, TOTAL_AREA + totalArea + WIDTH + minWidth);
                            details.put(STATUS, Result.Accepted.getResultVal());
                            scrutinyDetail.getDetail().add(details);
                            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                        } else {
                            details.put(REQUIRED, ", " + TOTAL_AREA + bathroomtotalArea.toString() + WIDTH + bathroomMinWidth.toString());
                            details.put(PROVIDED, ", " + TOTAL_AREA + totalArea + WIDTH + minWidth);
                            details.put(STATUS, Result.Not_Accepted.getResultVal());
                            scrutinyDetail.getDetail().add(details);
                            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
                        }
                    }
                }
            }
        }

        return pl;
    }

    @Override
    public Map<String, Date> getAmendments() {
        // Return an empty map as no amendments are defined
        return new LinkedHashMap<>();
    }
}
