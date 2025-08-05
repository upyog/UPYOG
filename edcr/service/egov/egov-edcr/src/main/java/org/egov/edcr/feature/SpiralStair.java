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

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.entity.edcr.*;
import org.egov.edcr.service.MDMSCacheManager;
import org.egov.edcr.utility.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.egov.edcr.constants.CommonFeatureConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.*;
import static org.egov.edcr.constants.CommonKeyConstants.BLOCK;
import static org.egov.edcr.constants.EdcrReportConstants.*;
import static org.egov.edcr.service.FeatureUtil.addScrutinyDetailtoPlan;
import static org.egov.edcr.service.FeatureUtil.mapReportDetails;

@Service
public class SpiralStair extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(SpiralStair.class);

	@Autowired
	MDMSCacheManager cache;

	/**
	 * Validates the building plan for spiral stair requirements.
	 * Currently performs no validation and returns the plan as-is.
	 *
	 * @param pl The building plan to validate
	 * @return The unmodified plan
	 */
	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	/**
	 * Processes spiral stair requirements for all blocks in the building plan.
	 * Fetches spiral stair rules from MDMS cache, validates diameter requirements,
	 * checks building height restrictions, and generates scrutiny details for compliance.
	 *
	 * @param plan The building plan to process
	 * @return The processed plan with scrutiny details added
	 */
	@Override
	public Plan process(Plan plan) {
	    BigDecimal expectedDiameter = BigDecimal.ZERO;
	    BigDecimal radiusMultiplier = BigDecimal.ZERO;
	    BigDecimal maxBuildingHeightForSpiral = BigDecimal.ZERO;

	    List<Object> rules = cache.getFeatureRules(plan, FeatureEnum.SPIRAL_STAIR.getValue(), false);
        Optional<SpiralStairRequirement> matchedRule = rules.stream()
            .filter(SpiralStairRequirement.class::isInstance)
            .map(SpiralStairRequirement.class::cast)
            .findFirst();
	    if (matchedRule.isPresent()) {
	    	SpiralStairRequirement rule = matchedRule.get();
	        expectedDiameter = rule.getSpiralStairExpectedDiameter();
	        radiusMultiplier = rule.getSpiralStairRadius();
	        maxBuildingHeightForSpiral = rule.getSpiralStairValue();
	    }

	    for (Block block : plan.getBlocks()) {
	        if (block.getBuilding() == null || block.getBuilding().getOccupancies().isEmpty()) {
	            continue;
	        }

	        ScrutinyDetail scrutinyDetail = initializeScrutinyDetailForSpiralStair(block);

	        for (Floor floor : block.getBuilding().getFloors()) {
	            Map<String, Object> typicalFloorValues = Util.getTypicalFloorValues(block, floor, false);
	            List<org.egov.common.entity.edcr.SpiralStair> spiralStairs = floor.getSpiralStairs();

	            if (spiralStairs.isEmpty()) continue;

	            boolean isTypicalFloor = (Boolean) typicalFloorValues.get(IS_TYPICAL_REP_FLOOR);
	            if (isTypicalFloor) continue;

	            String floorLabel = typicalFloorValues.get(TYPICAL_FLOOR) != null
	                              ? (String) typicalFloorValues.get(TYPICAL_FLOOR)
	                              : FLOOR_SPACED + floor.getNumber();

	            for (org.egov.common.entity.edcr.SpiralStair stair : spiralStairs) {
	                List<Circle> circles = stair.getCircles();
	                if (circles.isEmpty()) continue;

	                BigDecimal buildingHeight = Util.roundOffTwoDecimal(block.getBuilding().getBuildingHeight());
	                if (buildingHeight.compareTo(Util.roundOffTwoDecimal(maxBuildingHeightForSpiral)) > 0) {
	                    setReportOutputDetailsFloorStairWise(plan, RULE42_5_IV, floorLabel,
	                            stair.getNumber(), EMPTY_STRING,
	                            SPIRAL_STAIR_NOT_ALLOWED
	                                    + block.getNumber() + SINGLE_SPACE_STRING + floorLabel,
	                            Result.Not_Accepted.getResultVal(), scrutinyDetail);
	                    continue;
	                }

	                Circle minCircle = circles.stream().min(Comparator.comparing(Circle::getRadius)).get();
	                BigDecimal radius = Util.roundOffTwoDecimal(minCircle.getRadius());
	                BigDecimal diameter = Util.roundOffTwoDecimal(radius.multiply(Util.roundOffTwoDecimal(radiusMultiplier)));
	                BigDecimal minDiameter = Util.roundOffTwoDecimal(expectedDiameter);

	                String result = diameter.compareTo(minDiameter) >= 0
	                        ? Result.Accepted.getResultVal()
	                        : Result.Not_Accepted.getResultVal();

	                setReportOutputDetailsFloorStairWise(plan, RULE42_5_IV, floorLabel,
	                        String.format(DIAMETER_DESCRIPTION, stair.getNumber()),
	                        expectedDiameter.toString(), diameter.toString(),
	                        result, scrutinyDetail);
	            }
	        }
	    }

	    return plan;
	}

	/**
	 * Initializes scrutiny detail object for spiral stair validation reporting.
	 * Sets up column headings and key for the specific block being processed.
	 *
	 * @param block The building block for which scrutiny detail is being initialized
	 * @return Configured ScrutinyDetail object with appropriate headings and key
	 */
	private ScrutinyDetail initializeScrutinyDetailForSpiralStair(Block block) {
	    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, FLOOR);
	    scrutinyDetail.addColumnHeading(3, DESCRIPTION);
	    scrutinyDetail.addColumnHeading(4, REQUIRED);
	    scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
	    scrutinyDetail.addColumnHeading(6, STATUS);
	    scrutinyDetail.setKey(BLOCK + block.getNumber() + SPIRAL_FIRE_STAIR_SUFFIX);
	    return scrutinyDetail;
	}

	/**
	 * Adds a single entry to the scrutiny detail for spiral stair validation results.
	 * Creates a detailed report entry with rule information, floor details, requirements,
	 * and compliance status.
	 *
	 * @param pl The building plan
	 * @param ruleNo The rule number being validated
	 * @param floor The floor identifier
	 * @param description Description of the requirement being checked
	 * @param expected The expected/required value
	 * @param actual The actual/provided value
	 * @param status The compliance status (Accepted/Not_Accepted)
	 * @param scrutinyDetail The scrutiny detail object to add the entry to
	 */
	// Method to add one entry in scrutiny detail
	private void setReportOutputDetailsFloorStairWise(Plan pl, String ruleNo, String floor, String description,
			String expected, String actual, String status, ScrutinyDetail scrutinyDetail) {
		ReportScrutinyDetail detail = new ReportScrutinyDetail();
		detail.setRuleNo(ruleNo);
		detail.setDescription(description);
		detail.setFloorNo(floor);
		detail.setPermissible(actual);
		detail.setRequired(expected);
		detail.setStatus(status);

		Map<String, String> details = mapReportDetails(detail);
		addScrutinyDetailtoPlan(scrutinyDetail, pl, details);
	}

	/**
	 * Returns amendment dates for spiral stair rules.
	 * Currently returns an empty map as no amendments are defined.
	 *
	 * @return Empty LinkedHashMap of amendment dates
	 */
	@Override
	public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
	}

}
