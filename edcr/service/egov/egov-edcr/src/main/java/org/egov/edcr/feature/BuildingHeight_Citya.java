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

import static org.egov.edcr.utility.DcrConstants.BUILDING_HEIGHT;
import static org.egov.edcr.utility.DcrConstants.DECIMALDIGITS_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.HEIGHT_OF_BUILDING;
import static org.egov.edcr.utility.DcrConstants.OBJECTNOTDEFINED;
import static org.egov.edcr.utility.DcrConstants.ROUNDMODE_MEASUREMENTS;
import static org.egov.edcr.utility.DcrConstants.SECURITY_ZONE;
import static org.egov.edcr.utility.DcrConstants.SHORTESTDISTINACETOBUILDINGFOOTPRINT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.egov.common.entity.edcr.Block;
import org.egov.common.entity.edcr.CulDeSacRoad;
import org.egov.common.entity.edcr.Lane;
import org.egov.common.entity.edcr.NonNotifiedRoad;
import org.egov.common.entity.edcr.NotifiedRoad;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.springframework.stereotype.Service;

@Service
public class BuildingHeight_Citya extends FeatureProcess {

    // Constants for rule keys and other configurations
    private static final String RULE_EXPECTED_KEY = "buildingheight.expected";
    private static final String RULE_ACTUAL_KEY = "buildingheight.actual";
    private static final String SECURITYZONE_RULE_EXPECTED_KEY = "securityzone.expected";
    private static final String SECURITYZONE_RULE_ACTUAL_KEY = "securityzone.actual";

    private static final String SUB_RULE_32_1A = "32-1A";
    private static final String SUB_RULE_32_3 = "32-3";
    public static final String UPTO = "Up To";
    public static final String DECLARED = "Declared";
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    private static final BigDecimal TEN = BigDecimal.valueOf(10);

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
     * Processes the given plan to validate building height and security zone rules.
     * Currently, the method is not fully implemented.
     *
     * @param Plan The plan object to process.
     * @return The processed plan object.
     */
    @Override
    public Plan process(Plan Plan) {
        return Plan;
    }

    /**
     * Checks the height of buildings in the plan against the permissible limits.
     * Validates the height based on the distance from the road and setbacks.
     *
     * @param Plan The plan object containing building details.
     */
    private void checkBuildingHeight(Plan Plan) {
        String subRule = SUB_RULE_32_1A;
        String rule = HEIGHT_OF_BUILDING;

        BigDecimal maximumDistanceToRoad = BigDecimal.ZERO;

        // Get the maximum distance from the road
        maximumDistanceToRoad = getMaximimShortestdistanceFromRoad(Plan, maximumDistanceToRoad);

        // Iterate through all blocks in the plan to validate building height
        for (Block block : Plan.getBlocks()) {
            BigDecimal maximumDistanceToRoadEdge = BigDecimal.ZERO;
            BigDecimal maximumSetBackToBuildingLine = BigDecimal.ZERO;
            BigDecimal exptectedDistance = BigDecimal.ZERO;
            BigDecimal actualDistance = BigDecimal.ZERO;

            // Get the maximum distance to the road edge
            maximumDistanceToRoadEdge = getMaximumDistanceFromRoadEdge(maximumDistanceToRoadEdge, block);
            maximumSetBackToBuildingLine = getMaximumDistanceFromSetBackToBuildingLine(maximumSetBackToBuildingLine, block);
            actualDistance = block.getBuilding().getBuildingHeight();

            if (maximumDistanceToRoadEdge != null) {
                if (maximumDistanceToRoad.compareTo(TWELVE) <= 0) {
                    if (maximumSetBackToBuildingLine != null && maximumSetBackToBuildingLine.compareTo(BigDecimal.ZERO) > 0) {
                        exptectedDistance = maximumDistanceToRoadEdge
                                .multiply(BigDecimal.valueOf(2))
                                .add(BigDecimal.valueOf(3).multiply(maximumSetBackToBuildingLine
                                        .divide(BigDecimal.valueOf(0.5), 0, RoundingMode.DOWN)))
                                .setScale(DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);
                    } else {
                        exptectedDistance = maximumDistanceToRoadEdge.multiply(BigDecimal.valueOf(2))
                                .setScale(DECIMALDIGITS_MEASUREMENTS, ROUNDMODE_MEASUREMENTS);
                    }
                }
            }

            // Validate the height for each block
            if (exptectedDistance.compareTo(BigDecimal.ZERO) > 0) {
                String actualResult = getLocaleMessage(RULE_ACTUAL_KEY, actualDistance.toString());
                String expectedResult = getLocaleMessage(RULE_EXPECTED_KEY, exptectedDistance.toString());

                Map<String, String> details = new HashMap<>();
                details.put(RULE_NO, subRule);
                details.put(DESCRIPTION, HEIGHT_OF_BUILDING + " for Block " + block.getNumber());
                details.put(UPTO, expectedResult);
                details.put(PROVIDED, actualResult);

                if (actualDistance.compareTo(exptectedDistance) > 0) {
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                } else {
                    details.put(STATUS, Result.Verify.getResultVal());
                }

                scrutinyDetail.getDetail().add(details);
                Plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }
        }
    }

