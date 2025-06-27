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
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.River;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.infra.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RiverDistance extends FeatureProcess {
	private static final Logger LOG = LogManager.getLogger(RiverDistance.class);
    
    // Rule number and description constants
    private static final String RULE_22 = "22";
    public static final String MAIN_RIVER_DESCRIPTION = "Distance from main river";
    public static final String SUB_RIVER_DESCRIPTION = "Distance from sub river";
    public static final String MAIN_RIVER_PROTECTION_WALL_DESCRIPTION = "Distance from main river protection wall";
    public static final String MAIN_RIVER_EMBANKMENT_DESCRIPTION = "Distance from main river embankment";
    public static final String NO_DISTANCT_MENTIONED = "No distance is provided from protection wall embankment/river main edge or sub river";
    
    // Color codes for river types
    private static final Integer MAIN_RIVER = 1;
    private static final Integer SUB_RIVER = 2;

    // Validation method (currently returns the plan as is)
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }
    
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan process(Plan pl) {
        // Initialize scrutiny detail and table columns
        ScrutinyDetail scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_River Distance");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, PERMITTED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(5, STATUS);

        HashMap<String, String> errors = new HashMap<>();
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, RULE_22);

        // Variables to hold the minimum distances from various river elements
        BigDecimal minDistanceFromProtectionWall = BigDecimal.ZERO;
        BigDecimal minDistanceFromEmbankment = BigDecimal.ZERO;
        BigDecimal minDistanceFromMainRiverEdge = BigDecimal.ZERO;
        BigDecimal minDistanceFromSubRiver = BigDecimal.ZERO;
        
        // Variables to hold permissible distance values from MDMS
        BigDecimal rDminDistanceFromProtectionWall = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromEmbankment = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromMainRiverEdge = BigDecimal.ZERO;
        BigDecimal rDminDistanceFromSubRiver = BigDecimal.ZERO;

        // Separate rivers into main and sub rivers
        List<River> mainRiver = new ArrayList<>();
        List<River> subRiver = new ArrayList<>();
        List<River> rivers = pl.getDistanceToExternalEntity().getRivers();
        
        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);

        // Prepare parameters for rule fetching
        String feature = MdmsFeatureConstants.RIVER_DISTANCE;
        Map<String, Object> params = new HashMap<>();

        
        params.put("feature", feature);
        params.put("occupancy", occupancyName);
			
        Map<String,List<Map<String,Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
			
        // Keys for rule values to fetch
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_PROTECTION_WALL);
        valueFromColumn.add(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_EMBANKMENT);
        valueFromColumn.add(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_MAIN_RIVER_EDGE);
        valueFromColumn.add(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_SUB_RIVER);

        List<Map<String, Object>> permissibleValue = new ArrayList<>();
		
        // Fetch permissible values from MDMS
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        // Extract permissible distance values
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_PROTECTION_WALL)) {
            rDminDistanceFromProtectionWall = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_PROTECTION_WALL).toString()));
            rDminDistanceFromEmbankment = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_EMBANKMENT).toString()));
            rDminDistanceFromMainRiverEdge = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_MAIN_RIVER_EDGE).toString()));
            rDminDistanceFromSubRiver = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.RD_MIN_DISTANCE_FROM_SUB_RIVER).toString()));
        }

        // Split rivers into main and sub river lists based on color code
        if (!rivers.isEmpty()) {
            mainRiver = rivers.stream().filter(river -> river.getColorCode().equals(MAIN_RIVER))
                    .collect(Collectors.toList());
            subRiver = rivers.stream().filter(river -> river.getColorCode().equals(SUB_RIVER))
                    .collect(Collectors.toList());
        }

        // Extract distance lists for scrutiny
        List<BigDecimal> distancesFromRiverProtectionWall = !mainRiver.isEmpty()
                ? mainRiver.get(0).getDistancesFromProtectionWall()
                : new ArrayList<>();
        List<BigDecimal> distancesFromEmbankment = !mainRiver.isEmpty()
                ? mainRiver.get(0).getDistancesFromEmbankment()
                : new ArrayList<>();
        List<BigDecimal> distancesFromMainRiverEdge = !mainRiver.isEmpty()
                ? mainRiver.get(0).getDistancesFromRiverEdge()
                : new ArrayList<>();
        List<BigDecimal> distancesFromSubRiver = !subRiver.isEmpty()
                ? subRiver.get(0).getDistancesFromProtectionWall()
                : new ArrayList<>();

        // Check if the building is near the river
        if (StringUtils.isNotBlank(pl.getPlanInformation().getBuildingNearToRiver())
                && "YES".equalsIgnoreCase(pl.getPlanInformation().getBuildingNearToRiver())) {

            // Validate against protection wall distance
            if (distancesFromRiverProtectionWall != null && !distancesFromRiverProtectionWall.isEmpty()) {
                minDistanceFromProtectionWall = distancesFromRiverProtectionWall.stream().reduce(BigDecimal::min).get();

                if (minDistanceFromProtectionWall.compareTo(rDminDistanceFromProtectionWall) > 0) {
                    details.put(DESCRIPTION, MAIN_RIVER_PROTECTION_WALL_DESCRIPTION);
                    details.put(PERMITTED, ">" + rDminDistanceFromProtectionWall.toString());
                    details.put(PROVIDED, minDistanceFromProtectionWall.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    details.put(DESCRIPTION, MAIN_RIVER_PROTECTION_WALL_DESCRIPTION);
                    details.put(PERMITTED, "<=" + rDminDistanceFromProtectionWall.toString());
                    details.put(PROVIDED, minDistanceFromProtectionWall.toString());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }

            // Validate against embankment distance
            else if (distancesFromRiverProtectionWall != null && !distancesFromEmbankment.isEmpty()) {
                details = new HashMap<>();
                details.put(RULE_NO, RULE_22);

                minDistanceFromEmbankment = distancesFromEmbankment.stream().reduce(BigDecimal::min).get();

                if (minDistanceFromEmbankment.compareTo(rDminDistanceFromEmbankment) > 0) {
                    details.put(DESCRIPTION, MAIN_RIVER_EMBANKMENT_DESCRIPTION);
                    details.put(PERMITTED, ">" + rDminDistanceFromEmbankment.toString());
                    details.put(PROVIDED, minDistanceFromEmbankment.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    details.put(DESCRIPTION, MAIN_RIVER_EMBANKMENT_DESCRIPTION);
                    details.put(PERMITTED, "<=" + rDminDistanceFromEmbankment.toString());
                    details.put(PROVIDED, minDistanceFromEmbankment.toString());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }

            // Validate against main river edge distance
            else if (distancesFromMainRiverEdge != null && !distancesFromMainRiverEdge.isEmpty()) {
                details = new HashMap<>();
                details.put(RULE_NO, RULE_22);

                minDistanceFromMainRiverEdge = distancesFromMainRiverEdge.stream().reduce(BigDecimal::min).get();

                if (minDistanceFromMainRiverEdge.compareTo(rDminDistanceFromMainRiverEdge) > 0) {
                    details.put(DESCRIPTION, MAIN_RIVER_DESCRIPTION);
                    details.put(PERMITTED, ">" + rDminDistanceFromMainRiverEdge.toString());
                    details.put(PROVIDED, minDistanceFromMainRiverEdge.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    details.put(DESCRIPTION, MAIN_RIVER_DESCRIPTION);
                    details.put(PERMITTED, "<=" + rDminDistanceFromMainRiverEdge.toString());
                    details.put(PROVIDED, minDistanceFromMainRiverEdge.toString());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }

            // Validate against sub river distance
            if (distancesFromSubRiver != null && !distancesFromSubRiver.isEmpty()) {
                minDistanceFromSubRiver = distancesFromSubRiver.stream().reduce(BigDecimal::min).get();
                details = new HashMap<>();
                details.put(RULE_NO, RULE_22);

                if (minDistanceFromSubRiver.compareTo(rDminDistanceFromSubRiver) > 0) {
                    details.put(DESCRIPTION, SUB_RIVER_DESCRIPTION);
                    details.put(PERMITTED, ">" + rDminDistanceFromSubRiver.toString());
                    details.put(PROVIDED, minDistanceFromSubRiver.toString());
                    details.put(STATUS, Result.Accepted.getResultVal());
                } else {
                    details.put(DESCRIPTION, SUB_RIVER_DESCRIPTION);
                    details.put(PERMITTED, "<=" + rDminDistanceFromSubRiver.toString());
                    details.put(PROVIDED, minDistanceFromSubRiver.toString());
                    details.put(STATUS, Result.Not_Accepted.getResultVal());
                }
                scrutinyDetail.getDetail().add(details);
                pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
            }

            /*
             * If no distance values are provided, errors could be populated here.
             * This block is commented out in the original code.
             */
        }
        return pl;
    }

    // No amendments configured for this feature
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }
}