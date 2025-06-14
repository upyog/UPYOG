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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Circle;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpiralStair extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(SpiralStair.class);
	private static final String FLOOR = "Floor";
	private static final String RULE42_5_IV = "42-5-iv";
	private static final String DIAMETER_DESCRIPTION = "Minimum diameter for spiral fire stair %s";

	@Autowired
	FetchEdcrRulesMdms fetchEdcrRulesMdms;
	
	@Override
	public Plan process(Plan plan) {
    	// Initialize default values
    	BigDecimal spiralStairExpectedDiameter = BigDecimal.ZERO;
    	BigDecimal spiralStairRadius = BigDecimal.ZERO;
    	BigDecimal spiralStairValue = BigDecimal.ZERO;

    	String occupancyName = fetchEdcrRulesMdms.getOccupancyName(plan);
		
		// Set feature name
		String feature = MdmsFeatureConstants.SPIRAL_STAIR;
			
		Map<String, Object> params = new HashMap<>();
		
		// Determine occupancy type (currently only "Residential" supported)
		
		params.put("feature", feature);
		params.put("occupancy", occupancyName);
			
		Map<String,List<Map<String,Object>>> edcrRuleList = plan.getEdcrRulesFeatures();
			
		// MDMS columns to fetch
		ArrayList<String> valueFromColumn = new ArrayList<>();
		valueFromColumn.add(EdcrRulesMdmsConstants.SPIRAL_STAIR_EXPECTED_DIAMETER);
		valueFromColumn.add(EdcrRulesMdmsConstants.SPIRAL_STAIR_RADIUS);
		valueFromColumn.add(EdcrRulesMdmsConstants.SPIRAL_STAIR_VALUE);

		List<Map<String, Object>> permissibleValue = new ArrayList<>();
		
		// Fetch values from MDMS
		permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
		LOG.info("permissibleValue" + permissibleValue);

		// Extract values if available
		if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.SPIRAL_STAIR_EXPECTED_DIAMETER)) {
			spiralStairExpectedDiameter = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SPIRAL_STAIR_EXPECTED_DIAMETER).toString()));
			spiralStairRadius = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SPIRAL_STAIR_RADIUS).toString()));
			spiralStairValue = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.SPIRAL_STAIR_VALUE).toString()));
		}
		
		// Iterate through all blocks
		blk: for (Block block : plan.getBlocks()) {
			if (block.getBuilding() != null && !block.getBuilding().getOccupancies().isEmpty()) {

				// Initialize scrutiny detail report
				ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
				scrutinyDetail.addColumnHeading(1, RULE_NO);
				scrutinyDetail.addColumnHeading(2, FLOOR);
				scrutinyDetail.addColumnHeading(3, DESCRIPTION);
				scrutinyDetail.addColumnHeading(4, REQUIRED);
				scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
				scrutinyDetail.addColumnHeading(6, STATUS);
				scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Spiral Fire Stair");

				List<Floor> floors = block.getBuilding().getFloors();

				// Process each floor
				for (Floor floor : floors) {
					boolean isTypicalRepititiveFloor = false;

					// Get typical floor values for reporting
					Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor, isTypicalRepititiveFloor);

					List<org.egov.common.entity.edcr.SpiralStair> spiralStairs = floor.getSpiralStairs();

					if (spiralStairs.size() != 0) {
						boolean valid = false;

						// Check each spiral stair in this floor
						for (org.egov.common.entity.edcr.SpiralStair spiralStair : spiralStairs) {
							List<Circle> spiralPolyLines = spiralStair.getCircles();

							// Skip check for typical repetitive floor
							if (!(Boolean) typicalFloorValues.get("isTypicalRepititiveFloor")) {

								// If building height exceeds limit, spiral stair is not valid
								if (Util.roundOffTwoDecimal(block.getBuilding().getBuildingHeight())
										.compareTo(Util.roundOffTwoDecimal(spiralStairValue)) > 0
										&& !spiralPolyLines.isEmpty()) {
									valid = true;
								}

								// Get floor label for reporting
								String value = typicalFloorValues.get("typicalFloors") != null
										? (String) typicalFloorValues.get("typicalFloors")
										: " floor " + floor.getNumber();

								if (valid) {
									// Report spiral stair as not allowed
									setReportOutputDetailsFloorStairWise(plan, RULE42_5_IV, value,
											spiralStair.getNumber(), "",
											"spiral stair of fire stair not allowed for building with height > 9 for block "
													+ block.getNumber() + " " + value,
											Result.Not_Accepted.getResultVal(), scrutinyDetail);
								} else {
									// Check spiral stair diameter
									if (!spiralPolyLines.isEmpty()) {
										// Find spiral with minimum radius
										Circle minSpiralStair = spiralPolyLines.stream()
												.min(Comparator.comparing(Circle::getRadius)).get();

										BigDecimal minRadius = minSpiralStair.getRadius();

										BigDecimal radius = Util.roundOffTwoDecimal(minRadius);

										// Diameter = radius * multiplier
										BigDecimal diameter = Util.roundOffTwoDecimal(
												radius.multiply(Util.roundOffTwoDecimal(spiralStairRadius)));

										BigDecimal minDiameter = Util.roundOffTwoDecimal(spiralStairExpectedDiameter);

										if (diameter.compareTo(minDiameter) >= 0) {
											// Diameter is acceptable
											setReportOutputDetailsFloorStairWise(plan, RULE42_5_IV, value,
													String.format(DIAMETER_DESCRIPTION, spiralStair.getNumber()),
													spiralStairExpectedDiameter.toString(), String.valueOf(diameter),
													Result.Accepted.getResultVal(), scrutinyDetail);
										} else {
											// Diameter is not acceptable
											setReportOutputDetailsFloorStairWise(plan, RULE42_5_IV, value,
													String.format(DIAMETER_DESCRIPTION, spiralStair.getNumber()),
													spiralStairExpectedDiameter.toString(), String.valueOf(diameter),
													Result.Not_Accepted.getResultVal(), scrutinyDetail);
										}
									}
								}

							}
						}
					}
				}
			}
		}
		return plan;
	}

	// Method to add one entry in scrutiny detail
	private void setReportOutputDetailsFloorStairWise(Plan pl, String ruleNo, String floor, String description,
			String expected, String actual, String status, ScrutinyDetail scrutinyDetail) {
		Map<String, String> details = new HashMap<>();
		details.put(RULE_NO, ruleNo);
		details.put(FLOOR, floor);
		details.put(DESCRIPTION, description);
		details.put(REQUIRED, expected);
		details.put(PERMISSIBLE, actual);
		details.put(STATUS, status);
		scrutinyDetail.getDetail().add(details);
		pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
	}

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Override
	public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
	}

}
