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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.egov.common.constants.MdmsFeatureConstants;
import org.egov.common.entity.edcr.ElectricLine;
import org.egov.common.entity.edcr.Plan;
import org.egov.common.entity.edcr.Result;
import org.egov.common.entity.edcr.ScrutinyDetail;
import org.egov.edcr.constants.DxfFileConstants;
import org.egov.edcr.constants.EdcrRulesMdmsConstants;
import org.egov.edcr.entity.blackbox.PlanDetail;
import org.egov.edcr.service.FetchEdcrRulesMdms;
import org.egov.edcr.utility.DcrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class OverheadElectricalLineService_Citya extends FeatureProcess {
    private static final Logger LOG = LogManager.getLogger(OverheadElectricalLineService_Citya.class);
    private static final String SUB_RULE_31 = "31";

    private static BigDecimal overheadVerticalDistance_11000 = BigDecimal.ZERO;
    private static BigDecimal overheadVerticalDistance_33000 = BigDecimal.ZERO;
    private static BigDecimal overheadHorizontalDistance_11000 = BigDecimal.ZERO;
    private static BigDecimal overheadHorizontalDistance_33000 = BigDecimal.ZERO;
    private static BigDecimal overheadVoltage_11000 = BigDecimal.ZERO;
    private static BigDecimal overheadVoltage_33000 = BigDecimal.ZERO;
    private static final String REMARKS = "Remarks";
    private static final String VOLTAGE = "Voltage";

    @Autowired
    FetchEdcrRulesMdms fetchEdcrRulesMdms;

    @Override
    public Plan validate(Plan pl) {
        HashMap<String, String> errors = new HashMap<>();
        for (ElectricLine electricalLine : pl.getElectricLine()) {
            if (electricalLine.getPresentInDxf()) {
                if (electricalLine.getVoltage() == null) {
                    errors.put(DcrConstants.VOLTAGE,
                            edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                    new String[]{DcrConstants.VOLTAGE}, LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                }
                if (electricalLine.getVoltage() != null && (electricalLine.getHorizontalDistance() == null
                        && electricalLine.getVerticalDistance() == null)) {
                    errors.put(DcrConstants.ELECTRICLINE_DISTANCE,
                            edcrMessageSource.getMessage(DcrConstants.OBJECTNOTDEFINED,
                                    new String[]{DcrConstants.ELECTRICLINE_DISTANCE}, LocaleContextHolder.getLocale()));
                    pl.addErrors(errors);
                }
            }
        }
        return pl;
    }

    @Override
    public Plan process(Plan pl) {
        // Validate the plan for overhead electrical line compliance
        validate(pl);

        // Initialize scrutiny details for the report
        scrutinyDetail = new ScrutinyDetail();
        scrutinyDetail.setKey("Common_OverHead Electric Line");
        scrutinyDetail.addColumnHeading(1, RULE_NO);
        scrutinyDetail.addColumnHeading(2, DESCRIPTION);
        scrutinyDetail.addColumnHeading(3, REQUIRED);
        scrutinyDetail.addColumnHeading(4, PROVIDED);
        scrutinyDetail.addColumnHeading(6, VOLTAGE);
        scrutinyDetail.addColumnHeading(7, REMARKS);
        scrutinyDetail.addColumnHeading(8, STATUS);

        // Determine the occupancy type and feature for fetching permissible values
        String occupancyName = fetchEdcrRulesMdms.getOccupancyName(pl);
        String feature = MdmsFeatureConstants.OVERHEAD_ELECTRICAL_LINE_SERVICE;

        Map<String, Object> params = new HashMap<>();
       

        // Add feature and occupancy to the parameters map
        params.put("feature", feature);
        params.put("occupancy", occupancyName);

        // Fetch the list of rules from the plan object
        Map<String, List<Map<String, Object>>> edcrRuleList = pl.getEdcrRulesFeatures();

        // Specify the columns to fetch from the rules
        ArrayList<String> valueFromColumn = new ArrayList<>();
        valueFromColumn.add(EdcrRulesMdmsConstants.OVERHEAD_VERTICAL_DISTANCE_11000);
        valueFromColumn.add(EdcrRulesMdmsConstants.OVERHEAD_VERTICAL_DISTANCE_33000);
        valueFromColumn.add(EdcrRulesMdmsConstants.OVERHEAD_HORIZONTAL_DISTANCE_11000);
        valueFromColumn.add(EdcrRulesMdmsConstants.OVERHEAD_HORIZONTAL_DISTANCE_33000);
        valueFromColumn.add(EdcrRulesMdmsConstants.OVERHEAD_VOLTAGE_11000);
        valueFromColumn.add(EdcrRulesMdmsConstants.OVERHEAD_VOLTAGE_33000);

        // Initialize a list to store permissible values
        List<Map<String, Object>> permissibleValue = new ArrayList<>();

        // Fetch permissible values from MDMS
        permissibleValue = fetchEdcrRulesMdms.getPermissibleValue(edcrRuleList, params, valueFromColumn);
        LOG.info("permissibleValue" + permissibleValue); // Log the fetched permissible values

        // Check if permissible values are available and update the class-level variables
        if (!permissibleValue.isEmpty() && permissibleValue.get(0).containsKey(EdcrRulesMdmsConstants.OVERHEAD_VERTICAL_DISTANCE_11000)) {
            overheadVerticalDistance_11000 = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.OVERHEAD_VERTICAL_DISTANCE_11000).toString()));
            overheadVerticalDistance_33000 = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.OVERHEAD_VERTICAL_DISTANCE_33000).toString()));
            overheadHorizontalDistance_11000 = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.OVERHEAD_HORIZONTAL_DISTANCE_11000).toString()));
            overheadHorizontalDistance_33000 = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.OVERHEAD_HORIZONTAL_DISTANCE_33000).toString()));
            overheadVoltage_11000 = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.OVERHEAD_VOLTAGE_11000).toString()));
            overheadVoltage_33000 = BigDecimal.valueOf(Double.valueOf(permissibleValue.get(0).get(EdcrRulesMdmsConstants.OVERHEAD_VOLTAGE_33000).toString()));
        }

        // Iterate through all electric lines in the plan
        for (ElectricLine electricalLine : pl.getElectricLine()) {
            if (electricalLine.getPresentInDxf()) {
                // Check if voltage and distances are provided
                if (electricalLine.getVoltage() != null && electricalLine.getVoltage().compareTo(BigDecimal.ZERO) > 0
                        && (electricalLine.getHorizontalDistance() != null || electricalLine.getVerticalDistance() != null)) {

                    boolean horizontalDistancePassed = false;

                    // Validate horizontal distance
                    if (electricalLine.getHorizontalDistance() != null) {
                        String expectedResult = "";
                        String actualResult = electricalLine.getHorizontalDistance().toString() + DcrConstants.IN_METER;

                        // Check horizontal distance based on voltage
                        if (electricalLine.getVoltage().compareTo(overheadVoltage_11000) < 0) {
                            expectedResult = overheadHorizontalDistance_11000.toString() + DcrConstants.IN_METER;
                            if (electricalLine.getHorizontalDistance().compareTo(overheadHorizontalDistance_11000) >= 0)
                                horizontalDistancePassed = true;

                        } else if (electricalLine.getVoltage().compareTo(overheadVoltage_11000) >= 0
                                && electricalLine.getVoltage().compareTo(overheadVoltage_33000) <= 0) {
                            expectedResult = overheadHorizontalDistance_33000.toString() + DcrConstants.IN_METER;
                            if (electricalLine.getHorizontalDistance().compareTo(overheadHorizontalDistance_33000) >= 0)
                                horizontalDistancePassed = true;

                        } else if (electricalLine.getVoltage().compareTo(overheadVoltage_33000) > 0) {
                            Double totalHorizontalOHE = overheadHorizontalDistance_33000.doubleValue() + 0.3 *
                                    Math.ceil(electricalLine.getVoltage().subtract(overheadVoltage_33000)
                                            .divide(overheadVoltage_33000, 2, RoundingMode.HALF_UP).doubleValue());
                            expectedResult = totalHorizontalOHE + DcrConstants.IN_METER;
                            if (electricalLine.getHorizontalDistance().compareTo(BigDecimal.valueOf(totalHorizontalOHE)) >= 0) {
                                horizontalDistancePassed = true;
                            }
                        }

                        // Add scrutiny details based on validation results
                        if (horizontalDistancePassed) {
                            setReportOutputDetails(pl, SUB_RULE_31, DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE +
                                            electricalLine.getNumber(), expectedResult, actualResult,
                                    Result.Accepted.getResultVal(), "", electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
                        } else {
                            boolean verticalDistancePassed = processVerticalDistance(electricalLine, pl, "", "",
                                    overheadVoltage_11000, overheadVerticalDistance_11000, overheadVoltage_33000);
                            if (verticalDistancePassed) {
                                setReportOutputDetails(pl, SUB_RULE_31, DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE +
                                                electricalLine.getNumber(), expectedResult, actualResult,
                                        Result.Verify.getResultVal(), String.format(DcrConstants.HORIZONTAL_ELINE_DISTANCE_NOC, electricalLine.getNumber()),
                                        electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
                            } else {
                                setReportOutputDetails(pl, SUB_RULE_31, DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE +
                                                electricalLine.getNumber(), expectedResult, actualResult,
                                        Result.Not_Accepted.getResultVal(), "", electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
                            }

                            // Add NOC if vertical distance passed but horizontal distance failed
                            if (verticalDistancePassed) {
                                HashMap<String, String> noc = new HashMap<>();
                                noc.put(DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(),
                                        DcrConstants.HORIZONTAL_ELECTRICLINE_DISTANCE_NOC);
                                pl.addNocs(noc);
                            }
                        }
                    }
                }
            }
        }
        return pl; // Return the updated plan object
    }

    private boolean processVerticalDistance(ElectricLine electricalLine, Plan pl, String remarks1, String remarks2, BigDecimal overheadVoltage_11000, BigDecimal overheadVerticalDistance_11000, BigDecimal overheadVoltage_33000) {

        boolean verticalDistancePassed = false;

        if (electricalLine.getVerticalDistance() != null) {
            String actualResult = electricalLine.getVerticalDistance().toString() + DcrConstants.IN_METER;
            String expectedResult = "";

            if (electricalLine.getVoltage().compareTo(overheadVoltage_11000) < 0) {

                expectedResult = overheadVerticalDistance_11000.toString() + DcrConstants.IN_METER;
                if (electricalLine.getVerticalDistance().compareTo(overheadVerticalDistance_11000) >= 0)
                    verticalDistancePassed = true;

            } else if (electricalLine.getVoltage().compareTo(overheadVoltage_11000) >= 0
                    && electricalLine.getVoltage().compareTo(overheadVoltage_33000) <= 0) {

                expectedResult = overheadVerticalDistance_33000.toString() + DcrConstants.IN_METER;
                if (electricalLine.getVerticalDistance().compareTo(overheadVerticalDistance_33000) >= 0)
                    verticalDistancePassed = true;

            } else if (electricalLine.getVoltage().compareTo(overheadVoltage_33000) > 0) {

                Double totalVertficalOHE = overheadVerticalDistance_33000.doubleValue() + 0.3 *
                        Math.ceil(
                                electricalLine.getVoltage().subtract(overheadVoltage_33000)
                                        .divide(overheadVoltage_33000, 2, RoundingMode.HALF_UP)
                                        .doubleValue());
                expectedResult = totalVertficalOHE + DcrConstants.IN_METER;
                if (electricalLine.getVerticalDistance()
                        .compareTo(BigDecimal.valueOf(totalVertficalOHE)) >= 0) {
                    verticalDistancePassed = true;
                }
            }
            if (verticalDistancePassed) {
                setReportOutputDetails(pl, SUB_RULE_31, DcrConstants.VERTICAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(), expectedResult,
                        actualResult, Result.Accepted.getResultVal(), remarks1, electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
            } else {
                setReportOutputDetails(pl, SUB_RULE_31, DcrConstants.VERTICAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(), expectedResult,
                        actualResult, Result.Not_Accepted.getResultVal(), remarks2, electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
            }

        }
        return verticalDistancePassed;
    }

    private boolean processVerticalDistance(ElectricLine electricalLine, PlanDetail planDetail, String remarks1, String remarks2) {

        boolean verticalDistancePassed = false;

        if (electricalLine.getVerticalDistance() != null) {
            String actualResult = electricalLine.getVerticalDistance().toString() + DcrConstants.IN_METER;
            String expectedResult = "";

            if (electricalLine.getVoltage().compareTo(overheadVoltage_11000) < 0) {

                expectedResult = overheadVerticalDistance_11000.toString() + DcrConstants.IN_METER;
                if (electricalLine.getVerticalDistance().compareTo(overheadVerticalDistance_11000) >= 0)
                    verticalDistancePassed = true;

            } else if (electricalLine.getVoltage().compareTo(overheadVoltage_11000) >= 0
                    && electricalLine.getVoltage().compareTo(overheadVoltage_33000) <= 0) {

                expectedResult = overheadVerticalDistance_33000.toString() + DcrConstants.IN_METER;
                if (electricalLine.getVerticalDistance().compareTo(overheadVerticalDistance_33000) >= 0)
                    verticalDistancePassed = true;

            } else if (electricalLine.getVoltage().compareTo(overheadVoltage_33000) > 0) {

                Double totalVertficalOHE = overheadVerticalDistance_33000.doubleValue() + 0.3 *
                        Math.ceil(
                                electricalLine.getVoltage().subtract(overheadVoltage_33000)
                                        .divide(overheadVoltage_33000, 2, RoundingMode.HALF_UP)
                                        .doubleValue());
                expectedResult = totalVertficalOHE + DcrConstants.IN_METER;
                if (electricalLine.getVerticalDistance()
                        .compareTo(BigDecimal.valueOf(totalVertficalOHE)) >= 0) {
                    verticalDistancePassed = true;
                }
            }
            if (verticalDistancePassed) {
                setReportOutputDetails(planDetail, SUB_RULE_31, DcrConstants.VERTICAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(), expectedResult,
                        actualResult, Result.Accepted.getResultVal(), remarks1, electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
            } else {
                setReportOutputDetails(planDetail, SUB_RULE_31, DcrConstants.VERTICAL_ELECTRICLINE_DISTANCE + electricalLine.getNumber(), expectedResult,
                        actualResult, Result.Not_Accepted.getResultVal(), remarks2, electricalLine.getVoltage().toString() + DcrConstants.IN_KV);
            }

        }
        return verticalDistancePassed;
    }

    private void setReportOutputDetails(Plan pl, String ruleNo, String ruleDesc, String expected, String actual, String status, String remarks, String voltage) {
        Map<String, String> details = new HashMap<>();
        details.put(RULE_NO, ruleNo);
        details.put(DESCRIPTION, ruleDesc);
        details.put(REQUIRED, expected);
        details.put(PROVIDED, actual);
        details.put(REMARKS, remarks);
        details.put(VOLTAGE, voltage);
        details.put(STATUS, status);
        scrutinyDetail.getDetail().add(details);
        pl.getReportOutput().getScrutinyDetails().add(scrutinyDetail);
    }

    @Override
    public Map<String, Date> getAmendments() {
        return new LinkedHashMap<>();
    }

}
