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

import static org.egov.edcr.constants.DxfFileConstants.A;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.G;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreRoom_Citya extends FeatureProcess {

    // Constants for rule numbers and descriptions
    private static final String SUBRULE_41_II_A = "41-ii-a";
    private static final String SUBRULE_41_II_B = "41-ii-b";

    private static final String SUBRULE_41_II_A_AC_DESC = "Minimum height of ac room";
    private static final String SUBRULE_41_II_A_REGULAR_DESC = "Minimum height of regular room";
    private static final String SUBRULE_41_II_B_AREA_DESC = "Total area of rooms";
    private static final String SUBRULE_41_II_B_TOTAL_WIDTH = "Minimum Width of room";

    // Miscellaneous constants
    private static final String FLOOR = "Floor";
    private static final String ROOM_HEIGHT_NOTDEFINED = "Room height is not defined in layer ";
    private static final String LAYER_ROOM_HEIGHT = "BLK_%s_FLR_%s_%s";

    private static final Logger LOG = LogManager.getLogger(StoreRoom.class);

    // Autowired service to fetch permissible values from MDMS
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    // Placeholder validate method
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    @Override
    public Plan process(Plan pl) {

        // Color codes used to identify features like room height from drawing layers
        Map<String, Integer> heightOfRoomFeaturesColor = pl.getSubFeatureColorCodesMaster().get("HeightOfRoom");
        validate(pl); // Call to validate method (currently does nothing)
        HashMap<String, String> errors = new HashMap<>(); // To store layer-related errors

        // Variables to hold permissible values fetched from MDMS
        BigDecimal storeRoomValueOne = BigDecimal.ZERO;
        BigDecimal storeRoomValueTwo = BigDecimal.ZERO;
        BigDecimal storeRoomValueThree = BigDecimal.ZERO;

        String occupancyName = null;
        String feature = MdmsFeatureConstants.STORE_ROOM;

        // Determine occupancy type (currently checks only for "Residential")
        Map<String, Object> params = new HashMap<>();
        if (DxfFileConstants.A.equals(pl.getVirtualBuilding().getMostRestrictiveFarHelper().getType().getCode())) {
            occupancyName = "Residential";
        }

        // Setup parameters to fetch values from MDMS
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch rules related to the store room from plan's MDMS data
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // Required columns from MDMS
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_ONE);
        valueFromColumn.add(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_TWO);
        valueFromColumn.add(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_THREE);

        List<Map<String, Object>> permissibleValue = new ArrayList<>();

        // Get permissible values from MDMS
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        // Extract values from MDMS result if present
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_ONE)) {
            storeRoomValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_ONE).toString()));
            storeRoomValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_TWO).toString()));
            storeRoomValueThree = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.STORE_ROOM_VALUE_THREE).toString()));
        }

        // Begin block and floor-wise validation
        if (pl != null && pl.getBlocks() != null) {
            OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
                    ? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
                    : null;

            // Continue only if occupancy is of type A (Residential)
            if (mostRestrictiveOccupancy != null && mostRestrictiveOccupancy.getType() != null
                    && (A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))) {

                for (Block block : pl.getBlocks()) {
                    if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {

                        // Setup scrutiny detail report for room height
                        scrutinyDetail = new ScrutinyDetail();
                        scrutinyDetail.addColumnHeading(1, RULE_NO);
                        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                        scrutinyDetail.addColumnHeading(3, FLOOR);
                        scrutinyDetail.addColumnHeading(4, REQUIRED);
                        scrutinyDetail.addColumnHeading(5, PROVIDED);
                        scrutinyDetail.addColumnHeading(6, STATUS);
                        scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Room Height");

                        for (Floor floor : block.getBuilding().getFloors()) {

                            // Data containers
                            List<BigDecimal> roomAreas = new ArrayList<>();
                            List<BigDecimal> roomWidths = new ArrayList<>();
                            BigDecimal minimumHeight = BigDecimal.ZERO;
                            BigDecimal totalArea = BigDecimal.ZERO;
                            BigDecimal minWidth = BigDecimal.ZERO;
                            String subRule = null;
                            String subRuleDesc = null;
                            String color = "";

                            if (A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))
                                color = DxfFileConstants.COLOR_RESIDENTIAL_ROOM;

                            // If floor contains store rooms
                            if (floor.getStoreRooms() != null && floor.getStoreRooms().size() > 0) {

                                // Lists for storing height and geometry data
                                List<BigDecimal> storeRoomHeights = new ArrayList<>();
                                List<RoomHeight> heights = new ArrayList<>();
                                List<Measurement> rooms = new ArrayList<>();

                                // Collect all heights and room measurements
                                for (Room room : floor.getStoreRooms()) {
                                    if (room.getHeights() != null)
                                        heights.addAll(room.getHeights());
                                    if (room.getRooms() != null)
                                        rooms.addAll(room.getRooms());
                                }

                                // Filter height values based on color code
                                for (RoomHeight roomHeight : heights) {
                                    if (heightOfRoomFeaturesColor.get(color) == roomHeight.getColorCode()) {
                                        storeRoomHeights.add(roomHeight.getHeight());
                                    }
                                }

                                // Collect areas and widths from rooms with valid color
                                for (Measurement room : rooms) {
                                    if (heightOfRoomFeaturesColor.get(color) == room.getColorCode()) {
                                        roomAreas.add(room.getArea());
                                        roomWidths.add(room.getWidth());
                                    }
                                }

                                // If room height is captured, process it
                                if (!storeRoomHeights.isEmpty()) {
                                    BigDecimal minHeight = storeRoomHeights.stream().reduce(BigDecimal::min).get();

                                    // Choose the minimum permissible height based on occupancy
                                    if (A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
                                        minimumHeight = storeRoomValueOne;
                                        Log.info("Minimum Store Room Height required is set to : " + storeRoomValueThree);
                                    } else {
                                        minimumHeight = storeRoomValueOne;
                                    }

                                    subRule = SUBRULE_41_II_A;
                                    subRuleDesc = SUBRULE_41_II_A_REGULAR_DESC;

                                    // Build scrutiny result for height
                                    boolean valid = false;
                                    boolean isTypicalRepititiveFloor = false;
                                    Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);
                                    buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid, typicalFloorValues);

                                } else {
                                    // No height defined, log an error
                                    String layerName = String.format(LAYER_ROOM_HEIGHT, block.getNumber(), floor.getNumber(), "STORE_ROOM");
                                    errors.put(layerName, ROOM_HEIGHT_NOTDEFINED + layerName);
                                    pl.addErrors(errors);
                                }
                            }

                            // If area information is available, calculate total area and minimum width
                            if (!roomAreas.isEmpty()) {
                                totalArea = roomAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                                BigDecimal minRoomWidth = roomWidths.stream().reduce(BigDecimal::min).get();

                                // Assign expected minimum values from MDMS
                                minimumHeight = storeRoomValueTwo;

                                subRule = SUBRULE_41_II_B;
                                subRuleDesc = SUBRULE_41_II_B_AREA_DESC;

                                boolean valid = false;
                                boolean isTypicalRepititiveFloor = false;
                                Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);

                                // Build scrutiny result for total area
                                buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, totalArea, valid, typicalFloorValues);

                                // Optional: Add width check (currently commented out)
