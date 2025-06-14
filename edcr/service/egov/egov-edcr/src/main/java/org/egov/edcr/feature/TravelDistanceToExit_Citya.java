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
import static org.egov.edcr.constants.DxfFileConstants.B;
import static org.egov.edcr.constants.DxfFileConstants.D;
import static org.egov.edcr.constants.DxfFileConstants.F;
import static org.egov.edcr.constants.DxfFileConstants.G;
import static org.egov.edcr.constants.DxfFileConstants.H;
import static org.egov.edcr.constants.DxfFileConstants.I;

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
import org.egov.common.entity.edcr.OccupancyTypeHelper;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.service.ProcessHelper;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TravelDistanceToExit_Citya extends FeatureProcess {

    // Logger for logging important information
    private static final Logger LOG = LogManager.getLogger(TravelDistanceToExit_Citya.class);

    // Rule identifier and description for reporting
    private static final String SUBRULE_42_2 = "42-2";
    private static final String SUBRULE_42_2_DESC = "Maximum travel distance to emergency exit";

    // Permissible travel distances fetched from MDMS
    public static BigDecimal travelDistanceToExitValueOne = BigDecimal.ZERO;
    public static BigDecimal travelDistanceToExitValueTwo = BigDecimal.ZERO;
    public static BigDecimal travelDistanceToExitValueThree = BigDecimal.ZERO;

    // Service for fetching rule data from MDMS
    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    // No validation logic implemented for this feature
    @Override
    public Plan validate(Plan pl) {
        return pl;
    }

    @Override
    public Plan process(Plan pl) {
        Boolean exemption = Boolean.FALSE;

        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
        String feature = MdmsFeatureConstants.TRAVEL_DISTANCE_TO_EXIT;

        // Determine occupancy type for rule lookup
        Map<String, Object> params = new HashMap<>();
        

        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch rules from EDCR MDMS
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_ONE);
        valueFromColumn.add(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_TWO);
        valueFromColumn.add(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_THREE);

        List<Map<String, Object>> permissibleValue = new ArrayList<>();
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue);

        // Parse permissible values from MDMS result
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_ONE)) {
            travelDistanceToExitValueOne = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_ONE).toString()));
            travelDistanceToExitValueTwo = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_TWO).toString()));
            travelDistanceToExitValueThree = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.TRAVEL_DISTANCE_TO_EXIT_VALUE_THREE).toString()));
        }

        // Exemption logic for low-rise residential buildings or small plots
        if (pl != null && pl.getVirtualBuilding() != null &&
                !pl.getVirtualBuilding().getOccupancyTypes().isEmpty() && !pl.getBlocks().isEmpty()) {

            boolean floorsAboveGroundLessThanOrEqualTo3ForAllBlks = true;

            // Check if all blocks have ≤ 3 floors
            for (Block block : pl.getBlocks()) {
                if (block.getBuilding() != null && block.getBuilding().getFloorsAboveGround() != null &&
                        block.getBuilding().getFloorsAboveGround().compareTo(travelDistanceToExitValueThree) > 0) {
                    floorsAboveGroundLessThanOrEqualTo3ForAllBlks = false;
                    break;
                }
            }

            // Grant exemption if conditions met
            if ((pl.getVirtualBuilding().getResidentialBuilding().equals(Boolean.TRUE) &&
                    floorsAboveGroundLessThanOrEqualTo3ForAllBlks == true) || (ProcessHelper.isSmallPlot(pl))) {
                exemption = Boolean.TRUE;
            }
        }

        // If not exempted, validate travel distance
        if (!exemption) {
            HashMap<String, String> errors = new HashMap<>();

            // Handle missing data case
            if (pl != null) {
                if (pl.getTravelDistancesToExit().isEmpty()) {
                    errors.put(DcrConstants.TRAVEL_DIST_EXIT,
                            edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED, new String[] {
                                    DcrConstants.TRAVEL_DIST_EXIT }, LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                    return pl;
                }
            }

            // Prepare scrutiny details for report
            String subRule = SUBRULE_42_2;
            String subRuleDesc = SUBRULE_42_2_DESC;
            scrutinyDetail = new ScrutinyDetail();
            scrutinyDetail.setKey("Common_Travel Distance To Emergency Exits");
            scrutinyDetail.addColumnHeading(1, RULE_NO);
            scrutinyDetail.addColumnHeading(2, REQUIRED);
            scrutinyDetail.addColumnHeading(3, PROVIDED);
            scrutinyDetail.addColumnHeading(4, STATUS);
            scrutinyDetail.setSubHeading(SUBRULE_42_2_DESC);

            if (pl != null && pl.getVirtualBuilding() != null) {
                // Get the occupancy type to fetch required value
                OccupancyTypeHelper mostRestrictiveFarHelper = pl.getVirtualBuilding().getMostRestrictiveFarHelper();
                String code = mostRestrictiveFarHelper.getType().getCode();

                // Get occupancy-specific permissible distance
                Map<String, BigDecimal> occupancyValues = getOccupancyValues(travelDistanceToExitValueOne, travelDistanceToExitValueTwo);
                BigDecimal requiredValue = occupancyValues.get(code);

                if (requiredValue != null) {
                    // Loop through all provided travel distances and validate
                    for (BigDecimal maximumTravelDistance : pl.getTravelDistancesToExit()) {
                        boolean valid = false;
                        if (maximumTravelDistance.compareTo(requiredValue) <= 0) {
                            valid = true;
                        }

                        // Add result to scrutiny report
                        if (valid) {
                            setReportOutputDetails(pl, subRule, requiredValue + DcrConstants.IN_METER, maximumTravelDistance +
                                    DcrConstants.IN_METER, Result.Accepted.getResultVal());
                        } else {
                            setReportOutputDetails(pl,
                                    subRule, requiredValue + DcrConstants.IN_METER, maximumTravelDistance + DcrConstants.IN_METER,
                                    Result.Not_Accepted.getResultVal());
                        }
                    }
                }
            }
        }

        return pl;
    }

    // Helper to append result details to the scrutiny report
    private void setReportOutputDetails(Plan pl, String ruleNo, String expected, String actual, String status) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    // No amendments defined for this feature
    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

    // Mapping occupancy types to their respective travel distance limits
    public Map<String, BigDecimal> getOccupancyValues(BigDecimal valueOne, BigDecimal valueTwo) {

        Map<String, BigDecimal> roadWidthValues = new HashMap<>();

        roadWidthValues.put(D, valueOne);
        roadWidthValues.put(G, valueOne);
        roadWidthValues.put(F, valueOne);
        roadWidthValues.put(H, valueOne);

        roadWidthValues.put(A, valueTwo);
        roadWidthValues.put(I, valueTwo);
        roadWidthValues.put(B, valueTwo);

        return roadWidthValues;
    }
}