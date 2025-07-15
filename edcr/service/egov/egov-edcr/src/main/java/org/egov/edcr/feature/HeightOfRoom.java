/*
 * UPYOG  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
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
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Door;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Measurement;
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.Room;
import org.egov.common.entity.edcr.RoomHeight;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.common.entity.edcr.Window;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeightOfRoom extends FeatureProcess {

	private static final String RULE = "4.4.4";
	private static final String RULE1 = "4.4.4 (ix)";
	private static final String SUBRULE_41_II_B = "41-ii-b";

	private static final String RULE_AC_DESC = "Minimum height of ac room";
	private static final String RULE_REGULAR_DESC = "Minimum height of regular room";
	private static final String SUBRULE_41_II_B_AREA_DESC = "Total area of rooms";
	private static final String SUBRULE_41_II_B_TOTAL_WIDTH = "Minimum Width of room";

	public static final BigDecimal MINIMUM_HEIGHT_3_6 = BigDecimal.valueOf(3.6);
	public static final BigDecimal MINIMUM_HEIGHT_3 = BigDecimal.valueOf(3);
	public static final BigDecimal MINIMUM_HEIGHT_2_75 = BigDecimal.valueOf(2.75);
	public static final BigDecimal MINIMUM_HEIGHT_2_4 = BigDecimal.valueOf(2.4);
	public static final BigDecimal MINIMUM_AREA_9_5 = BigDecimal.valueOf(9.5);
	public static final BigDecimal MINIMUM_AREA_9_2 = BigDecimal.valueOf(9.2);
	public static final BigDecimal MINIMUM_WIDTH_2_4 = BigDecimal.valueOf(2.4);
	public static final BigDecimal MINIMUM_WIDTH_2_7 = BigDecimal.valueOf(2.7);
	public static final BigDecimal MINIMUM_WIDTH_2_1 = BigDecimal.valueOf(2.1);
	public static final BigDecimal MINIMUM_AREA_7_5 = BigDecimal.valueOf(7.5);
	public static final BigDecimal MAXIMUM_AREA_46_45 = BigDecimal.valueOf(46.45);
	private static final BigDecimal VENTILATION_PERCENTAGE = BigDecimal.valueOf(20); // 20% ventilation requirement
	private static final String FLOOR = "Floor";
	private static final String ROOM_HEIGHT_NOTDEFINED = "Room height is not defined in layer ";
	private static final String LAYER_ROOM_HEIGHT = "BLK_%s_FLR_%s_%s";

	private static final BigDecimal MIN_WINDOW_HEIGHT = BigDecimal.valueOf(0.50);
	private static final BigDecimal MIN_DOOR_HEIGHT = BigDecimal.valueOf(2.0);
	private static final BigDecimal MIN_WINDOW_WIDTH = BigDecimal.valueOf(0.50);
	private static final BigDecimal MIN_DOOR_WIDTH = BigDecimal.valueOf(1.0);
	private static final BigDecimal MIN_NON_HABITATIONAL_DOOR_WIDTH = BigDecimal.valueOf(0.76);
	BigDecimal ventilationPercentage = BigDecimal.ZERO;
	BigDecimal minDoorWidth = BigDecimal.ZERO;
	BigDecimal minDoorHeight = BigDecimal.ZERO;
	
	String subRuleDoor = "6.4.1";
	String subRuleDesc = "Minimum Area and Width of Room";
	String subRuleDesc1 = "Room Wise Ventialtion";
	String subRuleDesc5 = "Door Ventialtion";
	String subRuleDesc2 = "Room Wise Window Area";
	String subRuleDesc3 = "Window Area";
	String subRuleDesc4 = "Room wise Door Area";
	String subRuleDesc6 = "Door Area";
	private BigDecimal minimumHeight;

	private static final Logger LOG = LogManager.getLogger(HeightOfRoom.class);

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Autowired
	CacheManagerMdms cache;

	@Override
	public Plan process(Plan pl) {

		Map<String, Integer> heightOfRoomFeaturesColor = pl.getSubFeatureColorCodesMaster().get("HeightOfRoom");
		validate(pl);
		HashMap<String, String> errors = new HashMap<>();
		if (pl != null && pl.getBlocks() != null) {
			OccupancyTypeHelper mostRestrictiveOccupancy = pl.getVirtualBuilding() != null
					? pl.getVirtualBuilding().getMostRestrictiveFarHelper()
					: null;
			String occupancyName = "";
			if (mostRestrictiveOccupancy != null && mostRestrictiveOccupancy.getType() != null
					&& (A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())
							|| (G.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())
									|| F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())))) {
				for (Block block : pl.getBlocks()) {
					if (block.getBuilding() != null && !block.getBuilding().getFloors().isEmpty()) {
						scrutinyDetail = new ScrutinyDetail();
						scrutinyDetail.addColumnHeading(1, RULE_NO);
						scrutinyDetail.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail.addColumnHeading(3, FLOOR);
						scrutinyDetail.addColumnHeading(4, Room);
						scrutinyDetail.addColumnHeading(5, REQUIRED);
						scrutinyDetail.addColumnHeading(6, PROVIDED);
						scrutinyDetail.addColumnHeading(7, STATUS);

						scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + MdmsFeatureConstants.ROOM);

						ScrutinyDetail scrutinyDetail1 = new ScrutinyDetail();
						scrutinyDetail.addColumnHeading(1, RULE_NO);
						scrutinyDetail.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail.addColumnHeading(3, FLOOR);
						scrutinyDetail.addColumnHeading(4, Room);
						scrutinyDetail.addColumnHeading(5, REQUIRED);
						scrutinyDetail.addColumnHeading(6, PROVIDED);
						scrutinyDetail.addColumnHeading(7, STATUS);
						scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + MdmsFeatureConstants.ROOM_AREA);

						ScrutinyDetail scrutinyDetail2 = new ScrutinyDetail();
						scrutinyDetail2.addColumnHeading(1, RULE_NO);
						scrutinyDetail2.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail2.addColumnHeading(3, FLOOR);
						scrutinyDetail2.addColumnHeading(4, Room);
						scrutinyDetail2.addColumnHeading(5, REQUIRED);
						scrutinyDetail2.addColumnHeading(6, PROVIDED);
						scrutinyDetail2.addColumnHeading(7, STATUS);
						scrutinyDetail2.setKey("Block_" + block.getNumber() + "_" + MdmsFeatureConstants.DOORS);

						ScrutinyDetail scrutinyDetail5 = new ScrutinyDetail();
						scrutinyDetail5.addColumnHeading(1, RULE_NO);
						scrutinyDetail5.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail5.addColumnHeading(3, FLOOR);
						scrutinyDetail5.addColumnHeading(4, Room);
						scrutinyDetail5.addColumnHeading(5, REQUIRED);
						scrutinyDetail5.addColumnHeading(6, PROVIDED);
						scrutinyDetail5.addColumnHeading(7, STATUS);
						scrutinyDetail5.setKey(
								"Block_" + block.getNumber() + "_" + MdmsFeatureConstants.NON_HABITATIONAL_DOORS);

						ScrutinyDetail scrutinyDetail3 = new ScrutinyDetail();
						scrutinyDetail3.addColumnHeading(1, RULE_NO);
						scrutinyDetail3.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail3.addColumnHeading(3, FLOOR);
						scrutinyDetail3.addColumnHeading(4, Room);
						scrutinyDetail3.addColumnHeading(5, REQUIRED);
						scrutinyDetail3.addColumnHeading(6, PROVIDED);
						scrutinyDetail3.addColumnHeading(7, STATUS);
						scrutinyDetail3.setKey("Block_" + block.getNumber() + "_" + MdmsFeatureConstants.WINDOW);

						ScrutinyDetail scrutinyDetail4 = new ScrutinyDetail();
						scrutinyDetail4.addColumnHeading(1, RULE_NO);
						scrutinyDetail4.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail4.addColumnHeading(3, FLOOR);
						scrutinyDetail4.addColumnHeading(4, Room);
						scrutinyDetail4.addColumnHeading(5, REQUIRED);
						scrutinyDetail4.addColumnHeading(6, PROVIDED);
						scrutinyDetail4.addColumnHeading(7, STATUS);
						scrutinyDetail4.setKey("Block_" + block.getNumber() + "_"
								+ MdmsFeatureConstants.ROOM_WISE_VENTILATION + " Doors and Windows)");

						ScrutinyDetail scrutinyDetail6 = new ScrutinyDetail();
						scrutinyDetail6.addColumnHeading(1, RULE_NO);
						scrutinyDetail6.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail6.addColumnHeading(3, FLOOR);
						scrutinyDetail6.addColumnHeading(4, Room);
						scrutinyDetail6.addColumnHeading(5, REQUIRED);
						scrutinyDetail6.addColumnHeading(6, PROVIDED);
						scrutinyDetail6.addColumnHeading(7, STATUS);
						scrutinyDetail6.setKey(
								"Block_" + block.getNumber() + "_" + MdmsFeatureConstants.ROOM_WISE_WINDOW_AREA);

						ScrutinyDetail scrutinyDetail7 = new ScrutinyDetail();
						scrutinyDetail7.addColumnHeading(1, RULE_NO);
						scrutinyDetail7.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail7.addColumnHeading(3, FLOOR);
						scrutinyDetail7.addColumnHeading(4, Room);
						scrutinyDetail7.addColumnHeading(5, REQUIRED);
						scrutinyDetail7.addColumnHeading(6, PROVIDED);
						scrutinyDetail7.addColumnHeading(7, STATUS);
						scrutinyDetail7
								.setKey("Block_" + block.getNumber() + "_" + MdmsFeatureConstants.DOOR_VENTILATION);

						ScrutinyDetail scrutinyDetail8 = new ScrutinyDetail();
						scrutinyDetail8.addColumnHeading(1, RULE_NO);
						scrutinyDetail8.addColumnHeading(2, DESCRIPTION);
						scrutinyDetail8.addColumnHeading(3, FLOOR);
						scrutinyDetail8.addColumnHeading(4, Room);
						scrutinyDetail8.addColumnHeading(5, REQUIRED);
						scrutinyDetail8.addColumnHeading(6, PROVIDED);
						scrutinyDetail8.addColumnHeading(7, STATUS);
						scrutinyDetail8
								.setKey("Block_" + block.getNumber() + "_" + MdmsFeatureConstants.ROOM_WISE_DOOR_AREA);

						for (Floor floor : block.getBuilding().getFloors()) {
							List<BigDecimal> roomAreas = new ArrayList<>();
							List<BigDecimal> roomWidths = new ArrayList<>();
							BigDecimal minimumHeight = BigDecimal.ZERO;
							BigDecimal totalArea = BigDecimal.ZERO;
							BigDecimal minWidth = BigDecimal.ZERO;
							BigDecimal maxArea = BigDecimal.ZERO;
							String subRule = "4.4.4";
							String color = "";
						
							if (A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))
								color = DxfFileConstants.COLOR_RESIDENTIAL_ROOM;
							else if (F.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))
								color = DxfFileConstants.COLOR_COMMERCIAL_ROOM;
							else if (G.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode()))
								color = DxfFileConstants.COLOR_INDUSTRIAL_ROOM;

							if (floor.getAcRooms() != null && !floor.getAcRooms().isEmpty()) {
								processAcRooms(floor, block, color, mostRestrictiveOccupancy, heightOfRoomFeaturesColor,
										roomAreas, roomWidths, pl, errors);
							}

							processRegularRooms(pl, floor, block, color, mostRestrictiveOccupancy,
									heightOfRoomFeaturesColor, scrutinyDetail, errors);


							if (!roomAreas.isEmpty()) {
								totalArea = roomAreas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
								BigDecimal minRoomWidth = roomWidths.stream().reduce(BigDecimal::min).get();

								evaluateFirstRoomDetails(pl, floor, roomAreas, roomWidths, subRule, subRuleDesc,
										scrutinyDetail);

								evaluateRemainingRoomDetails(pl, floor, roomAreas, roomWidths, subRule, subRuleDesc,
										scrutinyDetail1);
							}

							evaluateDoorsForFloor(pl, floor, scrutinyDetail2);


							evaluateNonHabitationalDoors(pl, floor, scrutinyDetail5);


							evaluateWindows(pl, floor, scrutinyDetail3);
	
							if (floor.getRegularRooms() != null) {
							    for (Room room : floor.getRegularRooms()) {
							        evaluateRoomVentilation(pl, floor, room, scrutinyDetail4, scrutinyDetail6);
							    }
							}

							for (Room room : floor.getRegularRooms()) {
							    BigDecimal roomArea = calculateRoomArea(room);
							    BigDecimal requiredVentilationArea = getRequiredVentilationArea(roomArea, ventilationPercentage);
							    BigDecimal totalDoorArea = calculateTotalDoorArea(room);

							      evaluateDoorDimensions(pl, room, floor, requiredVentilationArea, totalDoorArea, scrutinyDetail8);

							}


						}
					}
				}
			}
		}

		return pl;

	}
	
	private void evaluateDoorsForFloor(Plan pl, Floor floor, ScrutinyDetail scrutinyDetail) {
	    if (floor.getDoors() == null || floor.getDoors().isEmpty()) return;

	    for (Door door : floor.getDoors()) {
	        if (door != null) {
	            evaluateSingleDoor(pl, floor, door, scrutinyDetail);
	        }
	    }
	}
	private void evaluateSingleDoor(Plan pl, Floor floor, Door door, ScrutinyDetail scrutinyDetail) {
	    BigDecimal doorWidth = door.getDoorWidth();
	    BigDecimal minDoorWidth = getMinimumDoorWidth(pl);

	    String subRule = SUBRULE_41_II_B;
	    String subRuleDesc = SUBRULE_41_II_B;

	    String requirement = " Width >=" + minDoorWidth;
	    String provided = " Width = " + doorWidth + DcrConstants.IN_METER;

	    String result = (doorWidth != null && doorWidth.compareTo(minDoorWidth) >= 0)
	            ? Result.Accepted.getResultVal()
	            : Result.Not_Accepted.getResultVal();

	    setReportOutputDetails(pl, subRule, subRuleDesc, floor.getNumber().toString(), requirement, provided, "", result, scrutinyDetail);
	}

	private BigDecimal calculateRoomArea(Room room) {
	    BigDecimal roomArea = BigDecimal.ZERO;
	    if (room.getRooms() != null) {
	        for (Measurement measurement : room.getRooms()) {
	            roomArea = roomArea.add(measurement.getArea());
	        }
	    }
	    return roomArea;
	}

	private BigDecimal getRequiredVentilationArea(BigDecimal roomArea, BigDecimal ventilationPercentage) {
	    return roomArea.multiply(ventilationPercentage)
	            .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
	}

	private BigDecimal calculateTotalDoorArea(Room room) {
	    BigDecimal totalDoorArea = BigDecimal.ZERO;
	    if (room.getDoors() != null) {
	        for (Door door : room.getDoors()) {
	            BigDecimal doorArea = door.getDoorHeight().multiply(door.getDoorWidth())
	                    .setScale(2, BigDecimal.ROUND_HALF_UP);
	            totalDoorArea = totalDoorArea.add(doorArea);
	        }
	    }
	    return totalDoorArea;
	}

	private void evaluateDoorDimensions(Plan pl, Room room, Floor floor, BigDecimal requiredVentilationArea,
			BigDecimal totalDoorArea, ScrutinyDetail scrutinyDetail8) {
		for (Door door : room.getDoors()) {
			BigDecimal doorHeight = door.getDoorHeight().setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal doorWidth = door.getDoorWidth().setScale(2, BigDecimal.ROUND_HALF_UP);

			minDoorHeight = BigDecimal.ZERO;
			minDoorWidth = BigDecimal.ZERO;
			String subRule = "4.4.4";

			List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.ROOM_WISE_DOOR_AREA, false);
			Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();

			if (matchedRule.isPresent()) {
				MdmsFeatureRule rule = matchedRule.get();
				minDoorHeight = rule.getMinDoorHeight();
				minDoorWidth = rule.getMinDoorWidth();
			}

			if (doorHeight.compareTo(minDoorHeight) >= 0 && doorWidth.compareTo(minDoorWidth) >= 0) {
				setReportOutputDetails(pl, subRuleDoor, subRuleDesc4, floor.getNumber().toString(), room.getNumber(),
						"Height >= " + minDoorHeight + ", Width >= " + minDoorWidth,
						"Height = " + doorHeight + ", Width = " + doorWidth, Result.Accepted.getResultVal(),
						scrutinyDetail8);
			} else {
				setReportOutputDetails(pl, subRule, subRuleDesc4, floor.getNumber().toString(), room.getNumber(),
						"Height >= " + minDoorHeight + ", Width >= " + minDoorWidth,
						"Height = " + doorHeight + ", Width = " + doorWidth, Result.Not_Accepted.getResultVal(),
						scrutinyDetail8);
			}
		}
	}

	private BigDecimal getMinimumDoorWidth(Plan pl) {
	    List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.DOORS, false);

	    return rules.stream()
	            .map(obj -> (MdmsFeatureRule) obj)
	            .findFirst()
	            .map(MdmsFeatureRule::getPermissible)
	            .orElse(BigDecimal.ZERO);
	}

	private void evaluateWindows(Plan pl, Floor floor, ScrutinyDetail scrutinyDetail) {
	    if (floor.getWindows() == null || floor.getWindows().isEmpty()) return;

	    for (Window window : floor.getWindows()) {
	        if (window != null) {
	            evaluateSingleWindow(pl, floor, window, scrutinyDetail);
	        }
	    }
	}
	
	private void evaluateRoomVentilation(Plan pl, Floor floor, Room room, ScrutinyDetail ventilationDetail,
			ScrutinyDetail windowDetail) {

		BigDecimal roomArea = calculateRoomArea(room);
		BigDecimal ventilationPercentage = getVentilationPercentage(pl);
		BigDecimal requiredVentilationArea = roomArea.multiply(ventilationPercentage).divide(BigDecimal.valueOf(100))
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		String subRuleDesc1 = "Room Wise Ventialtion";

		BigDecimal totalWindowArea = calculateWindowArea(room);
		BigDecimal totalDoorArea = calculateDoorArea(room);
		BigDecimal combinedArea = totalWindowArea.add(totalDoorArea);

		if (requiredVentilationArea.compareTo(BigDecimal.ZERO) != 0 && combinedArea.compareTo(BigDecimal.ZERO) != 0) {
			String result = combinedArea.compareTo(requiredVentilationArea) >= 0 ? Result.Accepted.getResultVal()
					: Result.Not_Accepted.getResultVal();

			setReportOutputDetails(pl, RULE1, subRuleDesc1, floor.getNumber().toString(), "" + room.getNumber(),
					"Ventilation Required >= " + requiredVentilationArea, "Area provided = " + combinedArea, result,
					ventilationDetail);
		}

		validateIndividualWindows(pl, floor, room, windowDetail);
	}

	private BigDecimal getVentilationPercentage(Plan pl) {
	    List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.ROOM_WISE_VENTILATION, false);
	    Optional<MdmsFeatureRule> matchedRule = rules.stream()
	            .map(obj -> (MdmsFeatureRule) obj)
	            .findFirst();
	    return matchedRule.map(MdmsFeatureRule::getPermissible).orElse(BigDecimal.ZERO);
	}
	private BigDecimal calculateWindowArea(Room room) {
	    BigDecimal area = BigDecimal.ZERO;
	    if (room.getWindows() != null) {
	        for (Window window : room.getWindows()) {
	            BigDecimal height = window.getWindowHeight();
	            BigDecimal width = window.getWindowWidth();
	            area = area.add(height.multiply(width).setScale(2, BigDecimal.ROUND_HALF_UP));
	        }
	    }
	    return area;
	}
	private BigDecimal calculateDoorArea(Room room) {
	    BigDecimal area = BigDecimal.ZERO;
	    if (room.getDoors() != null) {
	        for (Door door : room.getDoors()) {
	            BigDecimal height = door.getDoorHeight();
	            BigDecimal width = door.getDoorWidth();
	            area = area.add(height.multiply(width).setScale(2, BigDecimal.ROUND_HALF_UP));
	        }
	    }
	    return area;
	}

	private void validateIndividualWindows(Plan pl, Floor floor, Room room, ScrutinyDetail scrutinyDetail) {
		if (room.getWindows() == null)
			return;
		String subRuleDesc2 = "Room Wise Window Area";
		String subRule = "4.4.4";

		for (Window window : room.getWindows()) {
			BigDecimal height = window.getWindowHeight().setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal width = window.getWindowWidth().setScale(2, BigDecimal.ROUND_HALF_UP);

			setReportOutputDetails(pl, subRule, subRuleDesc2, floor.getNumber().toString(), room.getNumber(), "",
					"Height = " + height + ", Width = " + width, Result.Accepted.getResultVal(), scrutinyDetail);
		}
	}


	private void evaluateSingleWindow(Plan pl, Floor floor, Window window, ScrutinyDetail scrutinyDetail) {
	    BigDecimal windowHeight = window.getWindowHeight().setScale(2, BigDecimal.ROUND_HALF_UP);
	    BigDecimal windowWidth = window.getWindowWidth().setScale(2, BigDecimal.ROUND_HALF_UP);

	    BigDecimal minWindowHeight = BigDecimal.valueOf(0.50);
	    BigDecimal minWindowWidth = BigDecimal.valueOf(0.50);

	   
	    String subRuleDesc3 = "Window Area";

	    String requirement = "Height >= " + minWindowHeight + ", Width >= " + minWindowWidth;
	    String provided = "Height = " + windowHeight + ", Width = " + windowWidth;

	    String result = (windowHeight.compareTo(minWindowHeight) >= 0 && windowWidth.compareTo(minWindowWidth) >= 0)
	            ? Result.Accepted.getResultVal()
	            : Result.Not_Accepted.getResultVal();

	    setReportOutputDetails(pl, "", subRuleDesc3, floor.getNumber().toString(), "-", requirement, provided, result, scrutinyDetail);
	}


	private void evaluateFirstRoomDetails(Plan pl, Floor floor, List<BigDecimal> roomAreas, List<BigDecimal> roomWidths,
			String subRule, String subRuleDesc, ScrutinyDetail scrutinyDetail) {

		if (roomAreas.size() >= 1) {
			BigDecimal minimumArea = MINIMUM_AREA_9_5;
			BigDecimal minWidth = MINIMUM_WIDTH_2_4;
			BigDecimal maxArea = MAXIMUM_AREA_46_45;

			BigDecimal area = roomAreas.get(0);
			BigDecimal width = roomWidths.get(0);

			String requirement = "Area >= " + minimumArea + ", Width >=" + minWidth;
			String provided = "Area = " + area + ", Width = " + width + DcrConstants.IN_METER;

			String result = (area.compareTo(maxArea) <= 0 && area.compareTo(minimumArea) >= 0
					&& width.compareTo(minWidth) >= 0) ? Result.Accepted.getResultVal()
							: Result.Not_Accepted.getResultVal();

			setReportOutputDetails(pl, subRule, subRuleDesc, floor.getNumber().toString(), "", requirement, provided,
					result, scrutinyDetail);
		}
	}

	private void evaluateRemainingRoomDetails(Plan pl, Floor floor, List<BigDecimal> roomAreas,
			List<BigDecimal> roomWidths, String subRule, String subRuleDesc, ScrutinyDetail scrutinyDetail) {

		BigDecimal minimumArea = MINIMUM_AREA_7_5;
		BigDecimal minWidth = MINIMUM_WIDTH_2_1;
		BigDecimal maxArea = MAXIMUM_AREA_46_45;

		for (int i = 1; i < roomAreas.size(); i++) {
			BigDecimal area = roomAreas.get(i);
			BigDecimal width = roomWidths.get(i);

			String requirement = "Area >= " + minimumArea + ", Width >=" + minWidth;
			String provided = "Area = " + area + ", Width = " + width + DcrConstants.IN_METER;

			String result = (area.compareTo(maxArea) <= 0 && area.compareTo(minimumArea) >= 0
					&& width.compareTo(minWidth) >= 0) ? Result.Accepted.getResultVal()
							: Result.Not_Accepted.getResultVal();

			setReportOutputDetails(pl, subRule, subRuleDesc, floor.getNumber().toString(), "", requirement, provided,
					result, scrutinyDetail);
		}
	}
	
	private void evaluateNonHabitationalDoors(Plan pl, Floor floor, ScrutinyDetail scrutinyDetail) {
	    if (floor.getNonaHabitationalDoors() == null || floor.getNonaHabitationalDoors().isEmpty()) return;

	    for (Door door : floor.getNonaHabitationalDoors()) {
	        if (door != null) {
	            evaluateSingleNonHabitationalDoor(pl, floor, door, scrutinyDetail);
	        }
	    }
	}

	private void evaluateSingleNonHabitationalDoor(Plan pl, Floor floor, Door door,  ScrutinyDetail scrutinyDetail) {
	    BigDecimal doorHeight = door.getNonHabitationDoorHeight().setScale(2, BigDecimal.ROUND_HALF_UP);
	    BigDecimal doorWidth = door.getNonHabitationDoorWidth().setScale(2, BigDecimal.ROUND_HALF_UP);

	    BigDecimal minDoorWidth = BigDecimal.ZERO;
	    BigDecimal minDoorHeight = BigDecimal.ZERO;

	    List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.NON_HABITATIONAL_DOORS, false);

	    Optional<MdmsFeatureRule> matchedRule = rules.stream()
	            .map(obj -> (MdmsFeatureRule) obj)
	            .findFirst();

	    if (matchedRule.isPresent()) {
	        MdmsFeatureRule rule = matchedRule.get();
	        minDoorWidth = rule.getMinDoorWidth();
	        minDoorHeight = rule.getMinDoorHeight();
	    }

		String subRuleDoor = "6.4.1";
		String subRuleDesc6 = "Door Area";

	    String requirement = "Height >= " + minDoorHeight + ", Width >= " + minDoorWidth;
	    String provided = "Height = " + doorHeight + ", Width = " + doorWidth;

	    String result = (doorHeight.compareTo(minDoorHeight) >= 0 && doorWidth.compareTo(minDoorWidth) >= 0)
	            ? Result.Accepted.getResultVal()
	            : Result.Not_Accepted.getResultVal();

	    setReportOutputDetails(pl, subRuleDoor, subRuleDesc6, floor.getNumber().toString(), "-", requirement, provided, result, scrutinyDetail);
	}


	private void processAcRooms(Floor floor, Block block, String color, OccupancyTypeHelper mostRestrictiveOccupancy,
			Map<String, Integer> heightOfRoomFeaturesColor, List<BigDecimal> roomAreas, List<BigDecimal> roomWidths,
			Plan pl, Map<String, String> errors) {

		List<BigDecimal> residentialAcRoomHeights = new ArrayList<>();
		List<RoomHeight> acHeights = new ArrayList<>();
		List<Measurement> acRooms = new ArrayList<>();

		for (Room room : floor.getAcRooms()) {
			if (room.getHeights() != null)
				acHeights.addAll(room.getHeights());
			if (room.getRooms() != null)
				acRooms.addAll(room.getRooms());
		}

		for (RoomHeight roomHeight : acHeights) {
			if (heightOfRoomFeaturesColor.get(color) == roomHeight.getColorCode()) {
				residentialAcRoomHeights.add(roomHeight.getHeight());
			}
		}

		for (Measurement acRoom : acRooms) {
			if (heightOfRoomFeaturesColor.get(color) == acRoom.getColorCode()) {
				roomAreas.add(acRoom.getArea());
				roomWidths.add(acRoom.getWidth());
			}
		}

		if (!residentialAcRoomHeights.isEmpty()) {
			BigDecimal minHeight = residentialAcRoomHeights.stream().reduce(BigDecimal::min).get();
			BigDecimal minimumHeight;

			if (!"A".equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
				minimumHeight = MINIMUM_HEIGHT_2_75;
				Log.info("Minimum Residential AC Room Height required is set to++++++ : " + MINIMUM_HEIGHT_2_75);
			} else {
				minimumHeight = MINIMUM_HEIGHT_3;
			}

			String subRule = RULE;
			String subRuleDesc = RULE_AC_DESC;

			boolean valid = false;
			boolean isTypicalRepititiveFloor = false;
			Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
					isTypicalRepititiveFloor);

			buildResult(pl, floor, minimumHeight, subRule, subRuleDesc, minHeight, valid, typicalFloorValues);

		} else {
			String layerName = String.format(LAYER_ROOM_HEIGHT, block.getNumber(), floor.getNumber(), "AC_ROOM");
			errors.put(layerName, ROOM_HEIGHT_NOTDEFINED + layerName);
			pl.addErrors(errors);
		}
	}

	private void processRegularRooms(Plan pl, Floor floor, Block block, String color, OccupancyTypeHelper mostRestrictiveOccupancy,
			Map<String, Integer> heightOfRoomFeaturesColor, ScrutinyDetail scrutinyDetail, Map<String, String> errors) {

		if (floor.getRegularRooms() == null || floor.getRegularRooms().isEmpty())
			return;

		List<BigDecimal> residentialRoomHeights = new ArrayList<>();
		List<RoomHeight> heights = new ArrayList<>();
		List<Measurement> rooms = new ArrayList<>();

		collectHeightsAndRoomsFromRegularRooms(floor, heights, rooms);
		populateRoomNumberForMeasurements(floor.getRegularRooms());

		for (RoomHeight roomHeight : heights) {
			if (heightOfRoomFeaturesColor.get(color) == roomHeight.getColorCode()) {
				residentialRoomHeights.add(roomHeight.getHeight());
			}
		}

		boolean roomArea2Satisfied = false;
		BigDecimal roomArea1 = BigDecimal.ZERO, roomArea2 = BigDecimal.ZERO;
		BigDecimal roomWidth1 = BigDecimal.ZERO, roomWidth2 = BigDecimal.ZERO;

		List<Object> rules = cache.getFeatureRules(pl, MdmsFeatureConstants.ROOM_AREA, false);
		Optional<MdmsFeatureRule> matchedRule = rules.stream().map(obj -> (MdmsFeatureRule) obj).findFirst();
		if (matchedRule.isPresent()) {
			MdmsFeatureRule rule = matchedRule.get();
			roomArea1 = rule.getRoomArea1();
			roomArea2 = rule.getRoomArea2();
			roomWidth1 = rule.getRoomWidth1();
			roomWidth2 = rule.getRoomWidth2();
		}

		for (Measurement room : rooms) {
			boolean satisfied = false;

			BigDecimal roomArea = room.getArea().setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal roomWidth = room.getWidth().setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal minArea, minWidth;

			if (!roomArea2Satisfied && roomArea.compareTo(roomArea2) >= 0 && roomWidth.compareTo(roomWidth2) >= 0) {
				roomArea2Satisfied = true;
				minArea = roomArea2;
				minWidth = roomWidth2;
			} else {
				minArea = roomArea1;
				minWidth = roomWidth1;
			}

			validateAndReportRoom(pl, floor, room, scrutinyDetail, minArea, minWidth, roomArea, roomWidth);
		}

		buildRoomHeightResult(pl, floor, block, mostRestrictiveOccupancy, residentialRoomHeights, heightOfRoomFeaturesColor, color, errors);
	}

	private void collectHeightsAndRoomsFromRegularRooms(Floor floor, List<RoomHeight> heights,
			List<Measurement> rooms) {
		for (Room room : floor.getRegularRooms()) {
			if (room.getHeights() != null)
				heights.addAll(room.getHeights());
			if (room.getRooms() != null)
				rooms.addAll(room.getRooms());
		}
	}

	private void populateRoomNumberForMeasurements(List<Room> rooms) {
		for (Room room : rooms) {
			for (Measurement m : room.getRooms()) {
				m.setRoomNumber(room.getNumber());
			}
		}
	}

	private void validateAndReportRoom(Plan pl, Floor floor, Measurement room, ScrutinyDetail scrutinyDetail,
			BigDecimal minArea, BigDecimal minWidth, BigDecimal roomArea, BigDecimal roomWidth) {

		String result = (roomArea.compareTo(minArea) >= 0 && roomWidth.compareTo(minWidth) >= 0)
				? Result.Accepted.getResultVal()
				: Result.Not_Accepted.getResultVal();

		setReportOutputDetails(pl, RULE, RULE_REGULAR_DESC, floor.getNumber().toString(),
				String.valueOf(room.getRoomNumber()), "Area >= " + minArea + ", Width >= " + minWidth,
				"Area = " + roomArea + ", Width = " + roomWidth, result, scrutinyDetail);
	}

	private void buildRoomHeightResult(Plan pl, Floor floor, Block block, OccupancyTypeHelper mostRestrictiveOccupancy, List<BigDecimal> residentialRoomHeights,
			Map<String, Integer> heightOfRoomFeaturesColor, String color, Map<String, String> errors) {

		if (!residentialRoomHeights.isEmpty()) {
			BigDecimal minHeight = residentialRoomHeights.stream().reduce(BigDecimal::min).get();

			if (A.equalsIgnoreCase(mostRestrictiveOccupancy.getType().getCode())) {
				minimumHeight = MINIMUM_HEIGHT_2_75;
				Log.info("Minimum Residential Regular Room Height required is set to-------- : " + MINIMUM_HEIGHT_2_75);
			} else {
				minimumHeight = MINIMUM_HEIGHT_3;
			}

			boolean valid = false;
			boolean isTypicalRepititiveFloor = false;
			Map<String, Object> typicalFloorValues = ProcessHelper.getTypicalFloorValues(block, floor,
					isTypicalRepititiveFloor);

			buildResult(pl, floor, minimumHeight, RULE, RULE_REGULAR_DESC, minHeight, valid, typicalFloorValues);

		} else {
			String layerName = String.format(LAYER_ROOM_HEIGHT, block.getNumber(), floor.getNumber(), "REGULAR_ROOM");
			errors.put(layerName, ROOM_HEIGHT_NOTDEFINED + layerName);
			pl.addErrors(errors);
		}
	}

	private void buildResult(Plan pl, Floor floor, BigDecimal expected, String subRule, String subRuleDesc,
			BigDecimal actual, boolean valid, Map<String, Object> typicalFloorValues) {
		if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")
				&& expected.compareTo(BigDecimal.valueOf(0)) > 0 && subRule != null && subRuleDesc != null) {
			if (actual.compareTo(expected) >= 0) {
				valid = true;
			}
			String value = typicalFloorValues.get("typicalFloors") != null
					? (String) typicalFloorValues.get("typicalFloors")
					: " floor " + floor.getNumber();
			if (valid) {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, "", expected + DcrConstants.IN_METER,
						actual + DcrConstants.IN_METER, Result.Accepted.getResultVal(), scrutinyDetail);
				LOG.info("Room Height Validation True: (Expected/Actual) " + expected + "/" + actual);
			} else {
				setReportOutputDetails(pl, subRule, subRuleDesc, value, "", expected + DcrConstants.IN_METER,
						actual + DcrConstants.IN_METER, Result.Not_Accepted.getResultVal(), scrutinyDetail);
				LOG.info("Room Height Validation False: (Expected/Actual) " + expected + "/" + actual);
			}
		}
	}

	private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String floor, String room,
			String expected, String actual, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(DESCRIPTION, ruleDesc);
		details.put(FLOOR, floor);
		details.put(Room, room);
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