    /**
     * Checks if the buildings in the plan are within the permissible height for security zones.
     *
     * @param Plan The plan object containing building details.
     */
    private void checkBuildingInSecurityZoneArea(Plan Plan) {
        if (Plan.getPlanInformation().getSecurityZone()) {
            BigDecimal maxBuildingHeight = BigDecimal.ZERO;

            // Get the maximum building height in the plan
            for (Block block : Plan.getBlocks()) {
                if (maxBuildingHeight.compareTo(BigDecimal.ZERO) == 0 ||
                        block.getBuilding().getBuildingHeight().compareTo(maxBuildingHeight) >= 0) {
                    maxBuildingHeight = block.getBuilding().getBuildingHeight();
                }
            }

            if (maxBuildingHeight.compareTo(BigDecimal.ZERO) > 0) {
                scrutinyDetail = new ScrutinyDetail();
                scrutinyDetail.setKey("Common_Security Zone");
                scrutinyDetail.addColumnHeading(1, RULE_NO);
                scrutinyDetail.addColumnHeading(2, DESCRIPTION);
                scrutinyDetail.addColumnHeading(3, REQUIRED);
                scrutinyDetail.addColumnHeading(4, PROVIDED);
                scrutinyDetail.addColumnHeading(5, STATUS);

                String actualResult = getLocaleMessage(SECURITYZONE_RULE_ACTUAL_KEY, maxBuildingHeight.toString());
                String expectedResult = getLocaleMessage(SECURITYZONE_RULE_EXPECTED_KEY, TEN.toString());

                Map<String, String> details = new HashMap<>();
                details.put(RULE_NO, SUB_RULE_32_3);
                details.put(DESCRIPTION, SECURITY_ZONE);
                details.put(REQUIRED, expectedResult);
                details.put(PROVIDED, actualResult);

                if (maxBuildingHeight.compareTo(TEN) <= 0) {
                    details.put(STATUS, Result.Verify.getResultVal());
                } else {
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }

                scrutinyDetail.getDetail().add(details);
                Plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }
        } else {
            scrutinyDetail = new ScrutinyDetail();
            scrutinyDetail.setKey("Common_Security Zone");
            scrutinyDetail.addColumnHeading(1, RULE_NO);
            scrutinyDetail.addColumnHeading(2, DESCRIPTION);
            scrutinyDetail.addColumnHeading(3, DECLARED);
            scrutinyDetail.addColumnHeading(4, STATUS);

            Map<String, String> details = new HashMap<>();
            details.put(RULE_NO, SUB_RULE_32_3);
            details.put(DESCRIPTION, SECURITY_ZONE);
            details.put(DECLARED, "No");
            details.put(STATUS, Result.Verify.getResultVal());

            scrutinyDetail.getDetail().add(details);
            Plan.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
        }
    }