//                                subRuleDesc = SUBRULE_41_II_B_TOTAL_WIDTH;
//                                buildResult(pl, floor, minWidth, subRule, subRuleDesc, minRoomWidth, valid, typicalFloorValues);
                            }
                        }
                    }
                }
            }
        }

        return pl;
    }

    // Method to add scrutiny result based on expected vs actual value
    private void buildResult(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
                             BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues) {
        if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
                && expected.compareTo(BigDecimal.valueOf(0)) > 0 &&
                subRule != null && subRuleDesc != null) {

            if (actual.compareTo(expected) >= 0) {
                valid = true;
            }

            String value = typicalFloorValues.get("typicalFloors") != null
                    ? (String) typicalFloorValues.get("typicalFloors")
                    : " floor " + floor.getNumber();

            if (valid) {
                setReportOutputDetails(pl, subRule, subRuleDesc, value,
                        expected + DcrConstants.IN_METER,
                        actual + DcrConstants.IN_METER, Result.Accepted.getResultVal());
                LOG.info("Room Height Validation True: (Expected/Actual) " + expected + "/" + actual);
            } else {
                setReportOutputDetails(pl, subRule, subRuleDesc, value,
                        expected + DcrConstants.IN_METER,
                        actual + DcrConstants.IN_METER, Result.Not_Accepted.getResultVal());
                LOG.info("Room Height Validation False: (Expected/Actual) " + expected + "/" + actual);
            }
        }
    }

    // Helper method to populate scrutiny detail section of the report
    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String floor,
                                        String expected, String actual, String status) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(FLOOR, floor);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}