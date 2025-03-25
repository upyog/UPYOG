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
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BathRoomWaterClosets_Citya extends FeatureProcess {

    // Logger for logging information and errors
    private static final Logger LOG = LogManager.getLogger(BathRoomWaterClosets_Citya.class);

    // Rule identifier and description for bathroom water closets scrutiny
    private static final String RULE_41_IV = "41-iv";
    public static final String BathroomWaterClosets_DESCRIPTION = "Bathroom Water Closets";

    /**
     * This method is used to validate the plan object.
     * Currently, no validation logic is implemented.
     *
     * @param pl The plan object to validate.
     * @return The same plan object without any modifications.
     */
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    /**
     * This method processes the plan to validate bathroom water closets dimensions
     * against permissible values. It checks the height, width, and total area of
     * bathroom water closets in the plan and generates scrutiny details.
     *
     * @param pl The plan object to process.
     * @return The processed plan object with scrutiny details added.
     */
    @Override
    public Plan process(Plan pl) {
        // Initialize scrutiny detail for bathroom water closets validation
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_Bathroom Water Closets");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        // Map to store rule details
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_41_IV);
        details.put(DESCRIPTION, BathroomWaterClosets_DESCRIPTION);

        // Variables to store permissible and actual values
        BigDecimal minHeight = BigDecimal.ZERO, totalArea = BigDecimal.ZERO, minWidth = BigDecimal.ZERO;
        BigDecimal bathroomWCRequiredArea = BigDecimal.ZERO;
        BigDecimal bathroomWCRequiredWidth = BigDecimal.ZERO;
        BigDecimal bathroomWCRequiredHeight = BigDecimal.ZERO;
        BigDecimal bathroomWCRequiredminHeight = BigDecimal.ZERO;
        BigDecimal bathroomWCRequiredTotalArea = BigDecimal.ZERO;
        BigDecimal bathroomWCRequiredminWidth = BigDecimal.ZERO;

        // Determine the occupancy type and feature for fetching permissible values
        String occupancyName = null;
        String feature = "BathroomWaterClosets";

        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch permissible values for bathroom water closets dimensions
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add("bathroomWCRequiredArea");
        valueFromColumn.add("bathroomWCRequiredWidth");
        valueFromColumn.add("bathroomWCRequiredHeight");
        valueFromColumn.add("bathroomWCRequiredminHeight");
        valueFromColumn.add("bathroomWCRequiredTotalArea");
        valueFromColumn.add("bathroomWCRequiredminWidth");

        List<Map<String, Object>> permissibleValue = new ArrayList<>();

        try {
            permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
            LOG.info("permissibleValue" + permissibleValue);
        } catch (NullPointerException e) {
            LOG.error("Permissible Value for BathroomWaterClosets not found--------", e);
            return null;
        }

        // Extract permissible values if available
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey("bathroomWCRequiredArea")) {
            bathroomWCRequiredArea = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("bathroomWCRequiredArea").toString()));
            bathroomWCRequiredWidth = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("bathroomWCRequiredWidth").toString()));
            bathroomWCRequiredHeight = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("bathroomWCRequiredHeight").toString()));
            bathroomWCRequiredminHeight = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("bathroomWCRequiredminHeight").toString()));
            bathroomWCRequiredTotalArea = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("bathroomWCRequiredTotalArea").toString()));
            bathroomWCRequiredminWidth = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get("bathroomWCRequiredminWidth").toString()));
        }

        // Iterate through all blocks in the plan
        for (Block b : pl.getBlocks()) {
            if (b.getBuilding() != null && b.getBuilding().getFloors() != null
                    && !b.getBuilding().getFloors().isEmpty()) {

                // Iterate through all floors in the block
                for (Floor f : b.getBuilding().getFloors()) {

                    // Check if bathroom water closets exist on the floor
                    if (f.getBathRoomWaterClosets() != null && f.getBathRoomWaterClosets().getHeights() != null
                            && !f.getBathRoomWaterClosets().getHeights().isEmpty()
                            && f.getBathRoomWaterClosets().getRooms() != null
                            && !f.getBathRoomWaterClosets().getRooms().isEmpty()) {

                        // Calculate minimum height of bathroom water closets
                        if (f.getBathRoomWaterClosets().getHeights() != null
                                && !f.getBathRoomWaterClosets().getHeights().isEmpty()) {
                            minHeight = f.getBathRoomWaterClosets().getHeights().get(0).getHeight();
                            for (RoomHeight rh : f.getBathRoomWaterClosets().getHeights()) {
                                if (rh.getHeight().compareTo(minHeight) < 0) {
                                    minHeight = rh.getHeight();
                                }
                            }
                        }

                        // Calculate total area and minimum width of bathroom water closets
                        if (f.getBathRoomWaterClosets().getRooms() != null
                                && !f.getBathRoomWaterClosets().getRooms().isEmpty()) {
                            minWidth = f.getBathRoomWaterClosets().getRooms().get(0).getWidth();
                            for (Measurement m : f.getBathRoomWaterClosets().getRooms()) {
                                totalArea = totalArea.add(m.getArea());
                                if (m.getWidth().compareTo(minWidth) < 0) {
                                    minWidth = m.getWidth();
                                }
                            }
                        }

                        // Validate bathroom water closets dimensions against permissible values
                        if (minHeight.compareTo(bathroomWCRequiredminHeight) >= 0
                                && totalArea.compareTo(bathroomWCRequiredTotalArea) >= 0
                                && minWidth.compareTo(bathroomWCRequiredminWidth) >= 0) {

                            details.put(REQUIRED, "Height >= " + bathroomWCRequiredHeight + ", Total Area >= " + bathroomWCRequiredArea + ", Width >= " + bathroomWCRequiredWidth);
                            details.put(PROVIDED, "Height >= " + minHeight + ", Total Area >= " + totalArea
                                    + ", Width >= " + minWidth);
                            details.put(STATUS, Result.Accepted.getResultVal());
                            scrutinyDetail.getDetail().add(details);
                            pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);

                        } else {
                            details.put(REQUIRED, "Height >= " + bathroomWCRequiredHeight + ", Total Area >= " + bathroomWCRequiredArea + ", Width >= " + bathroomWCRequiredWidth);
                            details.put(PROVIDED, "Height >= " + minHeight + ", Total Area >= " + totalArea
                                    + ", Width >= " + minWidth);
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

    /**
     * This method returns an empty map as no amendments are defined for this feature.
     *
     * @return An empty map of amendments.
     */
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}