    /**
     * Gets the maximum distance from the road edge for a given block.
     *
     * @param maximumDistanceToRoadEdge The current maximum distance.
     * @param block                     The block to check.
     * @return The updated maximum distance.
     */
    private BigDecimal getMaximumDistanceFromRoadEdge(BigDecimal maximumDistanceToRoadEdge, Block block) {
        if (block.getBuilding().getDistanceFromBuildingFootPrintToRoadEnd() != null) {
            for (BigDecimal distanceFromroadEnd : block.getBuilding().getDistanceFromBuildingFootPrintToRoadEnd()) {
                if (distanceFromroadEnd.compareTo(maximumDistanceToRoadEdge) > 0) {
                    maximumDistanceToRoadEdge = distanceFromroadEnd;
                }
            }
        }
        return maximumDistanceToRoadEdge;
    }

    /**
     * Gets the maximum distance from the setback to the building line for a given block.
     *
     * @param distanceFromSetbackToBuildingLine The current maximum distance.
     * @param block                             The block to check.
     * @return The updated maximum distance.
     */
    private BigDecimal getMaximumDistanceFromSetBackToBuildingLine(BigDecimal distanceFromSetbackToBuildingLine, Block block) {
        if (block.getBuilding().getDistanceFromSetBackToBuildingLine() != null) {
            for (BigDecimal distance : block.getBuilding().getDistanceFromSetBackToBuildingLine()) {
                if (distance.compareTo(distanceFromSetbackToBuildingLine) > 0) {
                    distanceFromSetbackToBuildingLine = distance;
                }
            }
        }
        return distanceFromSetbackToBuildingLine;
    }

    /**
     * Gets the maximum shortest distance from the road for the given plan.
     *
     * @param Plan                   The plan object.
     * @param maximumDistanceToRoad  The current maximum distance.
     * @return The updated maximum distance.
     */
    private BigDecimal getMaximimShortestdistanceFromRoad(Plan Plan, BigDecimal maximumDistanceToRoad) {
        if (Plan.getNonNotifiedRoads() != null) {
            for (NonNotifiedRoad nonnotifiedRoad : Plan.getNonNotifiedRoads()) {
                for (BigDecimal shortDistance : nonnotifiedRoad.getShortestDistanceToRoad()) {
                    if (shortDistance.compareTo(maximumDistanceToRoad) > 0) {
                        maximumDistanceToRoad = shortDistance;
                    }
                }
            }
        }
        if (Plan.getNotifiedRoads() != null) {
            for (NotifiedRoad notifiedRoad : Plan.getNotifiedRoads()) {
                for (BigDecimal shortDistance : notifiedRoad.getShortestDistanceToRoad()) {
                    if (shortDistance.compareTo(maximumDistanceToRoad) > 0) {
                        maximumDistanceToRoad = shortDistance;
                    }
                }
            }
        }
        if (Plan.getCuldeSacRoads() != null) {
            for (CulDeSacRoad culdRoad : Plan.getCuldeSacRoads()) {
                for (BigDecimal shortDistance : culdRoad.getShortestDistanceToRoad()) {
                    if (shortDistance.compareTo(maximumDistanceToRoad) > 0) {
                        maximumDistanceToRoad = shortDistance;
                    }
                }
            }
        }
        if (Plan.getLaneRoads() != null) {
            for (Lane lane : Plan.getLaneRoads()) {
                for (BigDecimal shortDistance : lane.getShortestDistanceToRoad()) {
                    if (shortDistance.compareTo(maximumDistanceToRoad) > 0) {
                        maximumDistanceToRoad = shortDistance;
                    }
                }
            }
        }
        return maximumDistanceToRoad;
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