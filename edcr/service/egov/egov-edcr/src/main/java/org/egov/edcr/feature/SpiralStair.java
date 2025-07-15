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
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.Circle;
import org.egov.common.entity.edcr.Floor;
import org.egov.common.entity.edcr.MdmsFeatureRule;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.RuleKey;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.CacheManagerMdms;
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
	CacheManagerMdms cache;
	

	@Override
	public Plan validate(Plan pl) {
		return pl;
	}

	@Override
	public Plan process(Plan plan) {
	    BigDecimal expectedDiameter = BigDecimal.ZERO;
	    BigDecimal radiusMultiplier = BigDecimal.ZERO;
	    BigDecimal maxBuildingHeightForSpiral = BigDecimal.ZERO;

	    Optional<MdmsFeatureRule> matchedRule = cache.getFeatureRules(plan, MdmsFeatureConstants.SPIRAL_STAIR, false)
	                                                 .stream()
	                                                 .map(obj -> (MdmsFeatureRule) obj)
	                                                 .findFirst();

	    if (matchedRule.isPresent()) {
	        MdmsFeatureRule rule = matchedRule.get();
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

	            boolean isTypicalFloor = (Boolean) typicalFloorValues.get("isTypicalRepititiveFloor");
	            if (isTypicalFloor) continue;

	            String floorLabel = typicalFloorValues.get("typicalFloors") != null
	                              ? (String) typicalFloorValues.get("typicalFloors")
	                              : " floor " + floor.getNumber();

	            for (org.egov.common.entity.edcr.SpiralStair stair : spiralStairs) {
	                List<Circle> circles = stair.getCircles();
	                if (circles.isEmpty()) continue;

	                BigDecimal buildingHeight = Util.roundOffTwoDecimal(block.getBuilding().getBuildingHeight());
	                if (buildingHeight.compareTo(Util.roundOffTwoDecimal(maxBuildingHeightForSpiral)) > 0) {
	                    setReportOutputDetailsFloorStairWise(plan, RULE42_5_IV, floorLabel,
	                            stair.getNumber(), "",
	                            "spiral stair of fire stair not allowed for building with height > 9 for block "
	                                    + block.getNumber() + " " + floorLabel,
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

	
	private ScrutinyDetail initializeScrutinyDetailForSpiralStair(Block block) {
	    ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
	    scrutinyDetail.addColumnHeading(1, RULE_NO);
	    scrutinyDetail.addColumnHeading(2, FLOOR);
	    scrutinyDetail.addColumnHeading(3, DESCRIPTION);
	    scrutinyDetail.addColumnHeading(4, REQUIRED);
	    scrutinyDetail.addColumnHeading(5, PERMISSIBLE);
	    scrutinyDetail.addColumnHeading(6, STATUS);
	    scrutinyDetail.setKey("Block_" + block.getNumber() + "_" + "Spiral Fire Stair");
	    return scrutinyDetail;
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
	public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
	}

}
